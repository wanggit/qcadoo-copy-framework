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
package com.qcadoo.view.internal.components;

import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import org.json.JSONException;

import java.util.Locale;
import java.util.Map;

public final class TextAreaComponentPattern extends FieldComponentPattern {

    private static final String JSP_PATH = "elements/textArea.jsp";

    private static final String JS_OBJECT = "QCD.components.elements.TextArea";

    private int rows = 4;

    public TextAreaComponentPattern(final ComponentDefinition componentDefinition) {
        super(componentDefinition);
    }

    @Override
    protected void initializeComponent() throws JSONException {
        super.initializeComponent();

        for (ComponentOption option : getOptions()) {
            if ("rows".equals(option.getType())) {
                rows = Integer.parseInt(option.getValue());
            } else if (!"labelWidth".equals(option.getType())) {
                throw new IllegalStateException("Unknown option for textarea: " + option.getType());
            }
        }
    }

    @Override
    protected Map<String, Object> getJspOptions(final Locale locale) {
        Map<String, Object> options = super.getJspOptions(locale);

        options.put("rows", rows);

        return options;
    }

    @Override
    public ComponentState getComponentStateInstance() {
        return new TextAreaComponentState(this);
    }

    @Override
    public String getJspFilePath() {
        return JSP_PATH;
    }

    @Override
    public String getJsFilePath() {
        return JS_PATH;
    }

    @Override
    public String getJsObjectName() {
        return JS_OBJECT;
    }

}
