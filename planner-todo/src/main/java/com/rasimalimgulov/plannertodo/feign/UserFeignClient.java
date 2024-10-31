package com.rasimalimgulov.plannertodo.feign;

import com.rasimalimgulov.plannerentity.entity.User;
import lombok.extern.log4j.Log4j2;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "planner-users", fallbackFactory = UserFeignClientFallbackFactory.class, configuration = FeignConfig.class)
public interface UserFeignClient {
  @PostMapping("/user/id")
   ResponseEntity<User> findUserById(@RequestBody Long userId);
}

@Log4j2
@Component
class UserFeignClientFallbackFactory implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable cause) {
        log.info("Сработал FeignClientFallbackFactory с исключением: " + cause.getClass().getName());
        log.info(cause.getCause());
        Throwable cause1 = cause.getCause();
        if (cause1 instanceof UserNotFoundException) {
            log.info("Возвращает ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)");
            return userId -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        log.info("Не выполнил условие cause instanceof UserNotFoundException");
        return new MyCircuitBreaker();
    }
}
@Log4j2
@Component
class MyCircuitBreaker implements UserFeignClient {
    @Override
    public ResponseEntity<User> findUserById(Long userId) {
       log.info("Выполняется myCircuitBreaker и выдаёт SERVICE_UNAVAILABLE");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(null);
    }
}
