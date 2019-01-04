package no.bibsys.authorization;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, ALL;


    @Override
    public String toString() {
        if (this.equals(HttpMethod.ALL)) {
            return "*";
        } else {
            return super.toString();
        }
    }


}