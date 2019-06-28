package no.bibsys.db;

import java.io.InputStream;
import java.io.StringWriter;

import org.apache.http.client.HttpClient;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Selector;
import org.apache.jena.rdf.model.SimpleSelector;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Ignore;
import org.junit.Test;

public class DataLoaderTest {

    @Ignore
    @Test
    public void testLoad1() {
        Model model = ModelFactory.createDefaultModel();
        InputStream ins = ClassLoader.getSystemResourceAsStream("json/unit_real_.json");
        RDFDataMgr.read(model, ins,Lang.JSONLD);
        model.listSubjects().forEachRemaining(subject -> { 
            RDFNode description = model.getRDFNode(subject.asNode());
            String result = "";
            RDFDataMgr.write(System.out, description.getModel(), Lang.JSONLD);
                    }
        );
        
        
    }


    @Ignore
    @Test
    public void testLoad2() {
        Model model = ModelFactory.createDefaultModel();
        InputStream ins = ClassLoader.getSystemResourceAsStream("json/unit_real.nt");
        RDFDataMgr.read(model, ins,Lang.NTRIPLES);
        ResIterator resIterator = model.listSubjects();
        while (resIterator.hasNext()) {
            Resource resource = resIterator.nextResource();
            System.out.println(resource.toString());
            Selector selector = new SimpleSelector(resource, (Property) null, (RDFNode) null);
            StmtIterator listStatements = model.listStatements(selector);
            Model nyModel = ModelFactory.createDefaultModel();
            listStatements.forEachRemaining(statemnt -> nyModel.add(statemnt));
            
            StringWriter result = new StringWriter();
            RDFDataMgr.write(result, nyModel, Lang.JSONLD);
            System.out.println("##########################################");
            HttpClient httpClient = new 
            System.out.println(result.toString());
            
        }
        
        
    }

    
    
    
}
