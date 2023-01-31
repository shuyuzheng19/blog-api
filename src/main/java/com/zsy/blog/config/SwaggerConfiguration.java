package com.zsy.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author 郑书宇
 * @create 2023/1/16 15:15
 * @desc Swagger配置
 */
@Configuration
@EnableOpenApi
public class SwaggerConfiguration {

    public static final String AUTHOR_NAME="郑书宇";

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                // 指定构建api文档的详细信息的方法：apiInfo()
                .apiInfo(apiInfo())
                .select()
                // 指定要生成api接口的包路径
                .apis(RequestHandlerSelectors.basePackage("com.zsy.blog"))
                .paths(PathSelectors.any())
                //可以根据url路径设置哪些请求加入文档，忽略哪些请求
                .build();
    }

    /**
     * 设置api文档的详细信息
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                // 标题
                .title("ZSY-BLOG文档")
                // 接口描述
                .description("ZSY-BLOG文档接口")
                // 联系方式
                .contact(new Contact(AUTHOR_NAME,"http://www.sqeyjh.com","shuyuzheng19@gmail.com"))
                // 版本信息
                .version("1.0")
                // 构建
                .build();
    }

}
