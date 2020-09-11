package rs.ac.uns.ftn.informatics.semantic_web;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public @Data class OntologyBean {
	
	final static Logger logger = LoggerFactory.getLogger(OntologyBean.class);
	
	private static String ONT_FILE_NAME = "allotment_ont_29_08.owl";
	
	private HashMap<String, String> namespaces = new HashMap<String, String>() {
		private static final long serialVersionUID = -623596213485472886L;

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
	
	private OntModel ontModel;
	
	public Individual createIndividual(OntModel model, String clazz, String name) {
		logger.debug(clazz);
		logger.debug(name);
		OntClass c = model.getOntClass(clazz);
		Individual ind = model.createIndividual(namespaces.get("base") + "#" + name, c);
		//model.add(ind, RDF.type, clazz);
		return ind;
	}
	
	public void writeModel(OntModel m) { 
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
			m.getWriter("TURTLE").write(m, out, namespaces.get("base") + "#");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
	}
	
	public void writeModel(String filename) { 
		File file = new File(SemanticWebApplication.class.getClassLoader().getResource(".").getFile() + "/../../src/main/resources/" + filename + ".owl");
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
			ontModel.getWriter("TURTLE").write(ontModel, out, namespaces.get("base") + "#");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
	}
	
	public OntModel getOntModel() {
		if (ontModel == null) {
			ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
			InputStream is = SemanticWebApplication.class.getClassLoader().getResourceAsStream(ONT_FILE_NAME);
		
			try {
				ontModel.read(is, "RDF/XML");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			
			ontModel.loadImports();
		}
		
		
		return ontModel;
		
	}

}
