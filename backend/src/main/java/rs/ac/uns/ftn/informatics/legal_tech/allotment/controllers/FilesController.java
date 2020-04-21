package rs.ac.uns.ftn.informatics.legal_tech.allotment.controllers;
	
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import rs.ac.uns.ftn.informatics.legal_tech.allotment.AllotmentApplication;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/files")
public class FilesController {
	
	@ResponseBody
	@RequestMapping(value ="/source", produces = MediaType.TEXT_PLAIN_VALUE)
	public String sourceCode(HttpServletResponse response){
	    
		String sourceCode = "";
		
	    // Resource resource = new ClassPathResource("Allotment.sol");
		InputStream is = AllotmentApplication.class.getClassLoader().getResourceAsStream("Allotment.sol");
		
	    /*File file = null;
	    try {
			file = resource.getFile();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occured");
		}*/
	    
	    Scanner reader = new Scanner(is);
	    
	    while (reader.hasNextLine()) {
	    	String line = reader.nextLine();
	    	// System.out.println(line);
	    	sourceCode += line + "\n";
	    }
	    
	    reader.close();
	    
	    response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
	    
	    return sourceCode;
	}

}
