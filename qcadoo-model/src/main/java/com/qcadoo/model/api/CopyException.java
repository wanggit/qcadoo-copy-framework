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
package com.qcadoo.model.api;

/**
 * An <code>CopyException</code> is thrown by an DataAccessService if copied entity is in corrupted state
 */
@SuppressWarnings("serial")
public final class CopyException extends RuntimeException {

    private final Entity entity;

    /**
     * Creates new <code>CopyException</code> for the specified entity
     * 
     * @param entity
     *            entity in corrupted state
     */
    public CopyException(final Entity entity) {
        super();
        this.entity = entity;
    }

    /**
     * @return entity in corrupted state
     */
    public Entity getEntity() {
        return entity;
    }

}
