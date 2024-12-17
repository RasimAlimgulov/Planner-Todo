package com.rasimalimgulov.plannertodo.controller;

import com.rasimalimgulov.plannerentity.entity.Priority;
import com.rasimalimgulov.plannertodo.search.PrioritySearchValues;
import com.rasimalimgulov.plannertodo.service.PriorityService;
import com.rasimalimgulov.plannerutils.UserBuilder;
import com.rasimalimgulov.plannerutils.resttemplate.UserRestBuilder;
import com.rasimalimgulov.plannerutils.webclient.UserWebClientBuilder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;


/*

Чтобы дать меньше шансов для взлома (например, CSRF атак): POST/PUT запросы могут изменять/фильтровать закрытые данные, а GET запросы - для получения незащищенных данных
Т.е. GET-запросы не должны использоваться для изменения/получения секретных данных

*/

@RestController
@RequestMapping("/priority") // базовый URI
public class PriorityController {

    private final UserBuilder userBuilder;
    // доступ к данным из БД
    private final PriorityService priorityService;

    // используем автоматическое внедрение экземпляра класса через конструктор
    // не используем @Autowired ля переменной класса, т.к. "Field injection is not recommended "
    public PriorityController(UserWebClientBuilder userRestBuilder, PriorityService priorityService) {
        this.userBuilder = userRestBuilder;
        this.priorityService = priorityService;
    }


    @PostMapping("/all")
    public List<Priority> findAll(@RequestBody String userId) {
        return priorityService.findAll(userId);
    }


    @PostMapping("/add")
    public ResponseEntity<Priority> add(@RequestBody Priority priority, @AuthenticationPrincipal Jwt jwt) {
        priority.setUserId(jwt.getSubject());
        // проверка на обязательные параметры
        if (priority.getId() != null && priority.getId() != 0) {
            // id создается автоматически в БД (autoincrement), поэтому его передавать не нужно, иначе может быть конфликт уникальности значения
            return new ResponseEntity("redundant param: priority id MUST be null", HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (priority.getTitle() == null || priority.getTitle().trim().length() == 0) {
            return new ResponseEntity("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение color
        if (priority.getColor() == null || priority.getColor().trim().length() == 0) {
            return new ResponseEntity("missed param: color", HttpStatus.NOT_ACCEPTABLE);
        }
//        if (userBuilder.userExists(priority.getUserId())){
//            return ResponseEntity.ok(priorityService.add(priority));// возвращаем добавленный объект с заполненным ID
//        }
        if (!priority.getUserId().isBlank()){
            return ResponseEntity.ok(priorityService.add(priority));// возвращаем добавленный объект с заполненным ID
        }

        return new ResponseEntity("user id="+priority.getUserId()+" not found.",HttpStatus.NOT_FOUND);
    }


    @PutMapping("/update")
    public ResponseEntity update(@RequestBody Priority priority) {


        // проверка на обязательные параметры
        if (priority.getId() == null || priority.getId() == 0) {
            return new ResponseEntity("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение title
        if (priority.getTitle() == null || priority.getTitle().trim().length() == 0) {
            return new ResponseEntity("missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        // если передали пустое значение color
        if (priority.getColor() == null || priority.getColor().trim().length() == 0) {
            return new ResponseEntity("missed param: color", HttpStatus.NOT_ACCEPTABLE);
        }

        // save работает как на добавление, так и на обновление
        priorityService.update(priority);


        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)

    }

    // параметр id передаются не в BODY запроса, а в самом URL
    @PostMapping("/id")
    public ResponseEntity<Priority> findById(@RequestBody Long id) {

        Priority priority = null;

        // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
        // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
        try {
            priority = priorityService.findById(id);
        } catch (NoSuchElementException e) { // если объект не будет найден
            e.printStackTrace();
            return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return ResponseEntity.ok(priority);
    }


    // для удаления используем типа запроса put, а не delete, т.к. он позволяет передавать значение в body, а не в адресной строке
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {

        // можно обойтись и без try-catch, тогда будет возвращаться полная ошибка (stacktrace)
        // здесь показан пример, как можно обрабатывать исключение и отправлять свой текст/статус
        try {
            priorityService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id=" + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }

        return new ResponseEntity(HttpStatus.OK); // просто отправляем статус 200 (операция прошла успешно)
    }


    // поиск по любым параметрам PrioritySearchValues
    @PostMapping("/search")
    public ResponseEntity<List<Priority>> search(@RequestBody PrioritySearchValues prioritySearchValues,@AuthenticationPrincipal Jwt jwt) {
        prioritySearchValues.setUserId(jwt.getSubject());
        // проверка на обязательные параметры
        if (prioritySearchValues.getUserId().isBlank()) {
            return new ResponseEntity("missed param: userId", HttpStatus.NOT_ACCEPTABLE);
        }

        // если вместо текста будет пусто или null - вернутся все категории
        return ResponseEntity.ok(priorityService.find(prioritySearchValues.getTitle(), prioritySearchValues.getUserId()));
    }
}
