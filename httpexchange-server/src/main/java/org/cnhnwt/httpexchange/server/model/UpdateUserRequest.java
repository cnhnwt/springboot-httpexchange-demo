package org.cnhnwt.httpexchange.server.model;

/**
 * 更新用户请求
 * 
 * <p>用于更新用户信息时提交的数据</p>
 */
public class UpdateUserRequest {
    
    /**
     * 用户名，可选，长度3-50个字符
     */
    private String username;
    
    /**
     * 邮箱地址，可选，需符合邮箱格式
     */
    private String email;
    
    /**
     * 用户真实姓名，可选
     */
    private String fullName;
    
    /**
     * 用户状态：ACTIVE-活跃，INACTIVE-未激活，DISABLED-已禁用
     */
    private String status;
    
    public UpdateUserRequest() {
    }
    
    public UpdateUserRequest(String username, String email, String fullName, String status) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.status = status;
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
}