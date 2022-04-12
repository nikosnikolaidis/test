package gr.nikos.smartclideTDPrincipal.Analysis;

import java.util.List;

public class Report {
    private String method;
    private Metric metrics;
    private List<Issue> issueList;

    public Report(String method, Metric metrics, List<Issue> issueList) {
        this.method = method;
        this.metrics = metrics;
        this.issueList = issueList;
    }

    public String getMethod() {
        return method;
    }

    public Metric getMetrics() {
        return metrics;
    }

    public List<Issue> getIssueList() {
        return issueList;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setMetrics(Metric metrics) {
        this.metrics = metrics;
    }

    public void setIssueList(List<Issue> issueList) {
        this.issueList = issueList;
    }
}
