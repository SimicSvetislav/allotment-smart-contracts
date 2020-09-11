package rs.ac.uns.ftn.informatics.semantic_web.controllers;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.ontology.DatatypeProperty;
import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.ObjectProperty;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import rs.ac.uns.ftn.informatics.semantic_web.OntologyBean;
import rs.ac.uns.ftn.informatics.semantic_web.model.Contract;
import rs.ac.uns.ftn.informatics.semantic_web.model.ContractCTO;
import rs.ac.uns.ftn.informatics.semantic_web.services.ContractService;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/sw")
public class ContractController {
	
	final static Logger logger = LoggerFactory.getLogger(ContractController.class);
	
	@Autowired
	private ContractService service;
	
	@Autowired
	private OntologyBean ontBean;
	
	private HashMap<String, String> namespaces = new HashMap<String, String>() {
		private static final long serialVersionUID = -4798418354952325000L;

	{
		put("base", "http://www.semanticweb.org/sveta/ontologies/2019/11/allotment_ontology");
		put("pproc", "http://contsem.unizar.es/def/sector-publico/pproc");
		put("foaf", "http://xmlns.com/foaf/0.1/");
		put("gr", "http://purl.org/goodrelations/v1");
		put("s", "http://schema.org/");
		put("ethon", "http://ethon.consensys.net/");
		put("acco", "http://purl.org/acco/ns");
		put("vcard", "http://www.w3.org/2006/vcard/ns");
	}};
	
	@RequestMapping(method = RequestMethod.GET, path="/trc")
	public ResponseEntity<String> testRestClient() {
		
		return new ResponseEntity<String>("Test OK from service", HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.POST, path="/add/{reprId}")
	public ResponseEntity<String> addContract(@RequestBody ContractCTO contract,
			@PathVariable("reprId") Long reprId) {
		
		OntModel model = ontBean.getOntModel();
		OntClass clazz = null;
		String classStr = null;
		Literal l = null;
		String tempStr = null;
		
		// PublicContract
		tempStr = namespaces.get("pproc") + "#Contract";
		clazz = model.getOntClass(tempStr);
		Individual contractInd = createIndividual(model, 
				tempStr, 
				"Ugovor/" + contract.getId());
		model.add(contractInd, RDF.type, model.getOntClass(tempStr));
		
		// ContractingPartyAgency
		ObjectProperty objectProperty = model.getObjectProperty(ontBean.getNamespaces().get("base") + "#contracting_party_agency");
		Individual expressionObject = model.getIndividual(ontBean.getNamespaces().get("base") + "#Organizacija_" + contract.getAgId());
		model.add(contractInd, objectProperty, expressionObject); 
		
		// ContractingPartyAccomodation
		objectProperty = model.getObjectProperty(ontBean.getNamespaces().get("base") + "#contracting_party_accomodation");
		expressionObject = model.getIndividual(ontBean.getNamespaces().get("base") + "#Organizacija_" + contract.getAccId());
		model.add(contractInd, objectProperty, expressionObject); 
		
		// jurisdiction -> Sud (URI = naziv + mesto)
		tempStr = namespaces.get("base") + "#Court";
		clazz = model.getOntClass(tempStr);
		String courtURI = contract.getCourtName().replace(' ', '_') + '_' + contract.getCourtLocation().replace(' ', '_');
		Individual courtInd = createIndividual(model, 
				tempStr, 
				courtURI);
		model.add(courtInd, RDF.type, model.getOntClass(tempStr));
		
		objectProperty = model.getObjectProperty(ontBean.getNamespaces().get("base") + "#jurisdiction");
		model.add(contractInd, objectProperty, courtInd);

		// Legal Document Reference -> Law (URI)
		tempStr = namespaces.get("base") + "#Law";
		clazz = model.getOntClass(tempStr);
		Individual lawInd = createIndividual(model, 
				tempStr, 
				"Zakon_o_obligacionim_odnosima");
		model.add(lawInd, RDF.type, model.getOntClass(tempStr));
		
		objectProperty = model.getObjectProperty(ontBean.getNamespaces().get("pproc") + "#legalDocumentReference");
		model.add(contractInd, objectProperty, lawInd);
		
		// signatory agency
		objectProperty = model.getObjectProperty(ontBean.getNamespaces().get("base") + "#signatory_agency");
		
		Long agReprId = Long.parseLong(contract.getAgencyRepr());
		if (agReprId == 0) {
			agReprId = reprId;
		}
		
		expressionObject = model.getIndividual(ontBean.getNamespaces().get("base") + "#Zaposleni_" + agReprId);
		model.add(contractInd, objectProperty, expressionObject); 
		
		// singatory accomodation
		objectProperty = model.getObjectProperty(ontBean.getNamespaces().get("base") + "#signatory_accomodation");
		
		Long accReprId = Long.parseLong(contract.getAccomodationRepr());
		if (accReprId == 0) {
			accReprId = reprId;
		}
		
		expressionObject = model.getIndividual(ontBean.getNamespaces().get("base") + "#Zaposleni_" + accReprId);
		model.add(contractInd, objectProperty, expressionObject); 
		
		// Date_of_signing 
		DatatypeProperty dtProp = model.getDatatypeProperty(ns("base") + "#Date_of_signing");
		Calendar today = Calendar.getInstance();
		l = model.createTypedLiteral(new XSDDateTime(today), XSDDatatype.XSDdateTime);
		model.add(contractInd, dtProp, l);
		
		// Public contract --- Object of the contract --> Contract object
		objectProperty = model.getObjectProperty(ontBean.getNamespaces().get("pproc") + "#contractObject");
		Individual coInd = createIndividual(model, 
				ns("pproc") + "#ContractObject", 
				"Contract_object_" + contract.getId());
		model.add(contractInd, objectProperty, coInd);
		
		// Contract object --- Provision ---> Offering
		objectProperty = model.getObjectProperty(ns("pproc") + "#provision");
		Individual offeringInd = createIndividual(model, 
				ns("gr") + "#Offering", 
				"Offering_" + contract.getId());
		model.add(coInd, objectProperty, offeringInd);
		
		//	--- includes --> ProductOrService (Hotel)
		objectProperty = model.getObjectProperty(ns("gr") + "#includesObject");
		for (BigInteger bi: contract.getHotels()) {
			Individual smestajInd = model.getIndividual(ns("base") + "#Smestaj_" + bi);
			model.add(offeringInd, objectProperty, smestajInd);
		}
		
		//  --- hasEligibleQuantity --> QuantitativeValue
		objectProperty = model.getObjectProperty(ns("gr") + "#hasEligibleQuantity");
		Individual qvi = createIndividual(model, ns("gr") + "#QuantitativeValue", null);
		model.add(offeringInd, objectProperty, qvi);
		
		//		(hasValue, hasUnitOfMeasurment)
		dtProp = model.getDatatypeProperty(ns("gr") + "#hasValue");
		l = literal(model, contract.getRoomsInfo().get(0), XSDDatatype.XSDint);
		model.add(qvi, dtProp, l);
		
		dtProp = model.getDatatypeProperty(ns("gr") + "#hasUnitOfMeasurement");
		l = literal(model, "C62", XSDDatatype.XSDstring);
		model.add(qvi, dtProp, l);
		
		// Konfiguracija soba
		objectProperty = model.getObjectProperty(ns("base") + "#Rooms_configuration");
		for (int i=1; i < contract.getRoomsInfo().size(); ++i) {
			Long beds = contract.getRoomsInfo().get(i).longValue();
			
			if (beds == 0) {
				continue;
			}
			
			Individual bedDetailsInd = createIndividual(model, ns("acco") + "#BedDetails", "BedDetails_" + contract.getId() + "_" + i);
			dtProp = model.getDatatypeProperty(ns("base") + "#beds");
			l = literal(model, beds, XSDDatatype.XSDint);
			model.add(bedDetailsInd, dtProp, l);
			dtProp = model.getDatatypeProperty(ns("acco") + "#quantity");
			l = literal(model, i, XSDDatatype.XSDinteger);
			model.add(bedDetailsInd, dtProp, l);
			model.add(offeringInd, objectProperty, bedDetailsInd);
		}
	
		// Contract object --- Contract economic conditions --->
		// 	Contract economic conditions 
		
		Individual cecInd = createIndividual(model, ns("pproc") + "#ContractEconomicConditions", "ContractEconomicConditions_" + contract.getId());
		ObjectProperty cecProp = model.getObjectProperty(ns("pproc") + "#contractEconomicConditions");
		model.add(contractInd, cecProp, cecInd);
		
		//  (definisati popuste (opis + procenat/umanjena cena))
		//  	Popusti se definisu u okviru niza cena
		//  (Advance_payment, ukupna cena)
		// 		--- Advance Payment --> Payment Charge Specification 
		//			(hasCurrencyValue, hasCurrency) 
		
		objectProperty = model.getObjectProperty(ns("base") + "#Advance_payment");
		Individual pcs = createIndividual(model, ns("gr") + "#PaymentChargeSpecification", null);
		model.add(cecInd, objectProperty, pcs);
		dtProp = model.getDatatypeProperty(ns("gr") + "#hasCurrencyValue");
		l = literal(model, contract.getAdvancePayment(), XSDDatatype.XSDfloat);
		model.add(pcs, dtProp, l);
		dtProp = model.getDatatypeProperty(ns("gr") + "#hasCurrency");
		l = literal(model, "WEI", XSDDatatype.XSDstring);
		model.add(pcs, dtProp, l);
		
		//		---  Estimated value of the contract --> Bundle price
		//			(gr:hasCurrencyValue, gr:hasCurrency)
		objectProperty = model.getObjectProperty(ns("pproc") + "#estimatedValue");
		Individual bundlePriceInd = createIndividual(model, ns("pproc") + "#BundlePriceSpecification", null);
		model.add(cecInd, objectProperty, bundlePriceInd);
		dtProp = model.getDatatypeProperty(ns("gr") + "#hasCurrencyValue");
		
		Date startDate = new Date(contract.getStartDate().longValue());
		Date endDate = new Date(contract.getEndDate().longValue());
		Long contractDuration = getDateDiffDays(startDate, endDate);
		Double estimatedValue = new Double(contractDuration * contract.getRoomsInfo().get(0).longValue());
		
		l = literal(model, estimatedValue, XSDDatatype.XSDfloat);
		model.add(bundlePriceInd, dtProp, l);
		dtProp = model.getDatatypeProperty(ns("gr") + "#hasCurrency");
		l = literal(model, "WEI", XSDDatatype.XSDstring);
		model.add(bundlePriceInd, dtProp, l);
		
		// Contract object --- Contract temporal conditions ---> 
		//	Contract temporal conditions
		
		Individual ctcInd = createIndividual(model, ns("pproc") + "#ContractTemporalConditions", "ContractTemporalConditions_" + contract.getId());
		ObjectProperty ctcProp = model.getObjectProperty(ns("pproc") + "#contractTemporalConditions");
		model.add(contractInd, ctcProp, ctcInd);
		
		//  (Estimated end date, dodati Actual_start_date, 
		//   MainSeasonStartDate, MainSeasonEndDate)
		
		dtProp = model.createDatatypeProperty(ns("pproc") + "#estimatedEndDate");
		l = literal(model, new XSDDateTime(toCalendar(endDate)), XSDDatatype.XSDdateTime);
		model.add(ctcInd, dtProp, l);

		dtProp = model.createDatatypeProperty(ns("base") + "#Actual_start_date");
		l = literal(model, new XSDDateTime(toCalendar(startDate)), XSDDatatype.XSDdateTime);
		model.add(ctcInd, dtProp, l);
		
		Date mainSeasonStartDate = new Date(contract.getSomeContrains().get(4).longValue());
		dtProp = model.createDatatypeProperty(ns("base") + "#Main_season_start_date");
		l = literal(model, new XSDDateTime(toCalendar(mainSeasonStartDate)), XSDDatatype.XSDdateTime);
		model.add(ctcInd, dtProp, l);
		
		Date mainSeasonEndDate = new Date(contract.getSomeContrains().get(5).longValue());
		dtProp = model.createDatatypeProperty(ns("base") + "#Main_season_end_date");
		l = literal(model, new XSDDateTime(toCalendar(mainSeasonEndDate)), XSDDatatype.XSDdateTime);
		model.add(ctcInd, dtProp, l);
		
		// Contract object --- Contract additional obligations ---> 
		//  Contract additional obligations
		
		Individual caoInd = createIndividual(model, ns("pproc") + "#ContractAdditionalObligations", "ContractAdditionalObligations_" + contract.getId());
		ObjectProperty caoProp = model.getObjectProperty(ns("pproc") + "#contractAdditionalObligations");
		model.add(contractInd, caoProp, caoInd);
		
		//  (Final financial guarantee = provizija)
		dtProp = model.createDatatypeProperty(ns("pproc") + "#finalFinancialGuarantee");
		l = literal(model, contract.getCommision().floatValue(), XSDDatatype.XSDfloat);
		model.add(caoInd, dtProp, l);
		
		// Cene
		// Pun pansion
		// Polupansion
		// Van sezone
		// Deca
		
		DatatypeProperty hasCurrencyValueProp = model.getDatatypeProperty(ns("gr") + "#hasCurrencyValue");
		DatatypeProperty hasCurrencyProp = model.getDatatypeProperty(ns("gr") + "#hasCurrency");
		
		ObjectProperty fbProp = model.getObjectProperty(ns("base") + "#Full_board_price");
		Individual fbUnitPriceSpecInd = createIndividual(model, ns("gr") + "#UnitPriceSpecification", null);
		l = literal(model, contract.getPrices().get(1), XSDDatatype.XSDfloat);
		model.add(fbUnitPriceSpecInd, hasCurrencyValueProp, l);
		l = literal(model, "WEI", XSDDatatype.XSDstring);
		model.add(fbUnitPriceSpecInd, hasCurrencyProp, l);
		model.add(cecInd, fbProp, fbUnitPriceSpecInd);
		
		ObjectProperty hbProp = model.getObjectProperty(ns("base") + "#Half_board_price");
		Individual hbUnitPriceSpecInd = createIndividual(model, ns("gr") + "#UnitPriceSpecification", null);
		l = literal(model, contract.getPrices().get(0), XSDDatatype.XSDfloat);
		model.add(hbUnitPriceSpecInd, hasCurrencyValueProp, l);
		l = literal(model, "WEI", XSDDatatype.XSDstring);
		model.add(hbUnitPriceSpecInd, hasCurrencyProp, l);
		model.add(cecInd, hbProp, hbUnitPriceSpecInd);
		
		ObjectProperty osProp = model.getObjectProperty(ns("base") + "#Offseason_price");
		Individual osUnitPriceSpecInd = createIndividual(model, ns("gr") + "#UnitPriceSpecification", null);
		l = literal(model, contract.getPrices().get(2), XSDDatatype.XSDfloat);
		model.add(osUnitPriceSpecInd, hasCurrencyValueProp, l);
		l = literal(model, "WEI", XSDDatatype.XSDstring);
		model.add(osUnitPriceSpecInd, hasCurrencyProp, l);
		model.add(cecInd, osProp, osUnitPriceSpecInd);
		
		ObjectProperty kidsProp = model.getObjectProperty(ns("base") + "#Kids_price");
		Individual kidsUnitPriceSpecInd = createIndividual(model, ns("gr") + "#UnitPriceSpecification", null);
		l = literal(model, contract.getPrices().get(3), XSDDatatype.XSDfloat);
		model.add(kidsUnitPriceSpecInd, hasCurrencyValueProp, l);
		l = literal(model, "WEI", XSDDatatype.XSDstring);
		model.add(kidsUnitPriceSpecInd, hasCurrencyProp, l);
		model.add(cecInd, kidsProp, kidsUnitPriceSpecInd);
		
		// Period obaveštavanja
		dtProp = model.createDatatypeProperty(ns("base") + "#informing_period");
		l = literal(model, contract.getPeriods().get(0), XSDDatatype.XSDint);
		model.add(caoInd, dtProp, l);
		
		// Period za odustajanje
		dtProp = model.createDatatypeProperty(ns("base") + "#withdrawal_period");
		l = literal(model, contract.getPeriods().get(1), XSDDatatype.XSDint);
		model.add(caoInd, dtProp, l);
		
		// Nadoknada po krevetu u slučaju neblagovremenog raskida 
		// ili nepoštovanja ugovora - Charge fee
		objectProperty = model.createObjectProperty(ns("base") + "#Charge_fee");
		Individual priceSpecInd = createIndividual(model, ns("gr") + "#PriceSpecification", null);
		model.add(cecInd, objectProperty, priceSpecInd);
		
		dtProp = model.getDatatypeProperty(ns("gr") + "#hasCurrencyValue");
		l = literal(model, contract.getFinePerBed(), XSDDatatype.XSDfloat);
		model.add(priceSpecInd, dtProp, l);
		
		dtProp = model.getDatatypeProperty(ns("gr") + "#hasCurrency");
		l = literal(model, "WEI", XSDDatatype.XSDstring);
		model.add(priceSpecInd, dtProp, l);
		
		// Public contract --- Blockchain contract --> Contract Account (address)
		ObjectProperty blockchainContractOP = model.getObjectProperty(ontBean.getNamespaces().get("base") + "#blockchain_contract");
		classStr =  ontBean.getNamespaces().get("ethon") + "ContractAccount";
		clazz = model.getOntClass(classStr);
		Individual blockchainContractInd = createIndividual(model, 
				classStr,
				"Nalog_ugovora_" + contract.getId());
		model.add(blockchainContractInd, RDF.type, clazz);
		model.add(contractInd, blockchainContractOP, blockchainContractInd); 
		
		Contract c = service.findById(contract.getId());
		
		tempStr = ontBean.getNamespaces().get("ethon") + "address";
		Property addressProp = model.getProperty(tempStr);
		//DatatypeProperty addressProp = model.create(tempStr);
		l = model.createTypedLiteral(c.getAddress(), XSDDatatype.XSDhexBinary);
		model.add(blockchainContractInd, addressProp, l);
		
		ontBean.writeModel(model);
		
		
		return new ResponseEntity<String>("SW: Added successfully", HttpStatus.OK);
	}
	
	private Individual createIndividual(OntModel model, String clazz, String name) {
		logger.debug(clazz);
		logger.debug(name);
		OntClass c = model.getOntClass(clazz);
		
		Individual ind = model.createIndividual((name!=null) ? ontBean.getNamespaces().get("base") + "#" + name : null, c);
		//model.add(ind, RDF.type, clazz);
		return ind;
	}
	
	private String ns(String ontology) {
		return ontBean.getNamespaces().get(ontology);
	}
	
	private Literal literal(OntModel model, Object obj, RDFDatatype dtype) {
		return model.createTypedLiteral(obj, dtype);
	}
	
	private Long getDateDiffDays(Date date1, Date date2) {
	    long diffInMillies = date2.getTime() - date1.getTime();
	    return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
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
	
}
