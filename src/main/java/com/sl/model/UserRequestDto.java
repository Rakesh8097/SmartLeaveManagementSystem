package com.sl.model;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {

	private Long id;
    private String name;
    
//  @NotBlank(message = "email cannot blank")
//	@Schema(description = "email",example = "enter the email")
	@Column(name = "email")
    private String email;
    
//  @NotBlank(message = "password cannot blank")
//	@Schema(description = "password",example = "enter the password")
	@Column(name = "password")
    private String password;
    
    private String department;
    private String role;
}
 