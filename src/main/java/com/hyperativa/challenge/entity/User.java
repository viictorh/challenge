package com.hyperativa.challenge.entity;

import com.hyperativa.challenge.dto.SignupDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String login;
	private String name;
	private String password;
	private Boolean enabled;
	@ManyToOne
	@JoinColumn(name = "role_id")
	private Role role;

	public static User fromSignupDTO(SignupDTO signup) {
		User user = new User();
		user.enabled = true;
		user.login = signup.login();
		user.name = signup.name();
		return user;
	}

}
