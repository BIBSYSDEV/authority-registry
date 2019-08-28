package no.bibsys.web.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.bibsys.db.structures.Entity;
import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static java.util.Objects.nonNull;
import static no.bibsys.entitydata.validation.ontology.UnitOntology.SAME_AS;

public class EntityConverter extends BaseConverter {

    private static final String PATH_SEPARATOR = "/";
    private static final Logger logger = LoggerFactory.getLogger(EntityConverter.class);

    public static EntityDto toEntityDto(Entity entity) throws JsonProcessingException {
        EntityDto dto = new EntityDto();
        dto.setId(entity.getId());
        dto.setCreated(entity.getCreated());
        dto.setModified(entity.getModified());
        dto.setBody(toJson(entity.getBody()));
        return dto;
    }

    public static Entity toEntity(EntityDto dto) {
        Entity entity = new Entity();
        entity.setId(dto.getId());
        entity.setCreated(dto.getCreated());
        entity.setModified(dto.getModified());
        entity.setBody(toObjectNode(dto.getBody()));
        return entity;
    }

    public static Entity toEntity(String uri, EntityDto dto) {
        Entity entity = new Entity();
        String id = dto.getId();
        String finalizedId = nonNull(id) ? id : UUID.randomUUID().toString();
        String finalizedUri = uri + PATH_SEPARATOR + finalizedId;
        entity.setId(finalizedId);
        entity.setCreated(dto.getCreated());
        entity.setModified(dto.getModified());
        String body = rewriteBodyWithId(finalizedUri, dto.getBody());
        entity.setBody(toObjectNode(body));
        return entity;
    }

    private static String rewriteBodyWithId(String uri, String dtoBody) {
        Model input = ModelFactory.createDefaultModel();
        InputStream inputStream = IOUtils.toInputStream(dtoBody, StandardCharsets.UTF_8);
        RDFDataMgr.read(input, inputStream, Lang.JSONLD);
        ResIterator subjectIterator = input.listSubjects();

        boolean initialPass = true;
        Statement seeAlsoStatement = null;

        while (subjectIterator.hasNext()) {
            Resource subject = subjectIterator.nextResource();
            Resource replacementUri = ResourceFactory.createResource(uri);
            if (initialPass && !subject.isAnon() && !subject.getURI().equals(replacementUri.getURI())) {
                logger.info("Comparison of URIs");
                logger.info("Replacing {} with {}", subject, replacementUri);
                seeAlsoStatement = ResourceFactory.createStatement(replacementUri, SAME_AS, subject);
            }
            initialPass = false;
            ResourceUtils.renameResource(subject, uri);
        }

        if (nonNull(seeAlsoStatement)) {
            input.add(seeAlsoStatement);
        }

        StringWriter stringWriter = new StringWriter();
        RDFDataMgr.write(stringWriter, input, Lang.JSONLD);
        return stringWriter.toString();
    }
}
