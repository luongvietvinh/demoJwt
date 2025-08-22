package com.example.demo.service;


import java.util.List;
import java.util.Optional;
import com.example.demo.entity.Users;
import com.example.demo.request.RequestDto;

public interface IuserService {
  
    Users saveUser(RequestDto request);
    
    List<Users> getAllUsers(int page, int size);
    
    Optional<Users> getUserById(String id);
    
    void deleteUser(String id);
    
    Users updateUser(Users user);
}
