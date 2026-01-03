package org.cnhnwt.httpexchange.server.service.impl;

import org.cnhnwt.httpexchange.server.model.CreateUserRequest;
import org.cnhnwt.httpexchange.server.model.UpdateUserRequest;
import org.cnhnwt.httpexchange.server.model.User;
import org.cnhnwt.httpexchange.server.service.UserService;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 用户服务实现类
 * 
 * <p>使用内存存储实现用户管理功能</p>
 */
@Service
public class UserServiceImpl implements UserService {
    
    private final Map<Long, User> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public UserServiceImpl() {
        // 初始化一些测试数据
        User user1 = new User(idGenerator.getAndIncrement(), "zhangsan", "zhangsan@example.com", "张三", "ACTIVE");
        User user2 = new User(idGenerator.getAndIncrement(), "lisi", "lisi@example.com", "李四", "ACTIVE");
        User user3 = new User(idGenerator.getAndIncrement(), "wangwu", "wangwu@example.com", "王五", "INACTIVE");
        
        userStore.put(user1.getId(), user1);
        userStore.put(user2.getId(), user2);
        userStore.put(user3.getId(), user3);
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(userStore.values());
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userStore.get(id));
    }
    
    @Override
    public User create(CreateUserRequest request) {
        User user = new User();
        user.setId(idGenerator.getAndIncrement());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setStatus("ACTIVE");
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        
        userStore.put(user.getId(), user);
        return user;
    }
    
    @Override
    public Optional<User> update(Long id, UpdateUserRequest request) {
        User existingUser = userStore.get(id);
        if (existingUser == null) {
            return Optional.empty();
        }
        
        if (request.getUsername() != null) {
            existingUser.setUsername(request.getUsername());
        }
        if (request.getEmail() != null) {
            existingUser.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            existingUser.setFullName(request.getFullName());
        }
        if (request.getStatus() != null) {
            existingUser.setStatus(request.getStatus());
        }
        existingUser.setUpdatedAt(OffsetDateTime.now());
        
        return Optional.of(existingUser);
    }
    
    @Override
    public boolean delete(Long id) {
        return userStore.remove(id) != null;
    }
}