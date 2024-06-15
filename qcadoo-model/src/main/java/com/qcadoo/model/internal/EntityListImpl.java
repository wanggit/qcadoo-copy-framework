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
package com.qcadoo.model.internal;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityList;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;

public final class EntityListImpl extends AbstractList<Entity> implements EntityList {

    private final DataDefinition dataDefinition;

    private final Long parentId;

    private final FieldDefinition joinFieldDefinition;

    private List<Entity> entities = null;

    public EntityListImpl(final DataDefinition dataDefinition, final String joinFieldName, final Long parentId) {
        super();

        this.dataDefinition = dataDefinition;
        this.joinFieldDefinition = dataDefinition.getField(joinFieldName);
        this.parentId = parentId;

        if (this.parentId == null) {
            entities = Collections.<Entity> emptyList();
        }
    }

    private void loadEntities() {
        if (entities == null) {
            entities = find().list().getEntities();
        }
    }

    @Override
    public SearchCriteriaBuilder find() {
        return dataDefinition.find().createAlias(joinFieldDefinition.getName(), joinFieldDefinition.getName())
                .add(SearchRestrictions.eq(joinFieldDefinition.getName() + ".id", parentId));
    }

    @Override
    public Entity get(final int index) {
        if (entities == null) {
            loadEntities();
        }
        return entities.get(index);
    }

    @Override
    public int size() {
        if (entities == null) {
            loadEntities();
        }
        return entities.size();
    }

    @Override
    public boolean isEmpty() {
        if (entities == null) {
            loadEntities();
        }
        return entities.isEmpty();
    }

    @Override
    public String toString() {
        return "EntityList[" + dataDefinition.getPluginIdentifier() + "." + dataDefinition.getName() + "]["
                + joinFieldDefinition.getName() + "=" + parentId + "]";
    }

}
