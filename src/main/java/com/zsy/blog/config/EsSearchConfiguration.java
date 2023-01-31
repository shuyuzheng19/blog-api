package com.zsy.blog.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author 郑书宇
 * @create 2022/8/3 22:28
 * @desc
 */

@Configuration
public class EsSearchConfiguration {

    @Value("${web.hostname}")
    private String ip;

    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient Client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(ip,9200,"http"))
        );
        return Client;
    }
}
