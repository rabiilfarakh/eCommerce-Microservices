package org.example.paymenttservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class PaymenttServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymenttServiceApplication.class, args);
	}

}
