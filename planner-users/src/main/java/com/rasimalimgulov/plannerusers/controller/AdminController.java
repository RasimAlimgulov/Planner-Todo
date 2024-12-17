package com.rasimalimgulov.plannerusers.controller;

import com.rasimalimgulov.plannerentity.entity.User;
import com.rasimalimgulov.plannerusers.dto.UserDTO;
import com.rasimalimgulov.plannerusers.keycloak.KeyCloakUtils;
import com.rasimalimgulov.plannerusers.rabbit.MessageProducer;
import com.rasimalimgulov.plannerusers.search.UserSearchValues;
import com.rasimalimgulov.plannerusers.service.UserService;
import com.rasimalimgulov.plannerutils.webclient.UserWebClientBuilder;
import jakarta.ws.rs.core.Response;
import lombok.extern.log4j.Log4j2;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Log4j2
@RestController
@RequestMapping("/admin/user")
public class AdminController {
    private final static int CONFLICT = 409;
    private final KeyCloakUtils keyCloakUtils;
    private final UserService userService;
    private final UserWebClientBuilder userWebClientBuilder;
    private final KafkaTemplate<String, Long> kafkaTemplate;
    private final MessageProducer messageProducer;

    public AdminController(KeyCloakUtils keyCloakUtils, MessageProducer messageProducer, UserService userService, UserWebClientBuilder clientBuilder, KafkaTemplate<String, Long> kafkaTemplate) {
        this.keyCloakUtils = keyCloakUtils;
        this.messageProducer = messageProducer;
        this.userService = userService;
        this.userWebClientBuilder = clientBuilder;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/add")
    public ResponseEntity addUser(@RequestBody UserDTO userDTO) {
        if (userDTO.getId() != null) {
            return new ResponseEntity("Id must be empty!", HttpStatus.NOT_ACCEPTABLE);
        }
        if (userDTO.getEmail() == null || userDTO.getEmail().trim().length() == 0) {
            return new ResponseEntity("missed param: email", HttpStatus.NOT_ACCEPTABLE);
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().trim().length() == 0) {
            return new ResponseEntity("missed param: password", HttpStatus.NOT_ACCEPTABLE);
        }
        if (userDTO.getUsername() == null || userDTO.getUsername().trim().length() == 0) {
            return new ResponseEntity("missed param: username", HttpStatus.NOT_ACCEPTABLE);
        }
//        User user1=userService.addUser(userDTO);
////        if (user1!=null){
////            userWebClientBuilder.initUserData(userDTO.getId()).subscribe((x)-> System.out.println("Тестовые данные добавлены: "+x));
////        }
////        System.out.println("Этот код выполняется после subscribe на ответ запроса");
//       if (user1!=null){
//           messageProducer.sendMessage(user1.getId());
//           kafkaTemplate.send("myTopicExample", user1.getId());
//       }
//        return new ResponseEntity(user1, HttpStatus.CREATED);

        Response response = keyCloakUtils.createKeycloakUser(userDTO);
        if (response.getStatus() == CONFLICT) {
            return new ResponseEntity("User or Email already exist", HttpStatus.CONFLICT);
        }
        String userId = CreatedResponseUtil.getCreatedId(response);
        System.out.printf("User created with userId : %s%n",userId);
        List<String> roles=new ArrayList<>();
        roles.add("User_Realm");
        keyCloakUtils.addRoles(userId,roles);
        return ResponseEntity.status(response.getStatus()).build();
    }

    @PutMapping("/update")
    public ResponseEntity updateUser(@RequestBody UserDTO user) {
        if (user.getId().isBlank()) {
            return new ResponseEntity("missed param: id!", HttpStatus.NOT_ACCEPTABLE);
        }
//        if (user.getEmail() == null || user.getEmail().trim().length() == 0) {
//            return new ResponseEntity("missed param: email", HttpStatus.NOT_ACCEPTABLE);
//        }
//        if (user.getPassword() == null || user.getPassword().trim().length() == 0) {
//            return new ResponseEntity("missed param: password", HttpStatus.NOT_ACCEPTABLE);
//        }
//        if (user.getUsername() == null || user.getUsername().trim().length() == 0) {
//            return new ResponseEntity("missed param: username", HttpStatus.NOT_ACCEPTABLE);
//        }
//        return new ResponseEntity(userService.updateUser(user), HttpStatus.OK);
        keyCloakUtils.updateKCUser(user);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/deletebyid")
    public ResponseEntity deleteUserById(@RequestBody String userId) {

        keyCloakUtils.deleteKeyCloakUser(userId);
        return new ResponseEntity("User deleted", HttpStatus.OK);
//        try {
//            userService.deleteByUserId(userId);
//        } catch (EmptyResultDataAccessException e) {
//            e.printStackTrace();
//            return new ResponseEntity("UserId=" + userId + " not found", HttpStatus.NOT_ACCEPTABLE);
//        }

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
    public ResponseEntity findUserById(@RequestBody String userId) {

        return ResponseEntity.ok(keyCloakUtils.findUserKCById(userId));
//        log.info("Срабатывает метод findUserById");
//        Optional<User> userOptional = userService.findUserById(userId);
//        log.info("Получен результат " + Objects.toString(userOptional));
//        if (userOptional.isPresent()) {
//            return new ResponseEntity(userOptional.get(), HttpStatus.OK);
//        }
//
//        return new ResponseEntity("userId=" + userId + " not found", HttpStatus.NOT_FOUND);

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
//    public ResponseEntity<Page<User>> search(@RequestBody UserSearchValues userSearchValues) {
    public ResponseEntity<List<UserRepresentation>> search(@RequestBody String email) {

        return ResponseEntity.ok(keyCloakUtils.searchKCUser("email:"+email));
//        String email = userSearchValues.getEmail() != null ? userSearchValues.getEmail() : "";
//        String username = userSearchValues.getUsername() != null ? userSearchValues.getUsername() : "";
//
////        if (email==null || email.trim().length()==0) {
////            return new ResponseEntity("missed param: user email", HttpStatus.NOT_ACCEPTABLE);
////        }
//
//        String sortColumn = userSearchValues.getSortColumn() != null ? userSearchValues.getSortColumn() : "";
//        String sortDirection = userSearchValues.getSortDirection() != null ? userSearchValues.getSortDirection() : "";
//
//        Integer pageNumber = userSearchValues.getPageNumber() != null ? userSearchValues.getPageNumber() : 0;
//        Integer pageSize = userSearchValues.getPageSize() != null ? userSearchValues.getPageSize() : 10;
//
//        Sort.Direction direction = sortDirection == null || sortDirection.trim().length() == 0 || sortDirection.trim().equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
//        Sort sort = Sort.by(direction, sortColumn, "id");
//        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sort);
//        Page<User> result = userService.findByParams(username, email, pageable);
//        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}
