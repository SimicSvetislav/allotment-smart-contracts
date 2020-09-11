package rs.ac.uns.ftn.informatics.semantic_web.controllers;

import java.util.Calendar;
import java.util.Date;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.informatics.semantic_web.InitializationBean;
import rs.ac.uns.ftn.informatics.semantic_web.OntologyBean;
import rs.ac.uns.ftn.informatics.semantic_web.dto.QueryDTO;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/query")
public class QueryController {
	
	final static Logger logger = LoggerFactory.getLogger(InitializationBean.class);
	
	@Autowired
	private OntologyBean ontBean;
	
	@RequestMapping(method = RequestMethod.POST, path="") 
	public ResponseEntity<String> submitQuery(@RequestBody QueryDTO queryData){
		
		// sparqlTest(ontBean.getOntModel());
		sparqlQuery(queryData);
		
		/*Calendar today = Calendar.getInstance();
		XSDDateTime dt = new XSDDateTime(today);
		System.out.println(dt);
		Literal literal = ontBean.getOntModel().createTypedLiteral(dt, XSDDatatype.XSDdateTime);
		System.out.println(literal);*/
		
		return new ResponseEntity<String>("OK query", HttpStatus.OK);
	}
	
	private void sparqlTest(OntModel model) {
		
		ontBean.writeModel("before_query");
		
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 			  "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " + 
				 			  "SELECT * WHERE { " +
				 			  " ?person foaf:familyName ?x ." +
				 			  // " FILTER(?x = \"Luka\")" +
				 			  // " ?contract alo:Date_of_signing ?date" +
				 			  // " FILTER(?date > \"2020-09-07T00:43:31.78Z\"^^xsd:dateTime)" +
				 			  "}";
		 
		Query query = QueryFactory.create(queryString);
		 
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		 
		logger.info("Family name SPARQL results:");
		
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				Literal name = soln.getLiteral("x");
				logger.info(name.toString());
			}
		} finally {
			qexec.close();
		}
	}
	
	private void sparqlQuery(QueryDTO queryData) { 
		ontBean.writeModel("before_query");
		
		Literal startDateLiteral = makeDateTimeLiteral(queryData.getStartDate()*1000);
		Literal endDateLiteral = makeDateTimeLiteral(queryData.getEndDate()*1000);
		Literal locationLiteral = ontBean.getOntModel().createTypedLiteral(queryData.getLocation(), XSDDatatype.XSDstring);
		System.out.println(locationLiteral);
		// System.out.println(startDateLiteral);
		// System.out.println(endDateLiteral);
		System.out.println(endDateLiteral.getValue());
		System.out.println(startDateLiteral.getValue());
		System.out.println(startDateLiteral.getDatatype());
		System.out.println(startDateLiteral.getDatatypeURI());
		
		String dateTimeType = startDateLiteral.getDatatypeURI().split("#")[1];
		
		String queryString =  " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 			  " PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> " + 
				 			  " PREFIX alo: <http://www.semanticweb.org/sveta/ontologies/2019/11/allotment_ontology#> " +
				 			  " PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " + 
				 			  " PREFIX gr: <http://purl.org/goodrelations/v1#> " +
				 			  " PREFIX s: <http://schema.org/> " +
				 			  
				 			  " SELECT DISTINCT ?contract WHERE { " +
					 		  	" ?contract rdf:type pproc:Contract . " +
				 			  	" { " +
					 		  		" ?contract pproc:contractTemporalConditions ?ctc ." +
						 			" ?ctc pproc:estimatedEndDate ?endDate . " + 
						 			" ?ctc alo:Actual_start_date ?startDate . " +
						 			" FILTER (?startDate >= \"" + startDateLiteral.getValue() + "\"^^xsd:" + dateTimeType + ") " +
						 			" FILTER (?endDate <= \"" + endDateLiteral.getValue() + "\"^^xsd:" + dateTimeType + ") " +
						 		" } " + 
						 		" { " +
						 			" ?contract pproc:contractObject ?contractObject . " + 
						 			" ?contractObject pproc:provision ?offering . " +
						 			" ?offering gr:includesObject ?smestajniObjekat . " +
						 			" ?smestajniObjekat s:address ?adresa . " +
						 			" ?adresa s:addressLocality ?mesto . " +
						 			" FILTER regex(?mesto, \"" + locationLiteral + "\", \"i\") " +
						 			// " FILTER regex(?mesto, \"^" + locationLiteral + "$\", \"i\") " +
					 			" } " +
						 		" MINUS " +
						 		" { " +
						 		
						 		" } " +
				 			  "}";
		 
		System.out.println(queryString);
		Query query = QueryFactory.create(queryString);
		 
		QueryExecution qexec = QueryExecutionFactory.create(query, ontBean.getOntModel());
		 
		logger.info("SPARQL query results:");
		
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution solution = results.nextSolution();
				// Literal literal = solution.getLiteral("endDate");
				Resource res = solution.getResource("contract");
				logger.info(res.toString());
			}
		} finally {
			qexec.close();
		}
	}
	
	private Calendar toCalendar(Date date){ 
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 12);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	 	return cal;
	}
	
	private Literal makeDateTimeLiteral(long timestamp) {
		Date date = new Date(timestamp);
		Calendar dateCalendar = toCalendar(date);
		XSDDateTime xsdDateTime = new XSDDateTime(dateCalendar);
		
		return ontBean.getOntModel().createTypedLiteral(xsdDateTime, XSDDatatype.XSDdateTime);
		
	}
	
}
