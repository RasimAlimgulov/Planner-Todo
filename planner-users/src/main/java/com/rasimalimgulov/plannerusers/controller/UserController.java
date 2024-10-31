package com.rasimalimgulov.plannerusers.controller;

import com.rasimalimgulov.plannerentity.entity.User;
import com.rasimalimgulov.plannerusers.search.UserSearchValues;
import com.rasimalimgulov.plannerusers.service.UserService;
import com.rasimalimgulov.plannerutils.webclient.UserWebClientBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final UserWebClientBuilder userWebClientBuilder;
    public UserController(UserService userService, UserWebClientBuilder clientBuilder) {
        this.userService = userService;
        this.userWebClientBuilder = clientBuilder;
    }

    @PostMapping("/add")
    public ResponseEntity<User> addUser(@RequestBody User user) {
        if (user.getId() != null && user.getId() != 0) {
            return new ResponseEntity("Id must be empty!", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getEmail() == null || user.getEmail().trim().length() == 0) {
            return new ResponseEntity("missed param: email", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getPassword() == null || user.getPassword().trim().length() == 0) {
            return new ResponseEntity("missed param: password", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getUsername() == null || user.getUsername().trim().length() == 0) {
            return new ResponseEntity("missed param: username", HttpStatus.NOT_ACCEPTABLE);
        }
        User user1=userService.addUser(user);
        if (user1!=null){
            userWebClientBuilder.initUserData(user.getId()).subscribe((x)-> System.out.println("Тестовые данные добавлены: "+x));
        }
        System.out.println("Этот код выполняется после subscribe на ответ запроса");
        return new ResponseEntity(user1, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        if (user.getId() == null || user.getId() == 0) {
            return new ResponseEntity("missed param: id!", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getEmail() == null || user.getEmail().trim().length() == 0) {
            return new ResponseEntity("missed param: email", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getPassword() == null || user.getPassword().trim().length() == 0) {
            return new ResponseEntity("missed param: password", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getUsername() == null || user.getUsername().trim().length() == 0) {
            return new ResponseEntity("missed param: username", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(userService.updateUser(user), HttpStatus.OK);
    }

    @PostMapping("/deletebyid")
    public ResponseEntity deleteUserById(@RequestBody Long userId) {
        try {
            userService.deleteByUserId(userId);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("UserId=" + userId + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity("User deleted", HttpStatus.OK);
    }

    @PostMapping("/deletebyemail")
    public ResponseEntity deleteUserByEmail(@RequestBody String email) {
        try {
            userService.deleteByEmail(email);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("Email=" + email + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity("Email=" + email + " deleted", HttpStatus.OK);
    }

    @PostMapping("/id")
    public ResponseEntity<User> findUserById(@RequestBody Long userId) {
        log.info("Срабатывает метод findUserById");
         Optional<User> userOptional = userService.findUserById(userId);
         log.info("Получен результат "+Objects.toString(userOptional));
            if (userOptional.isPresent()) {
                return new ResponseEntity(userOptional.get(), HttpStatus.OK);
            }

        return new ResponseEntity("userId=" + userId + " not found", HttpStatus.NOT_FOUND);
    }

    @PostMapping("/email")
    public ResponseEntity<User> findUserByEmail(@RequestBody String email) {
        User user = null;
        try {
            user = userService.findUserByEmail(email);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity("email=" + email + " not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<User>> search(@RequestBody UserSearchValues userSearchValues) {
        String email=userSearchValues.getEmail()!=null?userSearchValues.getEmail():"";
        String username=userSearchValues.getUsername()!=null?userSearchValues.getUsername():"";

//        if (email==null || email.trim().length()==0) {
//            return new ResponseEntity("missed param: user email", HttpStatus.NOT_ACCEPTABLE);
//        }

        String sortColumn=userSearchValues.getSortColumn()!=null?userSearchValues.getSortColumn():"";
        String sortDirection=userSearchValues.getSortDirection()!=null?userSearchValues.getSortDirection():"";

        Integer pageNumber=userSearchValues.getPageNumber()!=null?userSearchValues.getPageNumber():0;
        Integer pageSize=userSearchValues.getPageSize()!=null?userSearchValues.getPageSize():10;

        Sort.Direction direction=sortDirection==null || sortDirection.trim().length() == 0 || sortDirection.trim().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort=Sort.by(direction,sortColumn,"id");
        PageRequest pageable=PageRequest.of(pageNumber,pageSize,sort);
        Page<User> result=userService.findByParams(username,email,pageable);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
