package com.rasimalimgulov.plannertodo.repository;

import com.rasimalimgulov.plannerentity.entity.Stat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StatRepository extends CrudRepository<Stat, Long> {

    Stat findByUserId(Long id);
}
