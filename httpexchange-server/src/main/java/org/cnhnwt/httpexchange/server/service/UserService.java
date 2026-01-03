package org.cnhnwt.httpexchange.server.service;

import org.cnhnwt.httpexchange.server.model.CreateUserRequest;
import org.cnhnwt.httpexchange.server.model.UpdateUserRequest;
import org.cnhnwt.httpexchange.server.model.User;

import java.util.List;
import java.util.Optional;

/**
 * 用户服务接口
 * 
 * <p>定义用户管理的核心业务操作</p>
 */
public interface UserService {
    
    /**
     * 获取所有用户列表
     * 
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 根据ID查找用户
     * 
     * @param id 用户ID
     * @return 用户信息，如果不存在则返回空
     */
    Optional<User> findById(Long id);
    
    /**
     * 创建新用户
     * 
     * @param request 创建用户请求
     * @return 创建成功的用户信息
     */
    User create(CreateUserRequest request);
    
    /**
     * 更新用户信息
     * 
     * @param id 用户ID
     * @param request 更新用户请求
     * @return 更新后的用户信息，如果用户不存在则返回空
     */
    Optional<User> update(Long id, UpdateUserRequest request);
    
    /**
     * 删除用户
     * 
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean delete(Long id);
}