package gr.nikos.smartclideTDPrincipal.Analysis;

public class RequestBodyEachEndpoint {
    private String fileName;
    private String endpointMethod;

    public RequestBodyEachEndpoint(String fileDir, String endpointMethod) {
        this.fileName = fileDir;
        this.endpointMethod = endpointMethod;
    }

    public String getFileName() {
        return fileName;
    }

    public String getEndpointMethod() {
        return endpointMethod;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setEndpointMethod(String endpointMethod) {
        this.endpointMethod = endpointMethod;
    }
}
