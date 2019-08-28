package no.bibsys;

public enum PathElements {
    REGISTRY("registry", 0),
    ENTITY("entity", 2);
    String elementString;
    int expectedPosition;

    PathElements(String elementString, int expectedPosition) {
        this.elementString = elementString;
        this.expectedPosition = expectedPosition;
    }
}
