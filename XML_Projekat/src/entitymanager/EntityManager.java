package entitymanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.TransformerException;

import org.apache.batik.bridge.RelaxedExternalResourceSecurity;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.DocumentDescriptor;
import com.marklogic.client.document.DocumentUriTemplate;
import com.marklogic.client.document.XMLDocumentManager;
import com.marklogic.client.eval.EvalResult;
import com.marklogic.client.eval.EvalResultIterator;
import com.marklogic.client.eval.ServerEvaluationCall;
import com.marklogic.client.io.DOMHandle;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.FileHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.JAXBHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import com.marklogic.client.semantics.GraphManager;
import com.marklogic.client.semantics.RDFMimeTypes;
import com.marklogic.client.semantics.SPARQLMimeTypes;
import com.marklogic.client.semantics.SPARQLQueryDefinition;
import com.marklogic.client.semantics.SPARQLQueryManager;

import conversion.XMLMarshaller;
import model.akt.Akt;
import model.amandman.Amandman;
import model.korisnik.Korisnici;
import transformations.XSLFOTransformator;
import util.CollectionConstants;
import util.ConnectionUtils;
import util.ConnectionUtils.ConnectionProperties;
import util.MetadataExtractor;
import util.OperationType;
import util.SearchResultsUtil;
import util.StringConstants;

public class EntityManager<T, ID extends Serializable> {

	private static DatabaseClient client;
	private static ConnectionProperties props;
	
	public EntityManager()
	{
		super();
		try {
			props = ConnectionUtils.loadProperties();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (props.database.equals("")) {
			client = DatabaseClientFactory.newClient(props.host, props.port, props.user, props.password, props.authType);
		} else {
			client = DatabaseClientFactory.newClient(props.host, props.port, props.database, props.user, props.password, props.authType);
		}
	}
	
	public List<Object> findByKeyWord(String keyword) throws IOException
	{
		List<Object> arl = new ArrayList<Object>();

		
		QueryManager queryManager = client.newQueryManager();
		
		// Query definition is used to specify Google-style query string
		StringQueryDefinition queryDefinition = queryManager.newStringDefinition();
		
		// Set the criteria
		queryDefinition.setCriteria(keyword);
		queryDefinition.setCollections(CollectionConstants.akti);
		
		SearchHandle results = queryManager.search(queryDefinition, new SearchHandle());
		
		// Serialize search results to the standard output
		MatchDocumentSummary matches[] = results.getMatchResults();

		MatchDocumentSummary result;
		
		EvalResultIterator response = null;
		
		for (int i = 0; i < matches.length; i++) {
			result = matches[i];
			
			ServerEvaluationCall invoker = client.newServerEval();
			String query = "declare namespace sk = \"http://www.ftn.uns.ac.rs/skupstina\";"
						   + "let $x := fn:doc(\"###\")/sk:Akt/sk:Naslov return fn:string($x)";
			query = query.replace("###",result.getUri());
			invoker.xquery(query);
			response = invoker.eval();
			String name = "";
			if (response.hasNext()) {
				for (EvalResult rs : response) {
					System.out.println(rs.getString());
					name = rs.getString();
				}
			}
			
			invoker = client.newServerEval();
			query = StringConstants.getExecutable(OperationType.listCollections);
			query = query.replace("replace1",result.getUri());
			invoker.xquery(query);
			response = invoker.eval();
			String collection = "";
			if (response.hasNext()) {
				for (EvalResult rs : response) {
					if(!rs.getString().equals(CollectionConstants.amandmani))
					{
						collection = rs.getString();
						collection = collection.replace("_", " ");
						collection = collection.toUpperCase();
					}
				}
			}
			arl.add(new SearchResultsUtil(name,result.getUri(),collection));
		}
		
		// Release the client
		//client.release();
		return arl;
	}
	public List<Object> findByMetaData(String dateFrom, String dateTo) throws IOException
	{
		List<Object> arl = new ArrayList<Object>();

		String query = "PREFIX xsi: <http://www.w3.org/2001/XMLSchema#>"
				+ "SELECT * FROM <grafovi> WHERE { "
				+ "?s <http://www.ftn.uns.ac.rs/skupstina/predpredlozen> ?date ."
				+ "FILTER ( ?date >= \""+dateFrom+"\"^^xsi:date && ?date < \""+dateTo+"\"^^xsi:date)}";
		
		SPARQLQueryManager sparqlQueryManager = client.newSPARQLQueryManager();
		SPARQLQueryDefinition sqlQuery = sparqlQueryManager.newQueryDefinition(query);
		JacksonHandle resultsHandle = new JacksonHandle();
		resultsHandle.setMimetype(SPARQLMimeTypes.SPARQL_JSON);
		resultsHandle = sparqlQueryManager.executeSelect(sqlQuery, resultsHandle);
		EvalResultIterator response = null;
		JsonNode tuples = resultsHandle.get().path("results").path("bindings");
		for ( JsonNode row : tuples ) {
			String subject = row.path("s").path("value").asText();
			String[] uri = subject.split("/");
			System.out.println(uri[uri.length-1]);
			
			ServerEvaluationCall invoker = client.newServerEval();
			String xQuery = "declare namespace sk = \"http://www.ftn.uns.ac.rs/skupstina\";"
						   + "let $x := fn:doc(\"###\")/sk:Akt/sk:Naslov return fn:string($x)";
			xQuery = xQuery.replace("###",uri[uri.length-1]);
			invoker.xquery(xQuery);
			response = invoker.eval();
			String name = "";
			if (response.hasNext()) {
				for (EvalResult rs : response) {
					System.out.println(rs.getString());
					name = rs.getString();
				}
			}
			invoker = client.newServerEval();
			query = StringConstants.getExecutable(OperationType.listCollections);
			query = query.replace("replace1",uri[uri.length-1]);
			invoker.xquery(query);
			response = invoker.eval();
			String collection = "";
			if (response.hasNext()) {
				for (EvalResult rs : response) {
					if(!rs.getString().equals(CollectionConstants.amandmani))
					{
						collection = rs.getString();
						collection = collection.replace("_", " ");
						collection = collection.toUpperCase();
					}
				}
			}
			arl.add(new SearchResultsUtil(name,uri[uri.length-1],collection));
		}
		
		return arl;
	}
	
	public Object findById(String id, String tipDokumenta) throws IOException, JAXBException, UnsupportedEncodingException
	{
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		JAXBContext context = JAXBContext.newInstance("model."+tipDokumenta);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		JAXBHandle content = new JAXBHandle(context);
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		xmlManager.read(id, metadata, content);
		Object doc = (Object)content.get();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(doc, baos);
		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
		String retVal = XSLFOTransformator.dokumentToHTMLStream(isFromFirstData, tipDokumenta);
		// Release the client
		return retVal;
	}
	
	public byte[] getAktToPDF(String id) throws IOException, JAXBException, UnsupportedEncodingException
	{
		System.out.println("ID: " + id);
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		JAXBContext context = JAXBContext.newInstance("model.akt");
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		JAXBHandle content = new JAXBHandle(context);
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		xmlManager.read(id, metadata, content);
		Object doc = (Object)content.get();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		marshaller.marshal(doc, baos);
		InputStream isFromFirstData = new ByteArrayInputStream(baos.toByteArray());
		byte[] retVal = XSLFOTransformator.aktToPDFStream(isFromFirstData);
		// Release the client
		return retVal;
	}
	
	public List<Object> findAll() throws IOException
	{
		List<Object> arl = new ArrayList<Object>();

		QueryManager queryManager = client.newQueryManager();
		
		// Query definition is used to specify Google-style query string
		StringQueryDefinition queryDefinition = queryManager.newStringDefinition();
		queryDefinition.setCollections(CollectionConstants.akti);
		
		// Set the criteria
		String criteria = "";
		queryDefinition.setCriteria(criteria);
		
		SearchHandle results = queryManager.search(queryDefinition, new SearchHandle());
		
		// Serialize search results to the standard output
		MatchDocumentSummary matches[] = results.getMatchResults();

		MatchDocumentSummary result;
		
		EvalResultIterator response = null;
		
		for (int i = 0; i < matches.length; i++) {
			result = matches[i];
			
			ServerEvaluationCall invoker = client.newServerEval();
			String query = "declare namespace sk = \"http://www.ftn.uns.ac.rs/skupstina\";"
						   + "let $x := fn:doc(\"###\")/sk:Akt/sk:Naslov return fn:string($x)";
			query = query.replace("###",result.getUri());
			invoker.xquery(query);
			response = invoker.eval();
			String name = "";
			if (response.hasNext()) {
				for (EvalResult rs : response) {
					name = rs.getString();
				}
			}
			invoker = client.newServerEval();
			query = StringConstants.getExecutable(OperationType.listCollections);
			query = query.replace("replace1",result.getUri());
			invoker.xquery(query);
			response = invoker.eval();
			String collection = "";
			if (response.hasNext()) {
				for (EvalResult rs : response) {
					if(!rs.getString().equals(CollectionConstants.amandmani))
					{
						collection = rs.getString();
						collection = collection.replace("_", " ");
						collection = collection.toUpperCase();
					}
				}
			}
			arl.add(new SearchResultsUtil(name,result.getUri(),collection));
		}
		
		// Release the client
		return arl;
	}
	
	public InputStream executeQuery(String xQuery, boolean wrap)
	{
		return null;
	}
	
	public void persist(T entity, String id) throws IOException, SAXException, TransformerException
	{
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		
		InputStreamHandle handle = new InputStreamHandle(XMLMarshaller.objectoToXML(entity));
		if(entity instanceof Akt)
		{
			DocumentMetadataHandle metadata = new DocumentMetadataHandle();
			metadata.getCollections().add(CollectionConstants.aktProcedura);
			metadata.getCollections().add(CollectionConstants.akti);
			
			try{
				DocumentDescriptor dsc =  xmlManager.exists(id);
				System.out.println(dsc.getUri());
			}catch(Exception e)
			{
				xmlManager.write(id ,metadata ,handle);
				String xmlFilePath = "temp.xml";
				String rdfFilePath = "rdf.rdf";
				
				
				MetadataExtractor metadataExtractor = new MetadataExtractor();
				
				GraphManager graphManager = client.newGraphManager();
				
				// Set the default media type (RDF/XML)
				graphManager.setDefaultMimetype(RDFMimeTypes.RDFXML);
				
				XMLMarshaller.objectToFile(entity);
				
				metadataExtractor.extractMetadata(
						new FileInputStream(new File(xmlFilePath)), 
						new FileOutputStream(new File(rdfFilePath)));
				
				FileHandle rdfFileHandle =
						new FileHandle(new File(rdfFilePath))
						.withMimetype(RDFMimeTypes.RDFXML);
				
				String SPARQL_NAMED_GRAPH_URI = "grafovi";
				graphManager.merge(SPARQL_NAMED_GRAPH_URI, rdfFileHandle);
			}
			

		}else if(entity instanceof Amandman)
		{
			DocumentMetadataHandle metadata = new DocumentMetadataHandle();
			metadata.getCollections().add(CollectionConstants.amandmanProcedura);
			metadata.getCollections().add(CollectionConstants.amandmani);
			DocumentUriTemplate template = xmlManager.newDocumentUriTemplate("xml");
			template.setDirectory(id+"/");
			DocumentDescriptor desc = xmlManager.create(template, metadata, handle);
		}
		
	}
	public void delete(String resourceId) throws IOException
	{
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		xmlManager.delete(resourceId);
		String xQuery = "xquery version \"1.0-ml\";"
				+ "import module namespace sem = \"http://marklogic.com/semantics\" "
				+ "  at \"/MarkLogic/semantics.xqy\";"
				+ "let $triples := cts:triples(sem:iri(\"http://www.ftn.uns.ac.rs/skupstina/"+resourceId+"\"),()())"
				+ "for $triple in $triples return (sem:database-nodes($triple) ! xdmp:node-delete(.))";
		
		ServerEvaluationCall invoker = client.newServerEval();
		
		invoker.xquery(xQuery);
		
		// Interpret the results
		EvalResultIterator response = invoker.eval();
		
	}
	
	public void update(String resourceId) throws IOException, JAXBException, XMLStreamException
	{
		
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		JAXBContext context = JAXBContext.newInstance("model.amandman");
		JAXBHandle content = new JAXBHandle(context);
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		xmlManager.read(resourceId, metadata, content);
		Amandman doc = (Amandman)content.get();
		String query = "";
		switch(doc.getOperacija())
		{
		case "deleteNode":
			query = StringConstants.getExecutable(OperationType.deleteNode);
			query = query.replace("replace1", doc.getKontekst().getReferentniZakon());
			query = query.replace("replace2", doc.getKontekst().getValue() );
			break;
		case "insertBefore":
			query = StringConstants.getExecutable(OperationType.insertBefore);
			query = query.replace("replace1", doc.getKontekst().getReferentniZakon());
			query = query.replace("replace2",doc.getKontekst().getValue() );
			query = query.replace("replace3", XMLMarshaller.amandmanConversion(doc));
			break;
		case "insertAfter":
			query = StringConstants.getExecutable(OperationType.insertAfter);
			query = query.replace("replace1", doc.getKontekst().getReferentniZakon());
			query = query.replace("replace2",doc.getKontekst().getValue() );
			query = query.replace("replace3", XMLMarshaller.amandmanConversion(doc));
			break;
		case "insertChild":
			query = StringConstants.getExecutable(OperationType.insertChild);
			query = query.replace("replace1", doc.getKontekst().getReferentniZakon());
			query = query.replace("replace2",doc.getKontekst().getValue() );
			query = query.replace("replace3", XMLMarshaller.amandmanConversion(doc));
			break;
		case "replaceNode":
			query = StringConstants.getExecutable(OperationType.replaceNode);
			query = query.replace("replace1", doc.getKontekst().getReferentniZakon());
			query = query.replace("replace2", doc.getKontekst().getValue() );
			query = query.replace("replace3", XMLMarshaller.amandmanConversion(doc));
			break;
		}
		ServerEvaluationCall invoker = client.newServerEval();
		invoker.xquery(query);
		
		EvalResultIterator response = invoker.eval();
		// menjanje amandmanove kolekcije nakon sto se izvrsio
		metadata.getCollections().clear();
		metadata.getCollections().add(CollectionConstants.amandmanPrihvacen);
		metadata.getCollections().add(CollectionConstants.amandmani);
		InputStreamHandle handle = new InputStreamHandle(XMLMarshaller.objectoToXML(doc));
		xmlManager.write(resourceId ,metadata ,handle);
	}
	
	public void changeCollection(String id, String[] Collections) throws IOException
	{

		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		DOMHandle content = new DOMHandle();
		xmlManager.read(id, metadata, content);
		metadata.getCollections().clear();
		metadata.getCollections().addAll(Collections);
		xmlManager.write(id, metadata, content);
	}
	public List<Object> findAllAmandmani() throws IOException
	{
		List<Object> arl = new ArrayList<Object>();

		
		QueryManager queryManager = client.newQueryManager();
		
		// Query definition is used to specify Google-style query string
		StringQueryDefinition queryDefinition = queryManager.newStringDefinition();
		queryDefinition.setCollections(CollectionConstants.amandmani);
		
		// Set the criteria
		String criteria = "";
		queryDefinition.setCriteria(criteria);
		
		SearchHandle results = queryManager.search(queryDefinition, new SearchHandle());
		
		// Serialize search results to the standard output
		MatchDocumentSummary matches[] = results.getMatchResults();

		MatchDocumentSummary result;
		EvalResultIterator response = null;
		for (int i = 0; i < matches.length; i++) {
			result = matches[i];
			String uri = result.getUri();
			String[] split = uri.split("/");
			String name = split[0].toUpperCase();
			name = name.replace("_", " ");
			name = name + " - " + split[1];
			
			ServerEvaluationCall invoker = client.newServerEval();
			String query = StringConstants.getExecutable(OperationType.listCollections);
			query = query.replace("replace1",result.getUri());
			invoker.xquery(query);
			response = invoker.eval();
			String collection = "";
			if (response.hasNext()) {
				for (EvalResult rs : response) {
					if(!rs.getString().equals(CollectionConstants.amandmani))
					{
						collection = rs.getString();
						collection = collection.replace("_", " ");
						collection = collection.toUpperCase();
					}
				}
			}
			arl.add(new SearchResultsUtil(name,uri,collection));
		}
		
		// Release the client
		return arl;
	}
	public Korisnici getKorisnici() throws JAXBException
	{
		XMLDocumentManager xmlManager = client.newXMLDocumentManager();
		JAXBContext context = JAXBContext.newInstance("model.korisnik");
		JAXBHandle content = new JAXBHandle(context);
		DocumentMetadataHandle metadata = new DocumentMetadataHandle();
		xmlManager.read("korisnici.xml", metadata, content);
		Korisnici doc = (Korisnici)content.get();
		return doc;
	}
}
