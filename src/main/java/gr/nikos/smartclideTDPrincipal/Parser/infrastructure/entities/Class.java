package gr.nikos.smartclideTDPrincipal.Parser.infrastructure.entities;

import java.util.Objects;

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

public class Class {

    private final String qualifiedName;

    public Class(String name) {
        this.qualifiedName = name;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class aClass = (Class) o;
        return Objects.equals(qualifiedName, aClass.qualifiedName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(qualifiedName);
    }

    @Override
    public String toString() {
        return qualifiedName;
    }
}
