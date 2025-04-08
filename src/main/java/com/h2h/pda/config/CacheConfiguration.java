package com.h2h.pda.config;

import com.h2h.pda.pojo.group.GroupCounter;
import com.h2h.pda.pojo.group.UserGroupCounter;
import com.h2h.pda.pojo.service.ServiceCounter;
import com.h2h.pda.pojo.service.UserServiceCounter;
import com.h2h.pda.pojo.vault.CredentialCounter;
import com.h2h.pda.pojo.vault.CredentialRequestCounter;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import java.time.Duration;

import static com.h2h.pda.config.CacheNames.*;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public CacheManager ehCacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        CacheConfigurationBuilder<String, GroupCounter> groupCounterConfiguration =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class,
                                GroupCounter.class,
                                ResourcePoolsBuilder
                                        .newResourcePoolsBuilder().offheap(1, MemoryUnit.MB))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)));

        cacheManager.createCache(GROUP_COUNTER, Eh107Configuration.fromEhcacheCacheConfiguration(groupCounterConfiguration));

        CacheConfigurationBuilder<String, UserGroupCounter> userGroupCounterConfiguration =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class,
                                UserGroupCounter.class,
                                ResourcePoolsBuilder
                                        .newResourcePoolsBuilder().offheap(1, MemoryUnit.MB))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)));


        cacheManager.createCache(GROUP_USER_COUNTER, Eh107Configuration.fromEhcacheCacheConfiguration(userGroupCounterConfiguration));

        CacheConfigurationBuilder<String, ServiceCounter> serviceCounterConfiguration =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class,
                                ServiceCounter.class,
                                ResourcePoolsBuilder
                                        .newResourcePoolsBuilder().offheap(1, MemoryUnit.MB))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)));

        cacheManager.createCache(SERVICE_COUNTER, Eh107Configuration.fromEhcacheCacheConfiguration(serviceCounterConfiguration));

        CacheConfigurationBuilder<String, UserServiceCounter> userServiceCounterConfiguration =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String.class,
                                UserServiceCounter.class,
                                ResourcePoolsBuilder
                                        .newResourcePoolsBuilder().offheap(1, MemoryUnit.MB))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)));

        cacheManager.createCache(SERVICE_USER_COUNTER, Eh107Configuration.fromEhcacheCacheConfiguration(userServiceCounterConfiguration));

        CacheConfigurationBuilder<String, CredentialCounter> vaultCounterConfiguration =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        String.class,
                        CredentialCounter.class,
                        ResourcePoolsBuilder
                                .newResourcePoolsBuilder().offheap(1, MemoryUnit.MB))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)));

        cacheManager.createCache(VAULT_COUNTER, Eh107Configuration.fromEhcacheCacheConfiguration(vaultCounterConfiguration));

        CacheConfigurationBuilder<String, CredentialRequestCounter> credentialRequestCounterConfiguration =
                CacheConfigurationBuilder.newCacheConfigurationBuilder(
                        String.class,
                        CredentialRequestCounter.class,
                        ResourcePoolsBuilder
                                .newResourcePoolsBuilder().offheap(1, MemoryUnit.MB))
                        .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(20)));

        cacheManager.createCache(CREDENTIAL_REQUEST_COUNTER, Eh107Configuration.fromEhcacheCacheConfiguration(credentialRequestCounterConfiguration));

        return cacheManager;
    }
}