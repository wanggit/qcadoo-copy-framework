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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.getField;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.EntityList;
import com.qcadoo.model.api.EntityTreeNode;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.SearchCriteriaBuilder;
import com.qcadoo.model.api.search.SearchOrders;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.internal.api.DataAccessService;
import com.qcadoo.model.internal.api.InternalDataDefinition;

public class EntityTreeImplTest {

    private DataAccessService dataAccessService;

    @Before
    public void init() {
        dataAccessService = mock(DataAccessService.class);
        SearchRestrictions restrictions = new SearchRestrictions();
        ReflectionTestUtils.setField(restrictions, "dataAccessService", dataAccessService);
    }

    @Test
    public void shouldBeEmptyIfParentIdIsNull() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class);
        EntityListImpl list = new EntityListImpl(dataDefinition, "tree", null);

        // then
        assertTrue(list.isEmpty());
    }

    @Test
    public void shouldLoadEntities() throws Exception {
        // given
        Entity entity = mock(Entity.class);
        List<Entity> entities = Collections.singletonList(entity);

        BelongsToType fieldType = mock(BelongsToType.class);
        InternalDataDefinition dataDefinition = mock(InternalDataDefinition.class, RETURNS_DEEP_STUBS);
        given(fieldType.getDataDefinition()).willReturn(dataDefinition);
        FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        given(fieldDefinition.getType()).willReturn(fieldType);
        given(fieldDefinition.getName()).willReturn("field");
        given(dataDefinition.getField("tree")).willReturn(fieldDefinition);
        given(
                dataDefinition.find().add(SearchRestrictions.belongsTo("field", dataDefinition, 1L))
                        .addOrder(SearchOrders.asc("priority")).list().getEntities()).willReturn(entities);

        EntityTreeImpl tree = new EntityTreeImpl(dataDefinition, "tree", 1L);

        // then
        assertEquals(1, tree.size());
        assertEquals(entity, tree.get(0));
        assertEquals(entity, getField(tree.getRoot(), "entity"));
    }

    @Test
    public void shouldBuildTree() throws Exception {
        // given
        Entity entity1 = mock(Entity.class);
        given(entity1.getId()).willReturn(1L);

        Entity entity2 = mock(Entity.class);
        given(entity2.getId()).willReturn(2L);
        given(entity2.getBelongsToField("parent")).willReturn(entity1);

        Entity entity3 = mock(Entity.class);
        given(entity3.getId()).willReturn(3L);
        given(entity3.getBelongsToField("parent")).willReturn(entity1);

        Entity entity4 = mock(Entity.class);
        given(entity4.getId()).willReturn(4L);
        given(entity4.getBelongsToField("parent")).willReturn(entity2);

        List<Entity> entities = Arrays.asList(new Entity[] { entity1, entity2, entity3, entity4 });

        BelongsToType fieldType = mock(BelongsToType.class);
        InternalDataDefinition dataDefinition = mock(InternalDataDefinition.class, RETURNS_DEEP_STUBS);
        given(fieldType.getDataDefinition()).willReturn(dataDefinition);
        FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        given(fieldDefinition.getType()).willReturn(fieldType);
        given(fieldDefinition.getName()).willReturn("field");
        given(dataDefinition.getField("tree")).willReturn(fieldDefinition);
        given(
                dataDefinition.find().add(SearchRestrictions.belongsTo("field", dataDefinition, 1L))
                        .addOrder(SearchOrders.asc("priority")).list().getEntities()).willReturn(entities);

        EntityTreeImpl tree = new EntityTreeImpl(dataDefinition, "tree", 1L);

        // when
        EntityTreeNodeImpl root = tree.getRoot();

        // then
        assertEquals(4, tree.size());
        assertEquals(Long.valueOf(1L), root.getId());
        assertEquals(Long.valueOf(2L), root.getChildren().get(0).getId());
        assertEquals(Long.valueOf(3L), root.getChildren().get(1).getId());
        assertEquals(Long.valueOf(4L), root.getChildren().get(0).getChildren().get(0).getId());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfThereAreMultipleRoots() throws Exception {
        // given
        Entity entity1 = mock(Entity.class);
        given(entity1.getId()).willReturn(1L);
        Entity entity2 = mock(Entity.class);
        given(entity2.getId()).willReturn(2L);
        List<Entity> entities = Arrays.asList(new Entity[] { entity1, entity2 });

        BelongsToType fieldType = mock(BelongsToType.class);
        InternalDataDefinition dataDefinition = mock(InternalDataDefinition.class, RETURNS_DEEP_STUBS);
        given(fieldType.getDataDefinition()).willReturn(dataDefinition);
        FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        given(fieldDefinition.getType()).willReturn(fieldType);
        given(fieldDefinition.getName()).willReturn("field");
        given(dataDefinition.getField("tree")).willReturn(fieldDefinition);
        given(
                dataDefinition.find().add(SearchRestrictions.belongsTo("field", dataDefinition, 1L))
                        .addOrder(SearchOrders.asc("priority")).list().getEntities()).willReturn(entities);

        EntityTreeImpl tree = new EntityTreeImpl(dataDefinition, "tree", 1L);

        // when
        tree.size();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfThereIsNoRoot() throws Exception {
        // given
        Entity entity = mock(Entity.class);
        given(entity.getBelongsToField("parent")).willReturn(entity);
        List<Entity> entities = Collections.singletonList(entity);

        BelongsToType fieldType = mock(BelongsToType.class);
        InternalDataDefinition dataDefinition = mock(InternalDataDefinition.class, RETURNS_DEEP_STUBS);
        given(fieldType.getDataDefinition()).willReturn(dataDefinition);
        FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        given(fieldDefinition.getType()).willReturn(fieldType);
        given(fieldDefinition.getName()).willReturn("field");
        given(dataDefinition.getField("tree")).willReturn(fieldDefinition);
        given(
                dataDefinition.find().add(SearchRestrictions.belongsTo("field", dataDefinition, 1L))
                        .addOrder(SearchOrders.asc("priority")).list().getEntities()).willReturn(entities);

        EntityTreeImpl tree = new EntityTreeImpl(dataDefinition, "tree", 1L);

        // when
        tree.size();
    }

    @Test
    public void shouldReturnCriteriaBuilder() throws Exception {
        // given
        BelongsToType fieldType = mock(BelongsToType.class);
        InternalDataDefinition dataDefinition = mock(InternalDataDefinition.class, RETURNS_DEEP_STUBS);
        given(fieldType.getDataDefinition()).willReturn(dataDefinition);
        FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        given(fieldDefinition.getType()).willReturn(fieldType);
        given(fieldDefinition.getName()).willReturn("field");
        given(dataDefinition.getField("tree")).willReturn(fieldDefinition);
        SearchCriteriaBuilder searchCriteriaBuilder = mock(SearchCriteriaBuilder.class);
        given(
                dataDefinition.find().createAlias(fieldDefinition.getName(), fieldDefinition.getName())
                        .add(SearchRestrictions.eq(fieldDefinition.getName() + ".id", 1L))).willReturn(searchCriteriaBuilder);

        EntityList list = new EntityListImpl(dataDefinition, "tree", 1L);

        // then
        assertEquals(searchCriteriaBuilder, list.find());
    }

    @Test
    public void shouldDelegateMethods() throws Exception {
        // given
        Entity entity = mock(Entity.class);
        given(entity.getStringField("entityType")).willReturn("entityType1");

        EntityTreeNode child = mock(EntityTreeNode.class);
        EntityTreeNodeImpl node = new EntityTreeNodeImpl(entity);
        node.addChild(child);

        // then
        assertEquals(1, node.getChildren().size());
        assertEquals(child, node.getChildren().get(0));
        assertEquals("entityType1", node.getEntityNoteType());
    }

}
