package com.fin.like.repository;

import com.fin.like.dto.UserDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //查詢所有使用者
    public List<UserDto> findAll() {
        String sql = "SELECT user_id, user_name, email, account, created_at, updated_at FROM users ORDER BY user_id";
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

    //查詢特定使用者是否存在
    public boolean existsById(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    private static class UserRowMapper implements RowMapper<UserDto> {
        @Override
        public UserDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            UserDto dto = new UserDto();
            dto.setUserId(rs.getString("user_id"));
            dto.setUserName(rs.getString("user_name"));
            dto.setEmail(rs.getString("email"));
            dto.setAccount(rs.getString("account"));
            dto.setCreatedAt(rs.getTimestamp("created_at") != null
                    ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            dto.setUpdatedAt(rs.getTimestamp("updated_at") != null
                    ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
            return dto;
        }
    }
}
