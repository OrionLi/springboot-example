package io.github.orionli.springbootexample.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.orionli.springbootexample.domain.User;

/**
 * @author 84185
 * @description 针对表【user】的数据库操作Mapper
 * @createDate 2024-04-21 15:04:15
 * @Entity io.github.orionli.springbootexample.domain.User
 */
public interface UserDao extends BaseMapper<User> {

    /**
     * 获取用户信息
     *
     * @param userId 用户id
     * @return 用户
     * @implNote 使用 mybatis-plus 的 selectOne 方法查询用户，该方法返回查询到的第一个用户。
     * 尽管在用户名上加了唯一索引，这里仍然使用 limit 1，以示例为目的。
     * 根据需要选择性地查询字段，避免使用 select *，以提高查询效率。
     * 感兴趣的同学可以研究MySQL查询优化。
     */
    default User getUserById(Long userId) {
        return selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getId, userId)
                        .select(User::getId, User::getUsername, User::getPhoneNumber, User::getSex)
                        .last("limit 1")
        );
    }


    /**
     * 根据用户名获取用户ID和密码
     * 此方法用于查询指定用户名的用户记录，并返回该用户的ID和密码
     * 通过限制查询结果为一条记录，确保只返回一个用户的ID和密码信息
     *
     * @param username 用户名，用于查询用户记录的唯一标识
     * @return User对象，包含查询到的用户ID和密码如果未找到匹配的用户，则返回null
     */
    default User getIdAndPasswordByUsername(String username) {
        return selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getUsername, username)
                        .select(User::getId, User::getPassword)
                        .last("limit 1")
        );
    }

}




