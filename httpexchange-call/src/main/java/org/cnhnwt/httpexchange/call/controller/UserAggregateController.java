package org.cnhnwt.httpexchange.call.controller;

import org.cnhnwt.client.httpexchange.server.api.UserControllerApi;
import org.cnhnwt.client.httpexchange.server.model.CreateUserRequest;
import org.cnhnwt.client.httpexchange.server.model.UpdateUserRequest;
import org.cnhnwt.client.httpexchange.server.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户聚合控制器
 * 
 * <p>作为业务聚合层，通过自动注入的 HttpExchange 客户端调用 httpexchange-server 的用户服务</p>
 * 
 * <p>使用 Programmatic Registration 自动注入 API 客户端，支持懒加载</p>
 * 
 * <p>配置项：clients.httpexchange-server.base-url</p>
 * <p>自动注入配置：clients.httpexchange-server.auto-register-apis=true（默认启用）</p>
 */
@RestController
@RequestMapping("/aggregate/users")
public class UserAggregateController {

    @Autowired
    private UserControllerApi userClient;
    
//    /**
//     * 构造函数注入
//     *
//     * <p>直接注入 UserControllerApi，无需手动创建</p>
//     * <p>客户端在首次使用时才会被创建（懒加载）</p>
//     *
//     * @param userClient 自动注入的 UserControllerApi 客户端
//     */
//    public UserAggregateController(UserControllerApi userClient) {
//        this.userClient = userClient;
//    }
    
    /**
     * 获取所有用户列表
     * 
     * <p>通过 HttpExchange 调用 httpexchange-server 获取用户列表</p>
     * 
     * @return 用户列表
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return userClient.getAllUsers();
    }
    
    /**
     * 根据ID获取用户详情
     * 
     * <p>通过 HttpExchange 调用 httpexchange-server 获取用户详情</p>
     * 
     * @param id 用户唯一标识
     * @return 用户详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return userClient.getUserById(id);
    }
    
    /**
     * 创建新用户
     * 
     * <p>通过 HttpExchange 调用 httpexchange-server 创建用户</p>
     * 
     * @param request 创建用户请求
     * @return 创建成功的用户信息
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        return userClient.createUser(request);
    }
    
    /**
     * 更新用户信息
     * 
     * <p>通过 HttpExchange 调用 httpexchange-server 更新用户</p>
     * 
     * @param id 用户唯一标识
     * @param request 更新用户请求
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") Long id,
            @RequestBody UpdateUserRequest request) {
        return userClient.updateUser(id, request);
    }
    
    /**
     * 删除用户
     * 
     * <p>通过 HttpExchange 调用 httpexchange-server 删除用户</p>
     * 
     * @param id 用户唯一标识
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id) {
        return userClient.deleteUser(id);
    }
}