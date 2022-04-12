package gr.nikos.smartclideTDPrincipal.Analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import gr.nikos.smartclideTDPrincipal.Parser.InvestigatorFacade;
import gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities.MethodCallSet;
import gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities.MethodDecl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EndpointAnalysisService {

    @Value("${gr.nikos.smartclide.sonarqube.url}")
    private String sonarQubeUrl;

    @Autowired
    private AnalysisService analysisService;

    private HashMap<MethodDeclaration, String> methodsOfStartingEndpoints= new HashMap<>();
    private List<File> allJavaFiles = new ArrayList<>();

    public List<Report> getEnpointMetrics(String url) {
        try {
            methodsOfStartingEndpoints.clear();

            //Get all issues
            String[] temp= url.split("/");
            String gitName= temp[temp.length-1];
            String projectKey= temp[temp.length-2]+":"+temp[temp.length-1];

            //clone
            ProcessBuilder pbuilder1 = new ProcessBuilder("bash", "-c", "git clone " + url);
            Process p1 = pbuilder1.start();
            BufferedReader reader1 = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            String line1;
            while ((line1 = reader1.readLine()) != null) {
                System.out.println(line1);
            }

            // Get Mappings
            System.out.println("Get all endpoints");
            try {
                getMappingsFromAllFiles(new File("/"+gitName));
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Get Report
            List<Report> reportList= GetAllReportForAllEndpoints("/"+gitName, projectKey);

            //delete clone
            FileSystemUtils.deleteRecursively(new File("/"+gitName));

            return  reportList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Report> getEndpointMetricsPrivate(RequestBodyEndpoints requestBodyEndpoints) {
        try {
            methodsOfStartingEndpoints.clear();
            allJavaFiles.clear();
            String projectKey= requestBodyEndpoints.getSonarQubeProjectKey();
            String gitUrl= requestBodyEndpoints.getGitUrl().replace("https://","").replace(".git","");
            String gitToken= requestBodyEndpoints.getGitToken();
            String url= "https://oauth2:" + gitToken + "@" + gitUrl;
            String[] temp= gitUrl.split("/");
            String gitName= temp[temp.length-1];

            // Git configuration
            System.out.println("Clone");
            ProcessBuilder pbuilderGit = new ProcessBuilder("bash", "-c", "git config --global http.sslverify \"false\"");
            Process pGit = pbuilderGit.start();
            BufferedReader inputReaderGit = new BufferedReader(new InputStreamReader(pGit.getInputStream()));
            String inputLineGit;
            while ((inputLineGit = inputReaderGit.readLine()) != null) {
                System.out.println("! " + inputLineGit);
            }

            // Clone
            System.out.println("Clone");
            ProcessBuilder pbuilder1 = new ProcessBuilder("bash", "-c", "git clone " + url);
            Process p1 = pbuilder1.start();
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p1.getErrorStream()));
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                System.out.println("~ " + errorLine);
            }
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(p1.getInputStream()));
            String inputLine;
            while ((inputLine = inputReader.readLine()) != null) {
                System.out.println("! " + inputLine);
            }

            // Get All Java Files
            System.out.println("Get all files");
            getAllFiles(new File("/"+gitName));

            // Get Mappings
            System.out.println("Get all endpoints");
            try {
                getGivenEndpointsFromAllFiles(requestBodyEndpoints.getRequestBodyEachEndpointList());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Get Report
            List<Report> reportList= GetAllReportForAllEndpoints("/"+gitName, projectKey);

            //delete clone
            FileSystemUtils.deleteRecursively(new File("/"+gitName));

            return reportList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Start method tracing and map the issues to endpoints
    private List<Report> GetAllReportForAllEndpoints(String dirName, String projectKey) {
        System.out.println("Get all Issues");
        List<Issue> allIssues= analysisService.getIssues(projectKey);
        //HashMap<String,Report> hashMap=new HashMap<>();
        List<Report> reportList = new ArrayList<>();
        //For each mapping method
        System.out.println("Start method trace");
        for(MethodDeclaration md: methodsOfStartingEndpoints.keySet()){
            //parse and find call tree
            InvestigatorFacade facade = new InvestigatorFacade(dirName, methodsOfStartingEndpoints.get(md), md);
            Set<MethodCallSet> methodCallSets = facade.start();
            if (!Objects.isNull(methodCallSets)){
                printResults(methodCallSets);
                List<Issue> endpointIssues = new ArrayList<Issue>();

                for (MethodCallSet methodCallSet : methodCallSets) {
                    //For each method called
                    for (MethodDecl methodCall : methodCallSet.getMethodCalls()) {
                        //Get issues in this file
                        List<Issue> filteredList = new ArrayList<Issue>();
                        filteredList = allIssues.stream()
                                .filter(issue -> issue.getIssueDirectory().replace(projectKey +":", "").equals(methodCall.getFilePath()))
                                .collect(Collectors.toList());

                        //For each issue
                        for(Issue issue: filteredList) {
                            int startIssueLine = Integer.parseInt(issue.getIssueStartLine());
                            if(methodCall.getCodeRange().getStartLine() <= startIssueLine &&
                                    methodCall.getCodeRange().getEndLine() >= startIssueLine){
                                endpointIssues.add(issue);
                            }
                        }
                    }
                }

                //Add all issues of endpoint
                int total = 0;
                for(Issue issue: endpointIssues){
                    String debtInString =issue.getIssueDebt();
                    if(debtInString.contains("h")){
                        String hoursInString = debtInString.split("h")[0];
                        int hours = Integer.parseInt(hoursInString) *60;
                        debtInString = hours+"min";
                    }
                    total += Integer.parseInt(debtInString.replace("min", ""));
                }

                /*String annotationPath="";
                for(AnnotationExpr n: md.getAnnotations()){
                    if(n.getName().asString().contains("Mapping")){
                        annotationPath= n.toString();
                    }
                }*/

                //hashMap.put(annotationPath+" | "+md.getName().toString(),new Report(new Metric("TD", total), endpointIssues));
                reportList.add(new Report(md.getName().toString(), new Metric("TD", total), endpointIssues));
            }
        }
        return reportList;
    }

    //For each file find given endpoints
    private void getGivenEndpointsFromAllFiles(List<RequestBodyEachEndpoint> requestBodyEachEndpointList) throws FileNotFoundException, IOException {
        for(RequestBodyEachEndpoint eachEndpoint:requestBodyEachEndpointList) {
            for(File file: allJavaFiles) {
                if (file.getName().equals(eachEndpoint.getFileName()) || file.getName().equals(eachEndpoint.getFileName()+".java")) {
                    List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

                    CompilationUnit cu = StaticJavaParser.parse(file);
                    VoidVisitor<List<MethodDeclaration>> methodNameVisitor = new MethodNamePrinterALL();
                    methodNameVisitor.visit(cu, methods);

                    methods.forEach(n -> {
                        if (n.getDeclarationAsString().equals(eachEndpoint.getEndpointMethod())) {
                            System.out.println(n.getDeclarationAsString());
                            methodsOfStartingEndpoints.put(n, file.getAbsolutePath());
                        }
                    });
                }
            }
        }
    }

    private void getAllFiles(File folder){
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (fileEntry.isDirectory()) {
                getAllFiles(fileEntry);
            }
            else if(fileEntry.getName().endsWith(".java")){
                allJavaFiles.add(fileEntry);
            }
        }
    }

    private static class MethodNamePrinterALL extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
            super.visit(md, collector);
            collector.add(md);
        }
    }

    //For each file find endpoints
    private void getMappingsFromAllFiles(File folder) throws FileNotFoundException, IOException {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                getMappingsFromAllFiles(fileEntry);
            } else if(fileEntry.getName().endsWith(".java")) {
                List<MethodDeclaration> methods = new ArrayList<MethodDeclaration>();

                CompilationUnit cu = StaticJavaParser.parse(fileEntry);
                VoidVisitor<List<MethodDeclaration>> methodNameVisitor = new MethodNamePrinter();
                methodNameVisitor.visit(cu, methods);

                methods.forEach(n-> System.out.println(n.getDeclarationAsString()));
                methods.forEach(n-> methodsOfStartingEndpoints.put(n, fileEntry.getAbsolutePath()));
            }
        }
    }

    private static class MethodNamePrinter extends VoidVisitorAdapter<List<MethodDeclaration>> {
        @Override
        public void visit(MethodDeclaration md, List<MethodDeclaration> collector) {
            super.visit(md, collector);
            if(!md.getAnnotationByName("GetMapping").isEmpty() ||
                    !md.getAnnotationByName("PostMapping").isEmpty() ||
                    !md.getAnnotationByName("PutMapping").isEmpty() ||
                    !md.getAnnotationByName("DeleteMapping").isEmpty() ||
                    !md.getAnnotationByName("PatchMapping").isEmpty() ||
                    !md.getAnnotationByName("RequestMapping ").isEmpty() ) {
                collector.add(md);
            }
        }
    }

    private static void printResults(Set<MethodCallSet> results) {
        for (MethodCallSet methodCallSet : results) {
            System.out.printf("Methods involved with %s method: %s", methodCallSet.getMethod().getQualifiedName(), System.lineSeparator());
            for (MethodDecl methodCall : methodCallSet.getMethodCalls()) {
                System.out.print(methodCall.getFilePath() + " | " + methodCall.getQualifiedName());
                System.out.printf(" | StartLine: %d | EndLine: %d%s", methodCall.getCodeRange().getStartLine(), methodCall.getCodeRange().getEndLine(), System.lineSeparator());
            }
            System.out.println();
        }
    }
}
