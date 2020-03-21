package rs.ac.uns.ftn.informatics.legal_tech.allotment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.persistence.SequenceGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Account;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.AccountsService;

@Component
public class InitializationBean {

  @Autowired
  private AccountsService service;

  @EventListener
  public void onApplicationEvent(ContextRefreshedEvent event) {
      System.out.println("Initialization bean triggered");
		BufferedReader reader = null;
		
		Resource resource = new ClassPathResource("accounts.data");
		
		try {
			reader = new BufferedReader(new FileReader(resource.getFile()));
			
			String line = null;
			Account[] accounts = new Account[10];
			for (int i=0; i < 10; i++) {
				accounts[i] = new Account();
			}
			while ((line = reader.readLine())!=null) {
				
				String[] parts = line.split(" ");
				if (parts.length > 1)  {
					if (parts[1].length() == 42) {
						Integer number = Integer.parseInt(parts[0].substring(parts[0].indexOf("(")+1, parts[0].indexOf(")")));
						accounts[number].setAccount(parts[1]);
						System.out.println(number + " " + parts[1]);
					} else if (parts[1].length() == 66) {
						Integer number = Integer.parseInt(parts[0].substring(parts[0].indexOf("(")+1, parts[0].indexOf(")")));
						accounts[number].setPrivateKey(parts[1]);
						System.out.println(number + " " + parts[1]);
						service.save(accounts[number]);
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
  	}	
}
