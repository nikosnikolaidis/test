package gr.nikos.smartclideTDPrincipal.Analysis;

/*
 * Copyright (C) 2021 UoM - University of Macedonia
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 */

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path= "api/analysis")
public class AnalysisController {
	
	@Autowired
	private AnalysisService analysisService;

	@Autowired
	private EndpointAnalysisService endpointAnalysisService;

	@CrossOrigin(origins = "*")
	@GetMapping(path="{projectKey}/measures")
	public Metric[] getMeasures(@PathVariable(value = "projectKey") String projectKey) {
		return analysisService.getMeasures(projectKey);
	}

	@CrossOrigin(origins = "*")
	@GetMapping(path="{projectKey}/issues")
	public List<Issue> getIssues(@PathVariable(value = "projectKey") String projectKey) {
		return analysisService.getIssues(projectKey);
	}

	@CrossOrigin(origins = "*")
	@GetMapping(path="endpoints")
	public List<Report> getEndpointMetrics(@RequestParam(required = true) String url) {
		return endpointAnalysisService.getEnpointMetrics(url);
	}

	@CrossOrigin(origins = "*")
	@PostMapping(path="endpoints")
	public List<Report> getEndpointMetricsPrivate(@RequestBody RequestBodyEndpoints requestBodyEndpoints) {
		return endpointAnalysisService.getEndpointMetricsPrivate(requestBodyEndpoints);
	}

//	@CrossOrigin(origins = "*")
//	@PostMapping(path="endpoints")
//	public HashMap<String,Report> getEndpointMetricsLocalGitlabSonar(@RequestBody RequestBodyEndpoints requestBodyEndpoints) {
//		return endpointAnalysisService.getEnpointMetricsLocal(requestBodyEndpoints);
//	}

	@CrossOrigin(origins = "*")
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<String> makeNewAnalysis(@RequestBody RequestBodyAnalysis requestBodyAnalysis) {
		analysisService.startNewAnalysis(requestBodyAnalysis);
		return new ResponseEntity<>("finished successful", HttpStatus.OK);
	}

}
