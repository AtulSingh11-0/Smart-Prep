package trex.hackathon.smart_prep;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
public class SmartPrepApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartPrepApplication.class, args);
		log.info("SmartPrepApplication started successfully on http://localhost:8080");
	}

}