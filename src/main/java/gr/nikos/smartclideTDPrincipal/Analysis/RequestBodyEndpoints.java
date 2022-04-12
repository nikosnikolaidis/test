package gr.nikos.smartclideTDPrincipal.Analysis;

import java.util.List;

public class RequestBodyEndpoints {
    private String sonarQubeProjectKey;
    private String gitUrl;
    private String gitToken;
    private List<RequestBodyEachEndpoint> requestBodyEachEndpointList;

    public RequestBodyEndpoints(String sonarQubeProjectKey, String gitUrl, String gitToken, List<RequestBodyEachEndpoint> requestBodyEachEndpointList) {
        this.sonarQubeProjectKey = sonarQubeProjectKey;
        this.gitUrl = gitUrl;
        this.gitToken = gitToken;
        this.requestBodyEachEndpointList= requestBodyEachEndpointList;
    }

    public String getSonarQubeProjectKey() {
        return sonarQubeProjectKey;
    }

    public String getGitUrl() {
        return gitUrl;
    }

    public String getGitToken() {
        return gitToken;
    }

    public List<RequestBodyEachEndpoint> getRequestBodyEachEndpointList() {
        return requestBodyEachEndpointList;
    }

    public void setSonarQubeProjectKey(String sonarQubeProjectKey) {
        this.sonarQubeProjectKey = sonarQubeProjectKey;
    }

    public void setGitUrl(String folderName) {
        this.gitUrl = folderName;
    }

    public void setGitToken(String gitToken) {
        this.gitToken = gitToken;
    }

    public void setRequestBodyEachEndpointList(List<RequestBodyEachEndpoint> requestBodyEachEndpointList) {
        this.requestBodyEachEndpointList = requestBodyEachEndpointList;
    }
}
