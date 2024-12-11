package com.ecommerce.demo.config;

import com.hazelcast.config.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public Config configure() {
        return new Config().setInstanceName("hazelcast-instance")
                .addMapConfig(new MapConfig()
                        .setName("orders-cache")
                        .setTimeToLiveSeconds(3600) // Cache TTL in seconds
                        .setMaxIdleSeconds(600)
                        .setEvictionConfig(
                                new EvictionConfig()
                                        .setSize(200)
                                        .setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_SIZE)
                                        .setEvictionPolicy(EvictionPolicy.LRU)
                        )
                        .setTimeToLiveSeconds(2000));
    }

}
