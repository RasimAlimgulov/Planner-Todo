package com.rasimalimgulov.plannerutils.resttemplate;

import com.rasimalimgulov.plannerentity.entity.User;
import com.rasimalimgulov.plannerutils.UserBuilder;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Log4j2
public class UserRestBuilder implements UserBuilder {
    private static final String BASE_URL = "http://localhost:8763/planner-users/user";
    public boolean userExists(Long userId) {

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<Long> request=new HttpEntity<>(userId); // запрос с параметром userId
        ResponseEntity<User> response=null; // ответ объектом User

        try {

            response = restTemplate.exchange(BASE_URL+"/id", HttpMethod.POST,request, User.class);
            if (response.getStatusCode()== HttpStatus.OK){
             return true;
            }
        }
        catch (Exception e) {
          e.printStackTrace();
        }
       return false;

    }

}
