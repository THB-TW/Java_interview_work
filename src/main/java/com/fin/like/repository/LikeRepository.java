package com.fin.like.repository;

import com.fin.like.dto.LikeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LikeRepository {

    private static final Logger log = LoggerFactory.getLogger(LikeRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public LikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //呼叫 sp_add_like 新增
    public void addLike(String userId, Long productNo, Integer purchaseQuantity, String account) {
        String sql = "CALL sp_add_like(?, ?, ?, ?)";
        log.debug("Calling sp_add_like: userId={}, productNo={}, pqt={}", userId, productNo, purchaseQuantity);
        jdbcTemplate.update(sql, userId, productNo, purchaseQuantity, account);
    }

    //呼叫 sp_get_like_list 查詢（原有功能保留）
    public List<LikeResponse> getLikeList(String userId) {
        String sql = "CALL sp_get_like_list(?)";
        log.debug("Calling sp_get_like_list: userId={}", userId);
        return jdbcTemplate.query(sql, new LikeRowMapper(), userId);
    }

    //呼叫 sp_update_like 修改
    public void updateLike(Long sn, String userId, Long productNo, Integer purchaseQuantity, String account) {
        String sql = "CALL sp_update_like(?, ?, ?, ?, ?)";
        log.debug("Calling sp_update_like: sn={}, userId={}, productNo={}", sn, userId, productNo);
        jdbcTemplate.update(sql, sn, userId, productNo, purchaseQuantity, account);
    }

    //呼叫 sp_delete_like 刪除
    public void deleteLike(Long sn, String userId) {
        String sql = "CALL sp_delete_like(?, ?)";
        log.debug("Calling sp_delete_like: sn={}, userId={}", sn, userId);
        jdbcTemplate.update(sql, sn, userId);
    }

    //查詢特定 sn 是否存在且屬於該 userId(用於修改/刪除前的驗證)
    public boolean existsSnAndUserId(Long sn, String userId) {
        String sql = "SELECT COUNT(*) FROM like_list WHERE sn = ? AND user_id = ?";
        //Integer.class轉成int
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, sn, userId);
        return count != null && count > 0;
    }

    /**
     * 分頁查詢喜好清單（含篩選、JOIN users 取得 email）
     *
     * @param userId    必填
     * @param productNo 可選篩選（商品編號）
     * @param account   可選篩選（扣款帳號，模糊搜尋）
     * @param page      0-based 頁碼
     * @param size      每頁筆數
     */
    public List<LikeResponse> getLikesPaged(String userId, Long productNo, String account, int page, int size) {
        StringBuilder sql = new StringBuilder(
            "SELECT l.sn, l.product_no, l.purchase_quantity, l.account, " +
            "l.total_fee, l.total_amount, u.email " +
            "FROM like_list l " +
            "JOIN users u ON l.user_id = u.user_id " +
            "WHERE l.user_id = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (productNo != null && productNo > 0) {
            sql.append("AND l.product_no = ? ");
            params.add(productNo);
        }
        if (account != null && !account.trim().isEmpty()) {
            sql.append("AND l.account LIKE ? ");
            params.add("%" + account.trim() + "%");
        }

        sql.append("ORDER BY l.sn LIMIT ? OFFSET ?");
        params.add(size);
        params.add((long) page * size);

        log.debug("getLikesPaged: userId={}, productNo={}, account={}, page={}, size={}", userId, productNo, account, page, size);
        return jdbcTemplate.query(sql.toString(), new LikePagedRowMapper(), params.toArray());
    }

    /** 計算分頁查詢的總筆數 */
    public long countLikes(String userId, Long productNo, String account) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM like_list l WHERE l.user_id = ? "
        );
        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (productNo != null && productNo > 0) {
            sql.append("AND l.product_no = ? ");
            params.add(productNo);
        }
        if (account != null && !account.trim().isEmpty()) {
            sql.append("AND l.account LIKE ? ");
            params.add("%" + account.trim() + "%");
        }

        Long count = jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
        return count != null ? count : 0L;
    }

    // RowMapper for sp_get_like_list（原有 SP 回傳，無 email）
    private static class LikeRowMapper implements RowMapper<LikeResponse> {
        @Override
        public LikeResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            LikeResponse dto = new LikeResponse();
            dto.setSn(rs.getLong("sn"));
            dto.setProductNo(rs.getLong("product_no"));
            dto.setPurchaseQuantity(rs.getInt("purchase_quantity"));
            dto.setAccount(rs.getString("account"));
            dto.setTotalFee(rs.getBigDecimal("total_fee"));
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            return dto;
        }
    }

    // RowMapper for 分頁查詢（含 email JOIN）
    private static class LikePagedRowMapper implements RowMapper<LikeResponse> {
        @Override
        public LikeResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            LikeResponse dto = new LikeResponse();
            dto.setSn(rs.getLong("sn"));
            dto.setProductNo(rs.getLong("product_no"));
            dto.setPurchaseQuantity(rs.getInt("purchase_quantity"));
            dto.setAccount(rs.getString("account"));
            dto.setTotalFee(rs.getBigDecimal("total_fee"));
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            dto.setEmail(rs.getString("email"));
            return dto;
        }
    }
}
