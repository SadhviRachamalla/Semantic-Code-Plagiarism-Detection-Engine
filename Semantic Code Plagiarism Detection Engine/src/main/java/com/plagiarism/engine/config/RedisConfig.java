package com.plagiarism.engine.config;

import com.plagiarism.engine.similarity.WinnowingFingerprinter.Fingerprint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, List<Fingerprint>> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, List<Fingerprint>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}
