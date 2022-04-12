package gr.nikos.smartclideTDPrincipal.Parser;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities.Class;
import gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities.JavaFile;
import gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities.MethodCallSet;
import gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities.Project;
import gr.nikos.smartclideTDPrincipal.Parser.visitors.ClassVisitor;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

public final class InvestigatorFacade {

    private final Project project;
    private final String startingFile;
    private final MethodDeclaration startingMethod;

    private final Set<MethodCallSet> methodCallSets = new HashSet<>();

    public InvestigatorFacade(String projectDir, String startingFile, MethodDeclaration startingMethod) {
        this.project = new Project(projectDir);
        this.startingFile = startingFile;
        this.startingMethod = startingMethod;
    }

    public InvestigatorFacade(String projectDir, String startingFile) {
        project = new Project(projectDir);
        this.startingFile = startingFile;
        this.startingMethod = null;
    }

    /**
     * Starts the whole process
     *
     * @return a set containing method call sets if operation successful, null otherwise
     */
    public Set<MethodCallSet> start() {
        ProjectRoot projectRoot = getProjectRoot();
        List<SourceRoot> sourceRoots = projectRoot.getSourceRoots();
        try {
            createSymbolSolver();
        } catch (IllegalStateException e) {
            return null;
        }
        if (createFileSet(sourceRoots) == 0)
            return null;
        startCalculations(sourceRoots);
        return this.methodCallSets;
    }

    /**
     * Get the project root
     * @return the ProjectRoot object
     */
    private ProjectRoot getProjectRoot() {
        return new SymbolSolverCollectionStrategy()
                .collect(Paths.get(project.getClonePath()));
    }

    /**
     * Create the symbol solver
     * that will be used to identify
     * user-defined classes
     */
    private void createSymbolSolver() {
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(project.getClonePath()));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(javaParserTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration
                .setSymbolResolver(symbolSolver)
                .setAttributeComments(false).setDetectOriginalLineSeparator(true);
        StaticJavaParser
                .setConfiguration(parserConfiguration);
    }

    /**
     * Creates the file set (add appropriate classes)
     * @param sourceRoots the source roots of project
     *
     * @return size of the file set (int)
     */
    private int createFileSet(List<SourceRoot> sourceRoots) {
        try {
            sourceRoots
                    .forEach(sourceRoot -> {
                        try {
                            sourceRoot.tryToParse()
                                    .stream()
                                    .filter(res -> res.getResult().isPresent())
                                    .filter(cu -> cu.getResult().get().getStorage().isPresent())
                                    .forEach(cu -> {
                                        try {
                                            project.getJavaFiles().add(new JavaFile(cu.getResult().get(), cu.getResult().get().getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1),
                                                    cu.getResult().get().getStorage().get().getPath().toString().replace("\\", "/"),
                                                    cu.getResult().get().findAll(ClassOrInterfaceDeclaration.class)
                                                            .stream()
                                                            .filter(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().isPresent())
                                                            .map(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().get())
                                                            .map(Class::new)
                                                            .collect(Collectors.toSet())));
                                        } catch (Throwable ignored) {}
                                    });
                        } catch (Exception ignored) {
                        }
                    });
        } catch (Exception ignored) {
        }
        return project.getJavaFiles().size();
    }

    /**
     * Starts the calculations
     *
     * @param sourceRoots the list of source roots of project
     *
     */
    private void startCalculations(List<SourceRoot> sourceRoots) {
        sourceRoots
                .forEach(sourceRoot -> {
                    try {
                        sourceRoot.tryToParse()
                                .stream()
                                .filter(res -> res.getResult().isPresent())
                                .filter(res -> res.getResult().get().getStorage().isPresent())
                                .filter(res -> res.getResult().get().getStorage().get().getPath().toAbsolutePath().toString().replace("\\", "/").equals(this.startingFile))
                                .forEach(res -> {
                                    analyzeClassOrInterfaces(res.getResult().get());
                                });
                    } catch (Exception ignored) {}
                });
    }

    /**
     * Analyzes the classes (or interfaces) given a compilation unit.
     *
     * @param cu the compilation unit given
     *
     */
    private void analyzeClassOrInterfaces(CompilationUnit cu) {
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cl -> {
            try {
                if (cu.getStorage().isPresent())
                    cl.accept(new ClassVisitor(project, methodCallSets, cu.getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1), this.startingMethod), null);
            } catch (Exception ignored) {}
        });
    }

    public Project getProject() {
        return project;
    }

    public String getStartingFile() {
        return startingFile;
    }

    public MethodDeclaration getStartingMethod() {
        return startingMethod;
    }

    public Set<MethodCallSet> getMethodCallSets() {
        return methodCallSets;
    }

}
