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

public class RequestBodyAnalysis {
	
	private String gitURL;
	private String token;
	
	public RequestBodyAnalysis(String gitURL) {
		this.gitURL = gitURL;
	}

	public RequestBodyAnalysis(String gitURL, String token) {
		this.gitURL = gitURL;
		this.token = token;
	}

	public String getGitName() {
		String[] temp= this.gitURL.split("/");
		return temp[temp.length-1];
	}

	public String getGitOwner() {
		String[] temp= this.gitURL.split("/");
		return temp[temp.length-2];
	}
	
	public String getGitURL() {
		return gitURL;
	}

	public String getToken() {
		return token;
	}

}
