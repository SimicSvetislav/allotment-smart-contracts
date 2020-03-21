package rs.ac.uns.ftn.informatics.legal_tech.allotment.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.entities.Representative;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.security.JwtProvider;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.security.JwtResponse;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.security.requests.LoginForm;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.security.requests.SignUpForm;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.OrganizationService;
import rs.ac.uns.ftn.informatics.legal_tech.allotment.services.RepresentativeService;

@SuppressWarnings({"rawtypes", "unchecked"})
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/auth/")
public class AuthenticationController {
	
	@Autowired
	private RepresentativeService service;
	
	@Autowired
	private OrganizationService orgService;
	
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtProvider jwtProvider;
    
    
	@RequestMapping(method = RequestMethod.POST, path = "signin", produces = "application/json")
    @ResponseBody
    public ResponseEntity authenticateUser(@RequestBody LoginForm loginRequest) {

    	Representative repr = null;
    	
    	repr  = service.findByDisplayName(loginRequest.getUsername());
		if(repr == null) {
			ResponseEntity res =  new ResponseEntity("Bad credentials", HttpStatus.NOT_ACCEPTABLE);
			return res;
		}
    	
    	UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
    	
    	final Authentication authentication = authenticationManager.authenticate(authReq);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("Get auth: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
            
        return new ResponseEntity<JwtResponse>(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities(), repr.getId()), HttpStatus.OK);
    	      
       
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "signin", produces = "application/json")
    @ResponseBody
    public ResponseEntity<JwtResponse> authenticateUserDummy() {

    	Representative repr = null;
    	String username = "ppera";
    	String password = "123";
    	
    	System.out.println("Everobody should get here");
    	
    	
    	repr  = service.findByDisplayName(username);
    	
    	if(repr == null) {
    		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    	}
    	
    	UsernamePasswordAuthenticationToken authReq = new UsernamePasswordAuthenticationToken(username, password);
    	
    	final Authentication authentication = authenticationManager.authenticate(authReq);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        System.out.println("Get auth: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
            
        return new ResponseEntity<JwtResponse>(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities(), repr.getId()), HttpStatus.OK);
    	      
       
    }
    
    @RequestMapping(method = RequestMethod.GET, path = "signup", produces = "application/json")
    @ResponseBody
    public ResponseEntity<Representative> addUserDummy() {

    	String username = "mika";
    	String email = "mika@gmail.com";
    	String fullName = "Mika Mikić";
    	String password = "123";
    	String phoneNumber = "060/987-65-21";
    	
    	if (service.findByDisplayName(username) != null) {
    		return new ResponseEntity<Representative>(HttpStatus.METHOD_NOT_ALLOWED);
    	}
    	
		Representative repr = new Representative();
		
		repr.setDisplayName(username);
		repr.setEmail(email);
		repr.setFullName(fullName);
		repr.setId(1L);
		repr.setPassword(encoder.encode(password));
		repr.setPhoneNumber(phoneNumber);
		
		repr.setRepresenting(orgService.findOneById(1L));
    	
        service.save(repr);

        return new ResponseEntity<Representative>(repr, HttpStatus.CREATED);

       
    }

    @PostMapping(value = "signup/{role}")
    public ResponseEntity<Representative> registerUser(@Valid @RequestBody SignUpForm signUpRequest,@PathVariable String role) {
        		
    	if (service.findByDisplayName(signUpRequest.getDisplayName()) != null) {
    		return new ResponseEntity<Representative>(HttpStatus.METHOD_NOT_ALLOWED);
    	}
    	
    	if(role.contains("ROLE_REPRESENTATIVE")) {
    		Representative repr = new Representative();
    		
    		repr.setDisplayName(signUpRequest.getDisplayName());
    		repr.setEmail(signUpRequest.getEmail());
    		repr.setFullName(signUpRequest.getFullName());
    		// repr.setNetworkAddress();
    		repr.setPassword(encoder.encode(signUpRequest.getPassword()));
    		repr.setPhoneNumber(signUpRequest.getPhoneNumber());
    		// repr.setRepresenting();
        	
            service.save(repr);

            return new ResponseEntity<Representative>(repr, HttpStatus.CREATED);
            
    	} else {
    		  return new ResponseEntity<Representative>(HttpStatus.FORBIDDEN);
    	}
    }
}
