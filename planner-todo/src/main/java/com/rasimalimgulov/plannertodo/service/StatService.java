package com.rasimalimgulov.plannertodo.service;

import com.rasimalimgulov.plannerentity.entity.Stat;
import com.rasimalimgulov.plannertodo.repository.StatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class StatService {

    private final StatRepository repository; // сервис имеет право обращаться к репозиторию (БД)

    public StatService(StatRepository repository) {
        this.repository = repository;
    }

    public Stat findStat(Long id) {
        return repository.findByUserId(id);
    }

}
