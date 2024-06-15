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
package com.qcadoo.view.internal.internal;

import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.view.api.ComponentState.MessageType;
import com.qcadoo.view.internal.states.AbstractComponentState;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MessageHolder {

    private final TranslationService translationService;

    private final Locale locale;

    private final List<Object[]> messages = new ArrayList<Object[]>();

    public MessageHolder(final TranslationService translationService, final Locale locale) {
        this.translationService = translationService;
        this.locale = locale;
    }

    public void addMessage(final String title, final String message, final MessageType type) {
        addMessage(title, message, type, true, false);
    }

    public void addMessage(final String title, final String message, final MessageType type, final boolean autoClose) {
        messages.add(new Object[] { getTranslatedTitle(title, type), message, type, autoClose, false });
    }

    public void addMessage(final String title, final String message, final MessageType type, final boolean autoClose, final boolean extraLarge) {
        messages.add(new Object[] { getTranslatedTitle(title, type), message, type, autoClose, extraLarge});
    }

    private Object getTranslatedTitle(final String title, final MessageType type) {
        if (title != null) {
            return title;
        }
        return translationService.translate("qcadooView.notification." + type.toString().toLowerCase(locale), locale);
    }

    public JSONArray renderMessages() throws JSONException {
        JSONArray json = new JSONArray();

        for (Object[] message : messages) {
            JSONObject jsonMessage = new JSONObject();
            jsonMessage.put(AbstractComponentState.JSON_MESSAGE_TITLE, message[0]);
            jsonMessage.put(AbstractComponentState.JSON_MESSAGE_BODY, message[1]);
            jsonMessage.put(AbstractComponentState.JSON_MESSAGE_TYPE, message[2]);
            jsonMessage.put(AbstractComponentState.JSON_MESSAGE_AUTOCLOSE, message[3]);
            jsonMessage.put(AbstractComponentState.JSON_MESSAGE_EXTRALARGE, message[4]);
            json.put(jsonMessage);
        }

        return json;
    }

}
