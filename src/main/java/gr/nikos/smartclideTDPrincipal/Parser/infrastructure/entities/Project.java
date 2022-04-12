package gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/*
 *
 *  *
 *  *  * Copyright (C) 2021 UoM - University of Macedonia
 *  *  *
 *  *  * This program and the accompanying materials are made available under the
 *  *  * terms of the Eclipse Public License 2.0 which is available at
 *  *  * https://www.eclipse.org/legal/epl-2.0/
 *  *  *
 *  *  * SPDX-License-Identifier: EPL-2.0
 *  *
 *
 */

public class Project {
    private String clonePath;
    private Set<JavaFile> javaFiles;

    public Project(String clonePath, Set<JavaFile> javaFiles) {
        this.clonePath = clonePath;
        this.javaFiles = javaFiles;
    }

    public Project(String clonePath) {
        this.clonePath = clonePath;
        this.javaFiles = ConcurrentHashMap.newKeySet();
    }

    public String getClonePath() {
        return clonePath;
    }

    public void setClonePath(String clonePath) {
        this.clonePath = clonePath;
    }

    public Set<JavaFile> getJavaFiles() {
        return javaFiles;
    }

    public void setJavaFiles(Set<JavaFile> javaFiles) {
        this.javaFiles = javaFiles;
    }
}
