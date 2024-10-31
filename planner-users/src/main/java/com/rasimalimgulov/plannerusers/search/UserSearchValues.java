package com.rasimalimgulov.plannerusers.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.PathVariable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchValues {
private String email;
private String username;
private Integer pageNumber;
private Integer pageSize;
private String sortColumn;
private String sortDirection;
}
