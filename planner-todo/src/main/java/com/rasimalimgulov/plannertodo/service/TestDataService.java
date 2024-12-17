package com.rasimalimgulov.plannertodo.service;

import com.rasimalimgulov.plannerentity.entity.Category;
import com.rasimalimgulov.plannerentity.entity.Priority;
import com.rasimalimgulov.plannerentity.entity.Task;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class TestDataService {
    private final TaskService taskService;
    private final PriorityService priorityService;
    private final CategoryService categoryService;

    public TestDataService(TaskService taskService, PriorityService priorityService, CategoryService categoryService) {
        this.taskService = taskService;
        this.priorityService = priorityService;
        this.categoryService = categoryService;
    }
    public void initTestData(String userId){

        Priority priority1 = new Priority();
        priority1.setUserId(userId);
        priority1.setTitle("High Priority");
        priority1.setColor("Red");
        Priority priority2 = new Priority();
        priority2.setUserId(userId);
        priority2.setTitle("Medium Priority");
        priority2.setColor("Green");
        priorityService.add(priority1);
        priorityService.add(priority2);

        Category category1 = new Category();
        category1.setUserId(userId);
        category1.setTitle("Family");
        Category category2 = new Category();
        category2.setUserId(userId);
        category2.setTitle("Work");
        categoryService.add(category1);
        categoryService.add(category2);

        Date date1 = new Date(); // получаем настоящее время
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);  // устанавливаем это время в календаре
        calendar1.add(Calendar.DATE, 1);  // добавляем 1 день
        date1 = calendar1.getTime(); // получаем новую дату

        Date date2 = new Date(); // получаем настоящее время
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);  // устанавливаем это время в календаре
        calendar2.add(Calendar.DATE, 7);  // добавляем неделю
        date2 = calendar2.getTime(); // получаем новую дату

        Task task1 = new Task();
        task1.setUserId(userId);
        task1.setTitle("Постричь собаку");
        task1.setPriority(priority1);
        task1.setCategory(category1);
        task1.setCompleted(true);
        task1.setTaskDate(date1);
        Task task2 = new Task();
        task2.setUserId(userId);
        task2.setTitle("Постричь собаку");
        task2.setPriority(priority2);
        task2.setCategory(category2);
        task2.setCompleted(false);
        task2.setTaskDate(date2);
        taskService.add(task1);
        taskService.add(task2);
    }
    }
