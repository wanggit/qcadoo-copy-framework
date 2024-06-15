/**
 * ***************************************************************************
 * Copyright (c) 2010 Qcadoo Limited
 * Project: Qcadoo Framework
 * Version: 1.4
 *
 * This file is part of Qcadoo.
 *
 * Qcadoo is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * ***************************************************************************
 */
package com.qcadoo.model.beans.sample;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;

import java.util.List;

public class SampleParentDatabaseObject implements HibernateProxy {

    /**
     * 
     */
    private static final long serialVersionUID = -3703171948011903671L;

    private Long id;

    private String name;

    private List<SampleSimpleDatabaseObject> entities;

    private List<SampleTreeDatabaseObject> tree;
    
    private List<SampleSimpleDatabaseObject> manyToMany;

    public SampleParentDatabaseObject() {
    }

    public SampleParentDatabaseObject(final Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    public List<SampleSimpleDatabaseObject> getEntities() {
        return entities;
    }

    public void setEntities(final List<SampleSimpleDatabaseObject> entities) {
        this.entities = entities;
    }

    public List<SampleTreeDatabaseObject> getTree() {
        return tree;
    }

    public void setTree(final List<SampleTreeDatabaseObject> tree) {
        this.tree = tree;
    }

    public List<SampleSimpleDatabaseObject> getManyToMany() {
        return manyToMany;
    }

    public void setManyToMany(List<SampleSimpleDatabaseObject> manyToMany) {
        this.manyToMany = manyToMany;
    }

    @Override
    public Object writeReplace() {
        return null;
    }

    @Override
    public LazyInitializer getHibernateLazyInitializer() {
        return null;
    }

}
