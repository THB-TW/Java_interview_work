package com.fin.like.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserCredentialsRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserCredentialsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 依登入帳號查詢雜湊密碼與對應的 userId */
    public Optional<String[]> findByUsername(String username) {
        String sql = "SELECT user_id, password FROM user_credentials WHERE username = ?";
        return jdbcTemplate.query(sql, rs -> {
            if (rs.next()) {
                return Optional.of(new String[]{rs.getString("user_id"), rs.getString("password")});
            }
            return Optional.empty();
        }, username);
    }

    /** 新增帳號密碼（申請帳號時使用） */
    public void insert(String userId, String username, String hashedPassword) {
        String sql = "INSERT INTO user_credentials (user_id, username, password) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, username, hashedPassword);
    }

    /** 確認 username 是否已存在 */
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM user_credentials WHERE username = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, username);
        return count != null && count > 0;
    }
}
