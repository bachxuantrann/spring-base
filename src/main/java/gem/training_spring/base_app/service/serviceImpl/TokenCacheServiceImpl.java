package gem.training_spring.base_app.service.serviceImpl;

import gem.training_spring.base_app.service.TokenCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenCacheServiceImpl  implements TokenCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public void saveToken(String key, String token) {
//        redisTemplate.opsForValue().set(key, token, 1, TimeUnit.MINUTES);
//        redisTemplate.opsForList().leftPush("refresh_tokens", token);
        redisTemplate.opsForValue().set(key, token, 60, TimeUnit.SECONDS);
    }


    @Override
    public String getToken(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public void deleteToken(String key) {
        redisTemplate.delete(key);
    }
}
