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
package com.qcadoo.model.internal.api;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchResult;
import com.qcadoo.model.internal.MasterModel;
import com.qcadoo.model.internal.search.SearchCriteria;

/**
 * Object defines database structure. This is {@link DataDefinition} extension for internal usage.
 */
public interface InternalDataDefinition extends DataDefinition {

    /**
     * Find endities for this data definition using given search criteria.
     * 
     * @param searchCriteria
     *            search criteria
     * @return search result
     */
    SearchResult find(final SearchCriteria searchCriteria);

    /**
     * Get fully qualified class name representing given data definition.
     * 
     * @return fully qualified class name
     */
    String getFullyQualifiedClassName();

    /**
     * Call view hooks on given entity.
     * 
     * @param entity
     *            entity
     */
    boolean callViewHook(final Entity entity);

    /**
     * Call create hooks on given entity.
     * 
     * @param entity
     *            entity
     */
    boolean callCreateHook(final Entity entity);

    /**
     * Call update hooks on given entity.
     * 
     * @param entity
     *            entity
     */
    boolean callUpdateHook(final Entity entity);

    /**
     * Call save hooks on given entity.
     * 
     * @param entity
     *            entity
     */
    boolean callSaveHook(final Entity entity);

    /**
     * Call delete hooks on given entity.
     * 
     * @param entity
     *            entity
     */
    boolean callDeleteHook(Entity entity);

    /**
     * Get class representing given data definition.
     * 
     * @see #getFullyQualifiedClassName()
     * @return class
     */
    Class<?> getClassForEntity();

    /**
     * Get new instance of class representing given data definition.
     * 
     * @see #getClassForEntity()
     * @return new entity instance
     */
    Object getInstanceForEntity();

    /**
     * Return true if entity of given data definition can be deleted.
     * 
     * @return is deletable
     */
    boolean isDeletable();

    /**
     * Return true if entity of given data definition can be created.
     * 
     * @return is creatable
     */
    boolean isInstertable();

    /**
     * Return true if entity of given data definition can be updated.
     * 
     * @return is updatable
     */
    boolean isUpdatable();

    /**
     * Return true if entity of given data definition can be audited.
     * 
     * @return is auditable
     */
    boolean isAuditable();

    /**
     * Call copy hooks on given entity.
     * 
     * @param targetEntity
     *            entity
     */
    boolean callCopyHook(Entity targetEntity);

    EntityHookDefinition getHook(String type, String className, String methodName);

    String getIdentifierExpression();

    boolean isEnabled();

    void enable();

    void disable();

    MasterModel getMasterModel();

    void setMasterModel(MasterModel masterModel);
}
