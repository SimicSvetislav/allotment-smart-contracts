package rs.ac.uns.ftn.informatics.legal_tech.allotment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import javax.persistence.SequenceGenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;

import io.reactivex.disposables.Disposable;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Account;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Organization;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.AccountsService;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.OrganizationService;

@Component
public class InitializationBean {

  @Autowired
  private AccountsService service;
  
  @Autowired
  private OrganizationService orgService;

  @Autowired
  private Web3j web3j;
  
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
						String account = parts[1];
						accounts[number].setAccount(account);
						System.out.println(number + " " + account);
						
						Organization org = orgService.findOneById((long)number + 1);
						if (org != null) {
							org.setAccount(account);
							orgService.save(org); 
						}
						
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
		
		// Adding transaction listener
		/*Disposable subscription = web3j.transactionFlowable().subscribe(tx -> {
		    System.out.println("Received notification!");
		});*/
		
		// System.out.println("Zone offset " + (Calendar.ZONE_OFFSET + Calendar.DST_OFFSET) / (60 * 1000));
		
  	}	
}
