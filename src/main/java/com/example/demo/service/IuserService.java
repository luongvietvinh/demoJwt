package com.example.demo.service;


import java.util.List;
import java.util.Optional;
import com.example.demo.entity.Users;

public interface IuserService {
  
    Users saveUser(Users user);
    
    List<Users> getAllUsers(int page, int size);
    
    Optional<Users> getUserById(String id);
    
    void deleteUser(String id);
    
    Users updateUser(Users user);
}
