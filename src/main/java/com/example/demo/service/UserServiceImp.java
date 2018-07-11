package com.example.demo.service;

import com.example.demo.entity.ClassRoom;
import com.example.demo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImp implements UserService {

    @Autowired
    @Qualifier("primaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ClassRoom> findAllClassRoom() {
        String sql = "select * from class_room";

        List<ClassRoom> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ClassRoom.class));

        return list;
    }

    @Override
    public User findUserById(int user_id) {
        String sql = "select c.user_id AS user_id, c.username AS username, c.classroom AS classroom FROM (SELECT a.user_id AS user_id, a.username AS username, b.c_name AS classroom FROM my_local_db.user a, my_local_db.class_room b where a.c_id = b.c_id) c where user_id = ?";
        User user = jdbcTemplate.queryForObject(sql, new Object[]{user_id}, new BeanPropertyRowMapper<>(User.class));
        return user;
    }

    @Override
    public User findUser(String username, String password) {
        String sql = "        SELECT \n" +
                "    c.user_id AS user_id,\n" +
                "    c.username AS username,\n" +
                "    c.classroom AS classroom\n" +
                "FROM\n" +
                "    (SELECT \n" +
                "        a.user_id AS user_id,\n" +
                "            a.username AS username,\n" +
                "            b.c_name AS classroom,\n" +
                "            a.password AS password\n" +
                "    FROM\n" +
                "        my_local_db.user a, my_local_db.class_room b\n" +
                "    WHERE\n" +
                "        a.c_id = b.c_id) c\n" +
                "WHERE\n" +
                "    c.username = ? \n" +
                "        AND c.password = ?;";
        List<User> res = jdbcTemplate.query(sql, new Object[]{username, password}, new BeanPropertyRowMapper<>(User.class));
        return res.size() > 0 ? res.get(0) : null;
    }

    @Override
    public int register(String username, String password) {
        String sql = "insert into my_local_db.user values(NULL,?, ?, 0);";
        int rows = jdbcTemplate.update(sql, username, password);
        return rows;
    }

}
