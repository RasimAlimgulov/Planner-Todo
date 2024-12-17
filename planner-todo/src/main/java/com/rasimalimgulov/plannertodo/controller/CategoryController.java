package com.rasimalimgulov.plannertodo.controller;

import com.rasimalimgulov.plannerentity.entity.Category;
import com.rasimalimgulov.plannerentity.entity.User;
import com.rasimalimgulov.plannertodo.feign.UserFeignClient;
import com.rasimalimgulov.plannertodo.feign.UserNotFoundException;
import com.rasimalimgulov.plannertodo.search.CategorySearchValues;
import com.rasimalimgulov.plannertodo.service.CategoryService;
import com.rasimalimgulov.plannerutils.UserBuilder;
import com.rasimalimgulov.plannerutils.resttemplate.UserRestBuilder;
import com.rasimalimgulov.plannerutils.webclient.UserWebClientBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;


/*

Используем @RestController вместо обычного @Controller, чтобы все ответы сразу оборачивались в JSON,
иначе пришлось бы добавлять лишние объекты в код, использовать @ResponseBody для ответа, указывать тип отправки JSON

Названия методов могут быть любыми, главное не дублировать их имена внутри класса и URL mapping

*/
@Log4j2
@RestController
@RequestMapping("/category") // базовый URI
public class CategoryController {

    // доступ к данным из БД
    private final CategoryService categoryService;
    private final UserWebClientBuilder userRestBuilder;
    //private final UserFeignClient userFeignClient;

    // используем автоматическое внедрение экземпляра класса через конструктор
    // не используем @Autowired ля переменной класса, т.к. "Field injection is not recommended "
    public CategoryController(CategoryService categoryService, UserWebClientBuilder userRestBuilder) {
        this.categoryService = categoryService;
        this.userRestBuilder = userRestBuilder;
    }

    @PostMapping("/all")
    public List<Category> findAll(@RequestBody String userId) {
        return categoryService.findAll(userId);
    }


    @PostMapping("/add")
    public ResponseEntity<Category> add(@RequestBody Category category, @AuthenticationPrincipal Jwt jwt) {

           category.setUserId(jwt.getSubject()); ///UUID пользователя KEyCloak

        // проверка на обязательные параметры
        if (category.getId() != null && category.getId() != 0) { // это означает, что id заполнено
            // id создается автоматически в БД (autoincrement), поэтому его передавать не нужно, иначе может быть конфликт уникальности значения
            return new ResponseEntity("redundant param: category id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (category.getTitle() == null || category.getTitle().trim().length() == 0) {
            return new ResponseEntity("missed param: title MUST be not null", HttpStatus.NOT_ACCEPTABLE);
        }

        //         проверяем есть ли такой user в user-service
//        if (userRestBuilder.userExists(category.getUserId())){
//            return ResponseEntity.ok(categoryService.add(category));// возвращаем добавленный объект с заполненным ID
//        }
//        userRestBuilder.userExistAsc(category.getUserId()).subscribe((user)-> System.out.println("User = "+user));
//        try {
//            ResponseEntity<User> response = userFeignClient.findUserById(category.getUserId());
//            log.info("Метод в контроллере получил статус " + response.getStatusCode());
//            // Проверка на статус SERVICE_UNAVAILABLE (сервис недоступен)
//            if (response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE) {
//                log.info("Выполняется условие response.getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE");
//                return new ResponseEntity("Система пользователей не доступна, попробуйте позже.", HttpStatus.SERVICE_UNAVAILABLE);
//            }
//            // Если пользователь не найден
//            if (response.getStatusCode() == HttpStatus.NOT_FOUND || response.getBody() == null) {
//                log.info("Пользователь не найден");
//                return new ResponseEntity("id = " + category.getUserId() + " not found.", HttpStatus.NOT_FOUND);
//            }
//            // Если пользователь найден, добавляем категорию
//            return new ResponseEntity(categoryService.add(category), HttpStatus.CREATED);
//        } catch (Exception e) {
//            log.warn("Произошла ошибка: " + e.getMessage());
//            return new ResponseEntity("Произошла ошибка при обработке запроса.", HttpStatus.INTERNAL_SERVER_ERROR);
//
        /// данные о пользователе не нужно проверять и делать запрос в userService

        if (!category.getUserId().isBlank()){
            return ResponseEntity.ok(categoryService.add(category));
        }
        //Если не передали userId
          return new ResponseEntity("user id = " + category.getUserId() + " not found.",HttpStatus.NOT_FOUND);
    }


        @PutMapping("/update")
    public ResponseEntity update(@RequestBody Category category) {

        // проверка на обязательные параметры
        if (category.getId() == null || category.getId() == 0) {
            return new ResponseEntity("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (category.getTitle() == null || category.getTitle().trim().length() == 0) {
            return new ResponseEntity("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        // save работает как на добавление, так и на обновление
        categoryService.update(category);

        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)
    }


    // для удаления используем тип запроса DELETE и передаем ID для удаления
    // можно также использовать метод POST и передавать ID в теле запроса
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {

        // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
        // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
        try {
            categoryService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 без объектов (операция прошла успешно)
    }


    // поиск по любым параметрам CategorySearchValues
    @PostMapping("/search")
    public ResponseEntity<List<Category>> search(@RequestBody CategorySearchValues categorySearchValues,@AuthenticationPrincipal Jwt jwt) {
        categorySearchValues.setUserId(jwt.getSubject());
        // проверка на обязательные параметры
        if (categorySearchValues.getUserId().isBlank()) {
            return new ResponseEntity("missed param: userId", HttpStatus.NOT_ACCEPTABLE);
        }

        // поиск категорий пользователя по названию
        List<Category> list = categoryService.findByTitle(categorySearchValues.getTitle(), categorySearchValues.getUserId());

        return ResponseEntity.ok(list);
    }


    // параметр id передаются не в BODY запроса, а в самом URL
    @PostMapping("/id")
    public ResponseEntity<Category> findById(@RequestBody Long id) {

        Category category = null;

        // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
        // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
        try {
            category = categoryService.findById(id);
        } catch (NoSuchElementException e) { // если объект не будет найден
            e.printStackTrace();
            return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(category);
    }

}
