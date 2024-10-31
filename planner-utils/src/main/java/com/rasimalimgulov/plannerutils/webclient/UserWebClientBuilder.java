package com.rasimalimgulov.plannerutils.webclient;

import com.rasimalimgulov.plannerentity.entity.User;
import com.rasimalimgulov.plannerutils.UserBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class UserWebClientBuilder implements UserBuilder {
    private static final String BASE_URL_USERS = "http://localhost:8763/planner-users/user";
    private static final String BASE_URL_TODO = "http://localhost:8763/planner-todo/data";

    public boolean userExists(Long userId) {
        try {
            User user = WebClient.create(BASE_URL_USERS)
                    .post()
                    .uri("/id")
                    .bodyValue(userId)
                    .retrieve()
                    .bodyToFlux(User.class)
                    .blockFirst();
            if (user != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    public Flux<User> userExistAsc(Long userId){
        return WebClient.create(BASE_URL_USERS)
                .post()
                .uri("/id")
                .bodyValue(userId)
                .retrieve()
                .bodyToFlux(User.class);
    }

    public Flux<Boolean> initUserData(Long userId) {
        return WebClient.create(BASE_URL_TODO)
                .post()
                .uri("/init")
                .bodyValue(userId)
                .retrieve()
                .bodyToFlux(Boolean.class);
    }

}
