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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.util.Assert.notNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.beans.sample.SampleSimpleDatabaseObject;

public class PriorityTest extends DataAccessTest {

    @Before
    public void init() {
        dataDefinition.addPriorityField(fieldDefinitionPriority);
    }

    @Test
    public void shouldBePrioritizable() throws Exception {
        // then
        assertTrue(dataDefinition.isPrioritizable());
    }

    @Test
    public void shouldHasPriorityField() throws Exception {
        // then
        notNull(dataDefinition.getField("priority"));
    }

    @Test
    public void shouldAddPriorityToEntityOnCreate() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition);
        entity.setField("priority", 13);
        entity.setField("belongsTo", 1L);

        given(criteria.uniqueResult()).willReturn(10);

        // when
        entity = dataDefinition.save(entity);

        // then
        assertEquals(11, entity.getField("priority"));
    }

    @Test
    public void shouldChangeEntitiesWithPriorityGreaterThatDeleted() throws Exception {
        // given
        SampleSimpleDatabaseObject existingDatabaseObject = new SampleSimpleDatabaseObject(1L);
        existingDatabaseObject.setPriority(11);

        SampleSimpleDatabaseObject otherDatabaseObject = new SampleSimpleDatabaseObject(2L);
        otherDatabaseObject.setPriority(12);

        given(session.get(SampleSimpleDatabaseObject.class, 1L)).willReturn(existingDatabaseObject);
        given(criteria.list()).willReturn(Lists.newArrayList(otherDatabaseObject));

        // when
        dataDefinition.delete(1L);

        // then
        SampleSimpleDatabaseObject updatedDatabaseObject = new SampleSimpleDatabaseObject(2L);
        updatedDatabaseObject.setPriority(11);

        verify(session).update(updatedDatabaseObject);

        SampleSimpleDatabaseObject deletedDatabaseObject = new SampleSimpleDatabaseObject(1L);
        deletedDatabaseObject.setPriority(11);

        verify(session).delete(deletedDatabaseObject);
    }

    @Test
    public void shouldChangeEntitiesBetweenCurrentAndTargetPriorityWhileMoving() throws Exception {
        // given
        SampleSimpleDatabaseObject existingDatabaseObject = new SampleSimpleDatabaseObject(1L);
        existingDatabaseObject.setPriority(5);

        SampleSimpleDatabaseObject otherDatabaseObject = new SampleSimpleDatabaseObject(2L);
        otherDatabaseObject.setPriority(6);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(existingDatabaseObject);
        given(criteria.uniqueResult()).willReturn(6);
        given(criteria.list()).willReturn(Lists.newArrayList(otherDatabaseObject));

        // when
        dataDefinition.move(1L, 1);

        // then
        SampleSimpleDatabaseObject movedDatabaseObject = new SampleSimpleDatabaseObject(1L);
        movedDatabaseObject.setPriority(6);

        verify(session).update(movedDatabaseObject);

        SampleSimpleDatabaseObject updatedDatabaseObject = new SampleSimpleDatabaseObject(2L);
        updatedDatabaseObject.setPriority(5);

        verify(session).update(updatedDatabaseObject);
    }

    @Test
    public void shouldChangeEntitiesBetweenCurrentAndTargetPriorityWhileMovingTo() throws Exception {
        // given
        SampleSimpleDatabaseObject existingDatabaseObject = new SampleSimpleDatabaseObject(1L);
        existingDatabaseObject.setPriority(5);

        SampleSimpleDatabaseObject otherDatabaseObject = new SampleSimpleDatabaseObject(2L);
        otherDatabaseObject.setPriority(6);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(existingDatabaseObject);
        given(criteria.uniqueResult()).willReturn(6);
        given(criteria.list()).willReturn(Lists.newArrayList(otherDatabaseObject));

        // when
        dataDefinition.moveTo(1L, 6);

        // then
        SampleSimpleDatabaseObject movedDatabaseObject = new SampleSimpleDatabaseObject(1L);
        movedDatabaseObject.setPriority(6);

        verify(session).update(movedDatabaseObject);

        SampleSimpleDatabaseObject updatedDatabaseObject = new SampleSimpleDatabaseObject(2L);
        updatedDatabaseObject.setPriority(5);

        verify(session).update(updatedDatabaseObject);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotMoveToPositionBelowOne() throws Exception {
        // when
        dataDefinition.moveTo(1L, -2);
    }

    @Test
    public void shouldNotMoveToOffsetBelowOne() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);
        databaseObject.setPriority(11);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);
        given(criteria.list()).willReturn(Lists.newArrayList());

        // when
        dataDefinition.move(1L, -20);

        // then
        SampleSimpleDatabaseObject movedDatabaseObject = new SampleSimpleDatabaseObject(1L);
        movedDatabaseObject.setPriority(1);

        verify(session).update(movedDatabaseObject);
    }

    @Test
    public void shouldNotMoveToPositionAboveMax() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);
        databaseObject.setPriority(11);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);
        given(criteria.uniqueResult()).willReturn(5);
        given(criteria.list()).willReturn(Lists.newArrayList());

        // when
        dataDefinition.moveTo(1L, 10);

        // then
        SampleSimpleDatabaseObject movedDatabaseObject = new SampleSimpleDatabaseObject(1L);
        movedDatabaseObject.setPriority(5);

        verify(session).update(movedDatabaseObject);
    }

    @Test
    public void shouldNotMoveIfPositionDoesNotChange() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);
        databaseObject.setPriority(11);

        given(criteria.uniqueResult()).willReturn(databaseObject, 11);
        given(criteria.list()).willReturn(Lists.newArrayList());

        // when
        dataDefinition.moveTo(1L, 15);

        // then
        verify(session, never()).update(Mockito.any(SampleSimpleDatabaseObject.class));
    }

    @Test
    public void shouldNotMoveToOffsetAboveMax() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseObject = new SampleSimpleDatabaseObject(1L);
        databaseObject.setPriority(11);

        given(session.get(any(Class.class), Matchers.anyInt())).willReturn(databaseObject);
        given(criteria.uniqueResult()).willReturn(15);
        given(criteria.list()).willReturn(Lists.newArrayList());

        // when
        dataDefinition.moveTo(1L, 20);

        // then
        SampleSimpleDatabaseObject movedDatabaseObject = new SampleSimpleDatabaseObject(1L);
        movedDatabaseObject.setPriority(15);

        verify(session).update(movedDatabaseObject);
    }
}
