package org.cnhnwt.httpexchange.server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 配置类
 * 
 * <p>配置 Swagger/OpenAPI 文档的基本信息</p>
 */
@Configuration
public class OpenApiConfig {
    
    @Value("${server.port:8181}")
    private int serverPort;
    
    /**
     * 配置 OpenAPI 文档信息
     * 
     * @return OpenAPI 配置对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("用户管理服务 API")
                        .description("HttpExchange Server - 提供用户管理的核心接口服务")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("开发团队")
                                .email("dev@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("本地开发服务器")
                ));
    }
}