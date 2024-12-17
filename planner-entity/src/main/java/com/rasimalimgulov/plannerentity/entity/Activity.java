package com.rasimalimgulov.plannerentity.entity;


/*

Вся активность пользователя (активация аккаунта, другие действия по необходимости)

*/


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.type.NumericBooleanConverter;

import java.util.Objects;

@Entity
@Table(name = "activity", schema = "todo", catalog = "planner-todo")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Activity { // название таблицы будет браться автоматически по названию класса с маленькой буквы: activity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = NumericBooleanConverter.class)
    private Boolean activated; // становится true только после подтверждения активации пользователем (обратно false уже стать не может по логике)

    @Column(updatable = false)
    private String uuid; // создается только один раз с помощью триггера в БД

    //    @OneToOne(fetch = FetchType.LAZY)
//    @MapsId
//    @JoinColumn(name = "user_id", referencedColumnName = "id")
//    private User user;
    @Column(name = "user_id")
    private String userId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return id.equals(activity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
