package rs.ac.uns.ftn.informatics.semantic_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SemanticWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SemanticWebApplication.class, args);
		System.out.println("Semantic Web app started");
		
		// FileManager.get().addLocatorClassLoader(SemanticWebApplication.class.getClassLoader());
		// Model model = FileManager.get().loadModel("F:\\Fakultet\\Master\\allotment-smart-contracts\\backend-semantic-web\\src\\main\\resources\\foaf_example.rdf");
		// model.write(System.out, "TURTLE");
		
		// sparqlTest();
		
	}

	/*
	static void sparqlTest() {
		FileManager.get().addLocatorClassLoader(SemanticWebApplication.class.getClassLoader());
		Model model = FileManager.get().loadModel("F:\\Fakultet\\Master\\allotment-smart-contracts\\backend-semantic-web\\src\\main\\resources\\foaf_example.rdf");
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 			  "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " + 
				 			  "SELECT * WHERE { " +
				 			  " ?person foaf:name ?x ." +
				 			  // " FILTER(?x = \"Luka\")" +
				 			  " ?person foaf:knows ?person2 ." +
				 			  " ?person2 foaf:name ?y ." + 
				 			  " FILTER( ?y = \"Luka\")" +
				 			  "}";
		 
		Query query = QueryFactory.create(queryString);
		 
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		 
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				Literal name = soln.getLiteral("x");
				System.out.println(name);
			}
		} finally {
			qexec.close();
		}
	}
	*/
	
}
