package no.bibsys.responses;

/**
 * Class representing a REST response containing the path of a created or updated resource.
 */
public class PathResponse {

    private String path;


    public PathResponse() {
    }

    public PathResponse(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PathResponse)) {
            return false;
        }

        PathResponse that = (PathResponse) o;

        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
