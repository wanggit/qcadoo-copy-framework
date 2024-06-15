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
package com.qcadoo.view.internal.menu.items;

import com.qcadoo.view.internal.menu.MenuItem;

/**
 * Menu item that leads to specified URL.
 */
public final class UrlMenuItem extends MenuItem {

    private final String pageUrl;

    /**
     * @param name
     *            identifier of item
     * @param label
     *            item label to display
     * @param description
     *            item description to display as a tooltip
     * @param pluginIdentifier
     *            plugin identifier of this item
     * @param pageUrl
     *            URL of page that this item leads to
     */
    public UrlMenuItem(final String name, final String label, final String description, final String pluginIdentifier,
            final String pageUrl) {
        super(name, label, description, pluginIdentifier);
        this.pageUrl = pageUrl;
    }

    @Override
    public String getPage() {
        return pageUrl;
    }
}
