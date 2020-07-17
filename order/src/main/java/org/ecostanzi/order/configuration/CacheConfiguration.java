package org.ecostanzi.order.configuration;

import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionType;
import org.infinispan.spring.starter.embedded.InfinispanGlobalConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
@Profile("k8s")
public class CacheConfiguration {

    @Bean
    public InfinispanGlobalConfigurer globalConfiguration() throws UnknownHostException {
        return () -> GlobalConfigurationBuilder
                .defaultClusteredBuilder()
                .transport().addProperty("configurationFile", "default-jgroups-kubernetes.xml")
                .build();
    }

    @Bean(name = "dist-cache")
    public org.infinispan.configuration.cache.Configuration smallCache() {
        return new ConfigurationBuilder()
                .clustering().cacheMode(CacheMode.DIST_SYNC)
                .expiration()
                    .maxIdle(15, TimeUnit.SECONDS)
                .build();
    }
}
