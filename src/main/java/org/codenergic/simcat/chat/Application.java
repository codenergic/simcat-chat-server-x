package org.codenergic.simcat.chat;

import java.io.IOException;

import org.codenergic.simcat.Simcat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = Simcat.class)
public class Application {
	public static void main(String[] args) throws IOException {
		SpringApplication.run(Application.class, args);
	}
}
