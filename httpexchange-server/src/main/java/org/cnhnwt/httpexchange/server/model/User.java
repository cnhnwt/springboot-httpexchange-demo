package org.cnhnwt.httpexchange.server.model;

import java.time.OffsetDateTime;

/**
 * 用户实体类
 *
 * <p>表示系统中的用户信息</p>
 */
public class User {
    
    /**
     * 用户唯一标识
     */
    private Long id;
    
    /**
     * 用户名，用于登录和显示
     */
    private String username;
    
    /**
     * 用户邮箱地址
     */
    private String email;
    
    /**
     * 用户真实姓名
     */
    private String fullName;
    
    /**
     * 用户状态：ACTIVE-活跃，INACTIVE-未激活，DISABLED-已禁用
     */
    private String status;
    
    /**
     * 用户创建时间
     */
    private OffsetDateTime createdAt;
    
    /**
     * 用户信息最后更新时间
     */
    private OffsetDateTime updatedAt;
    
    public User() {
    }
    
    public User(Long id, String username, String email, String fullName, String status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.status = status;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}