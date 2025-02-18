package org.example.productservice;

import org.example.productservice.entities.Product;
import org.example.productservice.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ProductServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ProductRepository productRepository) {
		return args -> {
			List<Product> userList=List.of(
					Product.builder()
							.name("tele")
							.description("description_xxxx")
							.quantity(5)
							.UserId(1)
							.build(),
					Product.builder()
							.name("mobile")
							.description("description_xxxx")
							.quantity(5)
							.UserId(2)
							.build(),
					Product.builder()
							.name("mobile2")
							.description("description_xxxx")
							.quantity(5)
							.UserId(2)
							.build()
			);
			productRepository.saveAll(userList);
		};
	}

}
