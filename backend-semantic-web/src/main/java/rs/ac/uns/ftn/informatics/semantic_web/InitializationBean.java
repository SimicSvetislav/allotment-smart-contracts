package rs.ac.uns.ftn.informatics.semantic_web;

import java.util.List;

import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import rs.ac.uns.ftn.informatics.semantic_web.model.Accomodation;
import rs.ac.uns.ftn.informatics.semantic_web.model.Agency;
import rs.ac.uns.ftn.informatics.semantic_web.model.Hotel;
import rs.ac.uns.ftn.informatics.semantic_web.model.Representative;
import rs.ac.uns.ftn.informatics.semantic_web.repositories.AccomodationRepository;
import rs.ac.uns.ftn.informatics.semantic_web.repositories.AgencyRepository;
import rs.ac.uns.ftn.informatics.semantic_web.repositories.HotelRepository;
import rs.ac.uns.ftn.informatics.semantic_web.repositories.RepresentativeRepository;

@Component
public class InitializationBean {

	final static Logger logger = LoggerFactory.getLogger(InitializationBean.class);
	
	@Autowired
	private AgencyRepository agRepo;
	
	@Autowired
	private AccomodationRepository accRepo;
	
	@Autowired
	private HotelRepository hotelRepo;
	
	@Autowired
	private RepresentativeRepository reprRepo;
	
	@Autowired
	private OntologyBean ontBean;
	
	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		logger.debug("Initialization bean triggered");
		
		//URL url = SemanticWebApplication.class.getClassLoader().getResource(ONT_FILE_NAME);
		//String ontFile = url.getFile();
		
		//Model model = RDFDataMgr.loadModel(ontFile);
		//model.write(System.out, "TURTLE");
		
		//OntModel m = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RULE_INF, null);
		OntModel m = ontBean.getOntModel();
		//m.read(is, "RDF/XML");
		//m.write(System.out, "TURTLE");
		
		/*ExtendedIterator<OntClass> classes = m.listClasses();
		
		while (classes.hasNext()) {
			OntClass clazz = classes.next();
			System.out.println(clazz.getURI()); 
		}*/
		
		//sparqlTest(m);
		//createSampleIndividual(m);
		
		/*for (String imp : m.listImportedOntologyURIs()) {
		    System.out.println( "Ontology imports " + imp );
		}*/
		
		populateKnowledgeBase(m);
		ontBean.writeModel(m);
		ontBean.setOntModel(m);
	}
	
	private void populateKnowledgeBase(OntModel model) {
		populateAgencies(model);
		
		populateAccomodations(model);
		ontBean.writeModel(model);
		populateHotels(model);
		
		populateRepresentatives(model);
	}
	
	private void populateAgencies(OntModel model) {
		List<Agency> agencies = agRepo.findAll();
		
		for (Agency a: agencies) {
			//System.out.println(a.getId());
			
			//Agencija
			String agClassStr = ontBean.getNamespaces().get("base") + "#" + "Agency";
			Individual ind = ontBean.createIndividual(model, 
					agClassStr, 
					"Organizacija_" + a.getId());
			model.add(ind, RDF.type, model.getOntClass(agClassStr));
			
			// Ime
			DatatypeProperty propName = model.getDatatypeProperty(ontBean.getNamespaces().get("gr") + "#legalName");
			model.add(ind, propName, a.getName());
		
			// Adresa
			String addrClassStr = ontBean.getNamespaces().get("s") + "PostalAddress";
			Individual addressInd = ontBean.createIndividual(model,
					addrClassStr, 
					"Adresa_" + a.getId());
			model.add(addressInd, RDF.type, model.getOntClass(addrClassStr));
			
			// Adresa - zemlja
			DatatypeProperty propCountry = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "addressCountry");
			model.add(addressInd, propCountry, a.getCountry());
			
			// Adresa - grad
			DatatypeProperty propCity = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "addressLocality");
			model.add(addressInd, propCity, a.getCity());
			
			// Adresa - ulica
			DatatypeProperty propStreet = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "streetAddress");
			model.add(addressInd, propStreet, a.getAddress());
						
			DatatypeProperty addressOP = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "address");
			model.add(ind, addressOP, addressInd);
		}
		
	}
	
	private void populateAccomodations(OntModel model) {
		List<Accomodation> accomodations = accRepo.findAll();
		
		String accClassStr = ontBean.getNamespaces().get("base") + "#" + "AccommodationOrg";
		for (Accomodation acc: accomodations) {
			// System.out.println(a.getId());
			
			// Ugostitelj 
			Individual accInd = 
					ontBean.createIndividual(model, 
					accClassStr, 
					"Organizacija_" + acc.getId());
			model.add(accInd, RDF.type, model.getOntClass(accClassStr));
			
			// Ime
			DatatypeProperty propName = model.getDatatypeProperty(ontBean.getNamespaces().get("gr") + "#legalName");
			model.add(accInd, propName, acc.getName());
		
			// Adresa
			String addrClassStr = ontBean.getNamespaces().get("s") + "PostalAddress";
			Individual addressInd = ontBean.createIndividual(model,
					addrClassStr, 
					"Adresa_" + acc.getId());
			model.add(addressInd, RDF.type, model.getOntClass(addrClassStr));
			
			// Adresa - zemlja
			DatatypeProperty propCountry = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "addressCountry");
			model.add(addressInd, propCountry, acc.getCountry());
			
			// Adresa - grad
			DatatypeProperty propCity = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "addressLocality");
			model.add(addressInd, propCity, acc.getCity());
			
			// Adresa - ulica
			DatatypeProperty propStreet = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "streetAddress");
			model.add(addressInd, propStreet, acc.getAddress());
						
			DatatypeProperty addressOP = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "address");
			model.add(accInd, addressOP, addressInd);
		}
		
	}
	
	private void populateHotels(OntModel model) {
		List<Hotel> hotels = hotelRepo.findAll();
		
		String accomodationClassStr = ontBean.getNamespaces().get("acco") + "#" + "Accommodation";
		for (Hotel hotel: hotels) {
			// Smestaj
			Individual hotelInd = 
					ontBean.createIndividual(model, 
					accomodationClassStr, 
					"Smestaj_" + hotel.getId());
			model.add(hotelInd, RDF.type, model.getOntClass(accomodationClassStr));
			
			// Ime
			DatatypeProperty propName = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "name");
			model.add(hotelInd, propName, hotel.getName());
			
			// Adresa
			String addrClassStr = ontBean.getNamespaces().get("s") + "PostalAddress";
			Individual addressInd = ontBean.createIndividual(model,
					addrClassStr, 
					"Adresa_h_" + hotel.getId());
			model.add(addressInd, RDF.type, model.getOntClass(addrClassStr));
			
			// Adresa - drzava
			DatatypeProperty propCountry = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "addressCountry");
			model.add(addressInd, propCountry, hotel.getCountry());
			
			// Adresa - mesto
			DatatypeProperty propCity = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "addressLocality");
			model.add(addressInd, propCity, hotel.getCity());
			
			// Adresa - ulica
			DatatypeProperty propStreet = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "streetAddress");
			model.add(addressInd, propStreet, hotel.getAddress());
						
			DatatypeProperty addressOP = model.getDatatypeProperty(ontBean.getNamespaces().get("s") + "address");
			model.add(hotelInd, addressOP, addressInd);
			
			// Set owner of the hotel
			ObjectProperty ownsOP = model.getObjectProperty(ontBean.getNamespaces().get("gr") + "#owns");
			Individual accInd = model.getIndividual(ontBean.getNamespaces().get("base") + "#" + "Organizacija_" + hotel.getOrg().getId());
			model.add(accInd, ownsOP, hotelInd); 
		}
		
	}
	
	private void populateRepresentatives(OntModel model) {
		List<Representative> representatives = reprRepo.findAll();
		
		String representativeClassStr = ontBean.getNamespaces().get("s") + "Person";
		for (Representative repr: representatives) {
			// Zaposleni
			Individual reprInd = 
					ontBean.createIndividual(model, 
					representativeClassStr, 
					"Zaposleni_" + repr.getId());
			model.add(reprInd, RDF.type, model.getOntClass(representativeClassStr));
			
			String fullName = repr.getFullName();
			String[] nameParts = fullName.split(" ");
			
			// Ime
			DatatypeProperty propGivenName = model.getDatatypeProperty(ontBean.getNamespaces().get("foaf") + "givenName");
			model.add(reprInd, propGivenName, nameParts[0]);
			
			// Prezime
			DatatypeProperty propFamilyName = model.getDatatypeProperty(ontBean.getNamespaces().get("foaf") + "familyName");
			model.add(reprInd, propFamilyName, nameParts[1]);
			
			// Email
			ObjectProperty propEmail = model.getObjectProperty(ontBean.getNamespaces().get("vcard") + "#email");
			model.add(reprInd, propEmail, repr.getEmail());
			
			// Telefon
			ObjectProperty propPhone = model.getObjectProperty(ontBean.getNamespaces().get("vcard") + "#tel");
			model.add(reprInd, propPhone, repr.getPhoneNumber());
			
			// Funkcija
			DatatypeProperty propTitle = model.getDatatypeProperty(ontBean.getNamespaces().get("vcard") + "#title");
			model.add(reprInd, propTitle, "Sluzbenik");
			
			// Organizacija
			ObjectProperty worksForOP = model.getObjectProperty(ontBean.getNamespaces().get("s") + "worksFor");
			Individual orgInd = model.getIndividual(ontBean.getNamespaces().get("base") + "#" + "Organizacija_" + repr.getRepresenting().getId());
			model.add(reprInd, worksForOP, orgInd);	
			
		}
		
	}
	
	/*
	private static void sparqlTest(OntModel model) {
		String queryString = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				 			  "PREFIX foaf: <http://xmlns.com/foaf/0.1/> " + 
				 			  "SELECT * WHERE { " +
				 			  " ?person foaf:familyName ?x ." +
				 			  // " FILTER(?x = \"Luka\")" +
				 			  "}";
		 
		Query query = QueryFactory.create(queryString);
		 
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		 
		logger.debug("Family name SPARQL results:");
		
		try {
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution soln = results.nextSolution();
				Literal name = soln.getLiteral("x");
				logger.debug(name.toString());
			}
		} finally {
			qexec.close();
		}
	}
	
	private void createSampleIndividual(OntModel m) {
		OntClass c = m.getOntClass("http://www.w3.org/2006/vcard/ns#Address");
		Individual ind = m.createIndividual(ontBean.getNamespaces().get("base") + "#" + "Adresa_Y", c);
		Individual ind2 = m.createIndividual(ontBean.getNamespaces().get("base") + "#" + "Adresa_X2", c);
		Individual ind3 = m.createIndividual(ontBean.getNamespaces().get("base") + "#" + "Adresa_Y3", c);
		
		for (Iterator<Resource> i = ind.listRDFTypes(false); i.hasNext(); ) {
			logger.debug(ind.getURI() + " is asserted in class " + i.next());
		}
		
		for (ExtendedIterator<? extends OntResource> classes = c.listInstances(); classes.hasNext(); ) {
			logger.debug(classes.next().toString());
        }
		
		for (ExtendedIterator<? extends OntResource> classes = m.getOntClass(ontBean.getNamespaces().get("s") + "Organization").listInstances(); classes.hasNext(); ) {
			logger.debug(classes.next().toString());
        }
		
		///m.add(ind, RDF.type, c);
		
		//URL url = Resources.getResource("/file_out.owl"); 
		//File file = new File(url.getFile());
		
		File file = new File(SemanticWebApplication.class.getClassLoader().getResource(".").getFile() + "/../../src/main/resources/file_out.owl");
		logger.debug(file.getAbsolutePath());
		
		try {
			if (file.createNewFile()) {
				logger.debug("File is created!");
			} else {
				logger.debug("File already exists.");
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		FileWriter out = null;
		try {
			out = new FileWriter(file);
			m.getWriter("RDF/XML").write(m, out, ontBean.getNamespaces().get("base") + "#");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
	}*/

	
	
}
