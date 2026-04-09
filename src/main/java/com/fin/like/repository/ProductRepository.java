package com.fin.like.repository;

import com.fin.like.dto.ProductDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //查詢所有金融商品(供前端下拉選單使用)
    public List<ProductDto> findAll() {
        String sql = """
                SELECT product_no, product_name, price, fee_rate, created_at, updated_at
                FROM products
                ORDER BY product_no
                """;
        return jdbcTemplate.query(sql, new ProductRowMapper());
    }

    //查詢特定金融商品是否存在
    public boolean existsById(String productNo) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_no = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, productNo);
        return count != null && count > 0;
    }

    //查詢金融商品
    public ProductDto findByName(String product_name) {
        String sql = """
                SELECT product_no, product_name, price, fee_rate, created_at, updated_at
                FROM products
                WHERE product_name = ?
                """;
        List<ProductDto> result = jdbcTemplate.query(sql, new ProductRowMapper(), product_name);
        return result.isEmpty() ? null : result.get(0);
    }

    private static class ProductRowMapper implements RowMapper<ProductDto> {
        @Override
        public ProductDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            ProductDto dto = new ProductDto();
            dto.setProductNo(rs.getLong("product_no"));
            dto.setProductName(rs.getString("product_name"));
            dto.setPrice(rs.getBigDecimal("price"));
            dto.setFeeRate(rs.getBigDecimal("fee_rate"));
            dto.setCreatedAt(
                    rs.getTimestamp("created_at") != null
                            ? rs.getTimestamp("created_at").toLocalDateTime()
                            : null
            );
            dto.setUpdatedAt(
                    rs.getTimestamp("updated_at") != null
                            ? rs.getTimestamp("updated_at").toLocalDateTime()
                            : null
            );
            return dto;
        }
    }
}