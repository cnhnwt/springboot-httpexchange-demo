package org.cnhnwt.httpexchange.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HttpExchange Server 应用程序入口
 * 
 * <p>提供用户管理的核心接口服务，支持 OpenAPI 文档生成</p>
 */
@SpringBootApplication
public class HttpExchangeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpExchangeServerApplication.class, args);
    }
}