package com.sl.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerRequestDto {
	
    private String name;
    private String email;
    private String password;
    private String department;
	
}
