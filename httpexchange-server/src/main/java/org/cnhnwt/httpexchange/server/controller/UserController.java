package org.cnhnwt.httpexchange.server.controller;

import org.cnhnwt.httpexchange.server.model.CreateUserRequest;
import org.cnhnwt.httpexchange.server.model.UpdateUserRequest;
import org.cnhnwt.httpexchange.server.model.User;
import org.cnhnwt.httpexchange.server.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 * 
 * <p>提供用户的增删改查 RESTful API 接口</p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 获取所有用户列表
     * 
     * @return 用户列表
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    
    /**
     * 根据ID获取用户详情
     * 
     * @param id 用户唯一标识
     * @return 用户详情，如果不存在返回404
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 创建新用户
     * 
     * @param request 创建用户请求，包含用户名、邮箱等信息
     * @return 创建成功的用户信息
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User user = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    
    /**
     * 更新用户信息
     * 
     * @param id 用户唯一标识
     * @param request 更新用户请求，包含需要更新的字段
     * @return 更新后的用户信息，如果用户不存在返回404
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") Long id,
            @RequestBody UpdateUserRequest request) {
        return userService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 删除用户
     * 
     * @param id 用户唯一标识
     * @return 删除成功返回204，用户不存在返回404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}