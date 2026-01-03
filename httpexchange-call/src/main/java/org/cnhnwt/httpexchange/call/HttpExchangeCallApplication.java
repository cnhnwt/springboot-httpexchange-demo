package org.cnhnwt.httpexchange.call;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * HttpExchange Call 应用程序入口
 * 
 * <p>业务聚合层，通过 HttpExchange 技术调用 Server 服务</p>
 */
@SpringBootApplication
public class HttpExchangeCallApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpExchangeCallApplication.class, args);
    }
}