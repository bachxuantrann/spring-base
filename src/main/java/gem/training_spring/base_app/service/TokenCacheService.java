package gem.training_spring.base_app.service;

public interface TokenCacheService {
    void saveToken(String key,String token);
    String getToken(String key);
    void deleteToken(String key);
}
