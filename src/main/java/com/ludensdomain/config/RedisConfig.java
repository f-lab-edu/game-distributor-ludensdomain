package com.ludensdomain.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.ludensdomain.util.RedisCacheKeyConstants.GAME_LIST;

@EnableCaching
@Configuration
public class RedisConfig {

    @Value("${spring.redis.session.host}")
    private String redisSessionHost;

    @Value("${spring.redis.cache.host}")
    private String redisCacheHost;

    @Value("${spring.redis.session.port}")
    private int redisSessionPort;

    @Value("${spring.redis.cache.port}")
    private int redisCachePort;

    /*
     * Redis에 연결하기 위해 필요한 객체로 thread-safe함
     * thread-safe하다는 건 멀티 쓰레드 환경에서 여러 쓰레드가 하나의 기능에 접근해도 개발자가 의도했던 대로 실행된다는 걸 의미한다.
     * RedisStandaloneConfiguration : RedisConnectionFactory를 통해 RedisConnection을 세팅하기 위한 configuration 객체
     * LettuceConnectionFacotory : Lettuce(Redis client) 기반의 ConnectionFactory 객체
     * Lettuce는 Jedis에 비교해 CPU 활용도와 응답 속도가 월등함으로 LettuceConnectionFactory를 사용함
     */
    @Bean("redisSessionConnectionFactory")
    @Primary
    public RedisConnectionFactory redisSessionConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisSessionHost);
        redisStandaloneConfiguration.setPort(redisSessionPort);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean("redisCacheConnectionFactory")
    public RedisConnectionFactory redisCacheConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisCacheHost);
        redisStandaloneConfiguration.setPort(redisCachePort);

        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * String 자료형 위주로 데이터를 받기 위해 RedisTemplate StringRedisSerializer 사용.
     * GenericJackson2JsonRedisSerializer은 객체를 JSON 방식으로 변환
     *
     * @return redisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisSessionTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisSessionConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public RedisTemplate<String, Object> redisCacheTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisCacheConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    /**.
     * RedisCacheManager : Spring에서 Redis를 통한 cache 기능을 기존 스프링 코드를 최대한 유지시키며 안전하게 적용하기 위해
     * 사용되는 기능이다. 데이터 생존 주기, 캐시 데이터 타입 등 캐시의 여러 설정을 부여할 수 있다.
     * disableCachingNullValues : null값이 들어가지 않도록 설정
     * Serialization : 기존 Redis의 byte array 타입을 특정 데이터 타입으로 받기 위해 설정
     * GenericJackson2JsonRedisSerializer은 모든 Object를 JSON 형태로 변환해주지만 Class Type을 같이 저장시켜서
     * 동일한 Class Type(DTO)만으로 조회가 가능하다는 특징이 있다.
     * cacheConfigurationMap : 캐시 별로 유효시간을 정하기 위해 HashMap 데이터 타입으로 설정
     * entryTtl(Duration) : 캐시의 수명 기간을 정의. 디폴트로 1시간 동안 캐시를 보유하도록 설정.
     */
    @Bean
    public RedisCacheManager redisCacheManager(
            @Qualifier("redisCacheConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext
                        .SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 게임 리스트를 조회하는 경우 게임 평점과 판매 수 같이 실시간적으로 업데이트가 되야 하는 데이터를 다루기 때문에 캐시 생명 주기를 5초로 산정
        Map<String, RedisCacheConfiguration> redisCacheConfigMap = new HashMap<>();
        redisCacheConfigMap.put(GAME_LIST, redisCacheConfiguration.entryTtl(Duration.ofSeconds(5)));

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .withInitialCacheConfigurations(redisCacheConfigMap)
                .build();
    }
}
