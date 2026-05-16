package com.example.academic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.academic.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
