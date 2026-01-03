package org.cnhnwt.httpexchange.server.model;

/**
 * 创建用户请求
 * 
 * <p>用于创建新用户时提交的数据</p>
 */
public class CreateUserRequest {
    
    /**
     * 用户名，必填，长度3-50个字符
     */
    private String username;
    
    /**
     * 邮箱地址，必填，需符合邮箱格式
     */
    private String email;
    
    /**
     * 用户真实姓名，可选
     */
    private String fullName;
    
    public CreateUserRequest() {
    }
    
    public CreateUserRequest(String username, String email, String fullName) {
        this.username = username;
        this.email = email;
        this.fullName = fullName;
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
}