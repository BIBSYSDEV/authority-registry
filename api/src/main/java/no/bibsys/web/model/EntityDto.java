package no.bibsys.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.XmlRootElement;
import no.bibsys.entitydata.validation.ModelParser;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;

@XmlRootElement
public class EntityDto extends ModelParser {

    private String id;
    private String created;
    private String modified;
    private String path;
    private String body;

    @JsonIgnore
    public String getEtagValue() {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] hash = digest.digest(getBody().getBytes(StandardCharsets.UTF_8));
            String hex = DatatypeConverter.printHexBinary(hash);
            return hex;
        } catch (NoSuchAlgorithmException e) {
            return null;
        }

    }


    @JsonIgnore
    public boolean isIsomorphic(EntityDto other) {
        if(this.body==null && other.getBody()==null){
            return true;
        }
        else if(this.body!=null && other.getBody()!=null){
            Model thisModel = parseModel(getBody(), Lang.JSONLD);
            Model thatModel = parseModel(other.getBody(), Lang.JSONLD);
            return thisModel.isIsomorphicWith(thatModel);
        }
        else {
            // one null and the other is not
            return false;
        }

    }

    @JsonRawValue
    @JsonDeserialize(using = JsonAsStringDeserializer.class)
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void validate() {
        // TODO: validate body
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getCreated() != null ? getCreated().hashCode() : 0);
        result = 31 * result + (getModified() != null ? getModified().hashCode() : 0);
        result = 31 * result + (getPath() != null ? getPath().hashCode() : 0);

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityDto)) {
            return false;
        }

        EntityDto other = (EntityDto) o;
        return Objects.equals(id, other.getId()) &&
            Objects.equals(created, other.getCreated()) &&
            Objects.equals(modified, other.getModified()) &&
            Objects.equals(path, other.getPath()) && (
             isIsomorphic(other)
        );

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    /**
     * Relative path to this resource, set in the API level
     */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "EntityDto [id=" + id + ", created=" + created + ", modified=" + modified + ", body="
            + body + "]";
    }

}
