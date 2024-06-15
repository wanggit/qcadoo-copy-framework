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
package com.qcadoo.model.integration;

import java.util.Locale;
import java.util.Map;

import com.qcadoo.localization.api.TranslationService;

public class MockTranslationService implements TranslationService {

    @Override
    public String translate(final String code, final Locale locale, final String... args) {
        return code;
    }

    @Override
    public Map<String, String> getMessagesGroup(final String prefix, final Locale locale) {
        return null;
    }

    @Override
    public String translate(final String code, final String secondCode, final Locale locale, final String... args) {
        return code;
    }

    @Override
    public String translate(final String code, final String secondCode, final String thirdCode, final Locale locale,
            final String... args) {
        return code;
    }

    @Override
    public Map<String, String> getLocales() {
        return null;
    }

    @Override
    public int getMaxUploadSize() {
        return 0;
    }
}
