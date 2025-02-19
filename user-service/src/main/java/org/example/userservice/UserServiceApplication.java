package org.example.userservice;

import org.example.userservice.entities.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner commandLineRunner(UserRepository userRepository) {
//		return args -> {
//			List<User> userList=List.of(
//				User.builder()
//						.firstName("rabii")
//						.lastName("lfarakh")
//						.address("safi")
//						.email("rabii@gmail.com")
//						.password("rabii123")
//						.build(),
//					User.builder()
//							.firstName("amin")
//							.lastName("chagir")
//							.address("agadir")
//							.email("amin@gmail.com")
//							.password("amin123")
//							.build()
//			);
//			userRepository.saveAll(userList);
//		};
//	}

}
