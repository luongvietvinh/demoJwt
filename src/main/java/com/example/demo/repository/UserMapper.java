package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.demo.entity.RoleEntity;
import com.example.demo.entity.Users;

@Mapper
public interface UserMapper {

    int insert(Users user);
    int insertRole(RoleEntity role);

    List<Users> findAll(@Param("limit") int limit, @Param("offset") int offset);

    Users findById(String userId);

    void delete(String userId);

    void update(Users user);
    
    Optional<Users> findByUserName(String userName);
    
    boolean existsByUserName(String userName);
}
