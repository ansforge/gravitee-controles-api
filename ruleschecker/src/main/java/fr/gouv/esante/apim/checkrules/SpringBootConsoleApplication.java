package fr.gouv.esante.apim.checkrules;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@Slf4j
@SpringBootApplication
@Profile({ "production" })
public class SpringBootConsoleApplication {

	public static void main(String[] args) {
		log.info("STARTING THE APPLICATION");
		SpringApplication.run(SpringBootConsoleApplication.class, args);
		log.info("APPLICATION FINISHED");
	}
}
