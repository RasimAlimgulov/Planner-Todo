package com.rasimalimgulov.plannertodo.service;

import com.rasimalimgulov.plannerentity.entity.Priority;
import com.rasimalimgulov.plannertodo.repository.PriorityRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Transactional
public class PriorityService {

    private final PriorityRepository repository;

    public PriorityService(PriorityRepository repository) {
        this.repository = repository;
    }

    public List<Priority> findAll(String userId) {
        return repository.findByUserIdOrderByIdAsc(userId);
    }

    public Priority add(Priority priority) {
        return repository.save(priority); // метод save обновляет или создает новый объект, если его не было
    }

    public Priority update(Priority priority) {
        return repository.save(priority); // метод save обновляет или создает новый объект, если его не было
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public Priority findById(Long id) {
        return repository.findById(id).get(); // т.к. возвращается Optional - можно получить объект методом get()
    }

    public List<Priority> find(String title, String userId) {
        return repository.findByTitle(title, userId);
    }

}
