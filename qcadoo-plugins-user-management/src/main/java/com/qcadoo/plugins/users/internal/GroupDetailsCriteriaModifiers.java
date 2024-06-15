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
package com.qcadoo.plugins.users.internal;

import com.qcadoo.model.api.DataDefinitionService;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.plugins.users.constants.GroupDetailsConstants;
import com.qcadoo.security.constants.QcadooSecurityConstants;
import com.qcadoo.view.api.components.lookup.FilterValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupDetailsCriteriaModifiers {

    @Autowired
    private DataDefinitionService dataDefinitionService;

    public void criteriaForGroups(final SearchCriteriaBuilder scb, final FilterValueHolder filterValue) {
        Long groupId = filterValue.getLong(GroupDetailsConstants.GROUP_ID);
        Entity group = dataDefinitionService.get(QcadooSecurityConstants.PLUGIN_IDENTIFIER, QcadooSecurityConstants.MODEL_GROUP).find().
                add(SearchRestrictions.idEq(groupId)).uniqueResult();
        List<Entity> roles = group.getManyToManyField("roles");
        for (Entity role : roles) {
            scb.add(SearchRestrictions.idNe(role.getId()));
        }
    }
}