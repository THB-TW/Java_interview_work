package com.fin.like.repository;

import com.fin.like.dto.LikeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    //呼叫 sp_get_like_list 查詢
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

    // RowMapper for sp_get_like_list(SP 只回傳 5 個欄位)
    private static class LikeRowMapper implements RowMapper<LikeResponse> {
        //轉換資料對應的DTO
        @Override
        public LikeResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
            LikeResponse dto = new LikeResponse();
            dto.setSn(rs.getLong("sn")); //抓DB值
            dto.setProductNo(rs.getLong("product_no"));
            dto.setPurchaseQuantity(rs.getInt("purchase_quantity"));
            dto.setAccount(rs.getString("account"));
            dto.setTotalFee(rs.getBigDecimal("total_fee"));
            dto.setTotalAmount(rs.getBigDecimal("total_amount"));
            return dto;
        }
    }
}
