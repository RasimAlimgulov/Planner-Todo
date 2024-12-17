package com.rasimalimgulov.plannertodo.controller;

import com.rasimalimgulov.plannerentity.entity.Category;
import com.rasimalimgulov.plannerentity.entity.Priority;
import com.rasimalimgulov.plannerentity.entity.Task;
import com.rasimalimgulov.plannertodo.service.CategoryService;
import com.rasimalimgulov.plannertodo.service.PriorityService;
import com.rasimalimgulov.plannertodo.service.TaskService;
import com.rasimalimgulov.plannertodo.service.TestDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/data")
public class TestDataController {
 private final TestDataService testDataService;

    public TestDataController(TestDataService testDataService) {
        this.testDataService = testDataService;
    }
    @KafkaListener(topics = "myTopicExample")
    public void consumeKafka(Long id){
        System.out.println("/////////////////////////////////////////////////////////////");
        System.out.println("Получили id добавленного пользователя равное "+id);
        System.out.println("/////////////////////////////////////////////////////////////");
    }

    @PostMapping("/init")
    public ResponseEntity<Boolean> init(@RequestBody String userId) {
        testDataService.initTestData(userId);
        return ResponseEntity.ok(true);
    }

}
