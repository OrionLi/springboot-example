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
     */
    default User getUserById(Long userId) {
        //mybatis-plus 的 selectOne 只是返回查询到的列表第一个用户，详细自己看源码
        //所以我这 limit 1 了
        //自己需要啥才查啥，不要 select * 。感兴趣的去看MySQL优化
        return selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getId, userId)
                        .select(User::getId, User::getUsername, User::getPhoneNumber, User::getSex)
                        .last("limit 1")
        );
    }

}




