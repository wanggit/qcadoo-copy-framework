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
package com.qcadoo.plugins.qcadooExport.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.qcadoo.plugins.qcadooExport.api.ExportToFileColumns;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.components.GridComponent;

@Service
public class ExportToFileColumnsService implements ExportToFileColumns {

    @Autowired
    private SecurityRolesService securityRolesService;

    public List<String> getColumns(final GridComponent grid) {
        List<String> columns = Lists.newLinkedList();

        grid.getColumns().entrySet().stream().forEach(entry -> {
            String columnAuthorizationRole = entry.getValue().getAuthorizationRole();

            if ((Strings.isNullOrEmpty(columnAuthorizationRole) || securityRolesService.canAccess(columnAuthorizationRole))
                    && !entry.getValue().isHidden()) {
                columns.add(entry.getKey());
            }
        });

        return columns;
    }

}
