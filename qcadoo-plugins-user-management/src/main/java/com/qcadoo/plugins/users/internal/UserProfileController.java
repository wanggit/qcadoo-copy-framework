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

import java.util.Locale;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.google.common.collect.ImmutableMap;
import com.qcadoo.plugins.users.constants.QcadooUsersConstants;
import com.qcadoo.security.api.SecurityService;
import com.qcadoo.view.api.crud.CrudService;

@Controller
public class UserProfileController {

    @Autowired
    private CrudService crudController;

    @Autowired
    private SecurityService securityService;

    @RequestMapping(value = "userProfile", method = RequestMethod.GET)
    public ModelAndView getAccessDeniedPageView(final Locale locale) {
        JSONObject json = new JSONObject(ImmutableMap.of("form.id", securityService.getCurrentUserId().toString()));
        Map<String, String> arguments = ImmutableMap.of("context", json.toString());
        return crudController.prepareView(QcadooUsersConstants.PLUGIN_IDENTIFIER, QcadooUsersConstants.VIEW_PROFILE, arguments,
                locale);
    }
}
