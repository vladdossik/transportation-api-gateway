package transportation.config;

import java.time.Duration;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.validation.Valid;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

  @Bean
  public CacheManager ehCacheManager(
      @Value("${cache.heap_in_mb}") long cacheHeapSize,
      @Value("${cache.ttl_in_minutes}") long timeToLive) {
    CachingProvider provider = Caching.getCachingProvider();
    CacheManager cacheManager = provider.getCacheManager();

    cacheManager.createCache(
        "cache",
        Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(
                    String.class,
                    String.class, // dto to store
                    ResourcePoolsBuilder.newResourcePoolsBuilder()
                        .heap(cacheHeapSize, MemoryUnit.MB)
                )
                .withExpiry(
                    ExpiryPolicyBuilder.timeToIdleExpiration(
                        Duration.ofMinutes(timeToLive)
                    )
                )
        )
    );

    return cacheManager;
  }
}
