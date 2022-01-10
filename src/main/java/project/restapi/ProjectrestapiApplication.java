package project.restapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EntityScan(basePackages = {"project.restapi.model"})
@ComponentScan(basePackages = {"project.*"})
@EnableJpaRepositories(basePackages = {"project.restapi.repository"})
@EnableTransactionManagement
@EnableWebMvc
@RestController
@EnableAutoConfiguration
public class ProjectrestapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectrestapiApplication.class, args);
	}

}
