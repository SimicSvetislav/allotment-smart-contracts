package rs.ac.uns.ftn.informatics.legal_tech.allotment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AllotmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AllotmentApplication.class, args);
	}

}
