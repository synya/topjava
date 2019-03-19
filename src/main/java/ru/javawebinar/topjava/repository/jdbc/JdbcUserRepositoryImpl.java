package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepositoryImpl implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
            insertRoles(user.getRoles(), user.getId());
        } else {
            if (namedParameterJdbcTemplate.update(
                    "UPDATE users SET name=:name, email=:email, password=:password, " +
                            "registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay " +
                            "WHERE id=:id", parameterSource) == 0) {
                return null;
            }
            deleteRoles(user.getId());
            insertRoles(user.getRoles(), user.getId());
        }
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users u " +
                "LEFT JOIN user_roles ur ON ur.user_id = u.id " +
                "WHERE id = ?", ROW_MAPPER, id);
        return DataAccessUtils.singleResult(mergeUserRoles(users));
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users u " +
                "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                "WHERE email=?", ROW_MAPPER, email);
        return DataAccessUtils.singleResult(mergeUserRoles(users));
    }

    @Override
    public List<User> getAll() {
        return mergeUserRoles(jdbcTemplate.query("SELECT * FROM users u " +
                "LEFT JOIN user_roles ur ON u.id = ur.user_id " +
                "ORDER BY name, email", ROW_MAPPER));
    }

    private List<User> mergeUserRoles(List<User> users) {
        Map<Integer, User> userMap = new LinkedHashMap<>();
        users.forEach(user -> userMap.merge(user.getId(), user, (oldUser, newUser) -> {
            Set<Role> roles = oldUser.getRoles();
            roles.addAll(newUser.getRoles());
            oldUser.setRoles(roles);
            return oldUser;
        }));
        return new ArrayList<>(userMap.values());
    }

    private int[] insertRoles(Set<Role> roles, Integer userId) {
        List<Role> roleList = new ArrayList<>(roles);
        return jdbcTemplate.batchUpdate(
                "INSERT INTO user_roles (user_id, role) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, userId);
                        ps.setString(2, roleList.get(i).name());
                    }

                    public int getBatchSize() {
                        return roleList.size();
                    }
                });
    }

    private void deleteRoles(Integer userId) {
        jdbcTemplate.update("DELETE FROM user_roles ur WHERE ur.user_id = ?", userId);
    }
}
