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

import com.google.common.collect.Lists;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.search.SearchRestrictions;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.beans.sample.SampleParentDatabaseObject;
import com.qcadoo.model.beans.sample.SampleSimpleDatabaseObject;
import com.qcadoo.model.internal.api.InternalDataDefinition;
import com.qcadoo.model.internal.types.IntegerType;
import com.qcadoo.model.internal.types.StringType;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import static junit.framework.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.util.Assert.isInstanceOf;

public class EntityServiceImplTest extends DataAccessTest {

    @Test(expected = IllegalStateException.class)
    public void shouldThrownAnExceptionWhileGettingNotExistingField() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(1L);

        FieldDefinition fieldDefinition = new FieldDefinitionImpl(new DataDefinitionImpl("", "", null), "unknown");

        // when
        entityService.getField(databaseEntity, fieldDefinition);
    }

    @Test
    public void shouldReturnNullWhileGettingEmptyField() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(1L);

        // when
        Object value = entityService.getField(databaseEntity, fieldDefinitionName);

        // then
        assertNull(value);
    }

    @Test
    public void shouldReturnProperValueOfTheField() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(1L);
        databaseEntity.setName("Mr T");

        // when
        Object value = entityService.getField(databaseEntity, fieldDefinitionName);

        // then
        assertEquals("Mr T", value);
    }

    @Test
    public void shouldReturnProperValueOfTheBelongsToField() throws Exception {
        // given
        SampleParentDatabaseObject parentDatabaseEntity = new SampleParentDatabaseObject(1L);
        parentDatabaseEntity.setName("Mr X");

        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(2L);
        databaseEntity.setName("Mr T");
        databaseEntity.setBelongsTo(parentDatabaseEntity);

        // when
        Object value = entityService.getField(databaseEntity, fieldDefinitionBelongsTo);

        // then
        isInstanceOf(Entity.class, value);
        assertEquals(Long.valueOf(1), ((Entity) value).getId());
        assertEquals("Mr X", ((Entity) value).getField("name"));
    }

    @Test
    public void shouldNotThrownAnExceptionWhileGettingFieldWithInvalidType() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(1L);
        databaseEntity.setName("Mr T");

        FieldDefinition fieldDefinition = new FieldDefinitionImpl(new DataDefinitionImpl("", "", null), "name").withType(new IntegerType());

        // when
        entityService.getField(databaseEntity, fieldDefinition);
    }

    @Test
    public void shouldReturnClassForGivenDataDefinition() throws Exception {
        // when
        Class<?> clazz = dataDefinition.getClassForEntity();

        // then
        assertEquals(SampleSimpleDatabaseObject.class, clazz);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrownAnExceptionIfClassForGivenDataDefinitionDoesNotExist() throws Exception {
        // given
        DataDefinitionImpl dataDefinition = new DataDefinitionImpl("", "definition", null);
        dataDefinition.setFullyQualifiedClassName("java.lang.SomeUselessNotExistingClass");

        // when
        dataDefinition.getClassForEntity();
    }

    @Test
    public void shouldReturnProperId() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(13L);

        // when
        Long id = entityService.getId(databaseEntity);

        // then
        assertEquals(Long.valueOf(13), id);
    }

    @Test
    public void shouldSetProperId() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject();

        // when
        entityService.setId(databaseEntity, 13L);

        // then
        assertEquals(Long.valueOf(13), databaseEntity.getId());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrownAnExceptionWhileSettingNotExistingField() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(1L);

        FieldDefinition fieldDefinition = new FieldDefinitionImpl(new DataDefinitionImpl("", "", null), "unknown").withType(new StringType());

        // when
        entityService.setField(databaseEntity, fieldDefinition, "XXX");
    }

    @Test
    public void shouldSetEmptyField() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(1L);
        databaseEntity.setName("name");

        // when
        entityService.setField(databaseEntity, fieldDefinitionName, null);

        // then
        assertNull(databaseEntity.getName());
    }

    @Test
    public void shouldSetNotEmptyField() throws Exception {
        // given
        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(1L);
        databaseEntity.setName("name");

        // when
        entityService.setField(databaseEntity, fieldDefinitionName, "XXX");

        // then
        assertEquals("XXX", databaseEntity.getName());
    }

    @Test
    public void shouldSetBelongsToField() throws Exception {
        // given
        Entity parentEntity = new DefaultEntity(dataDefinition, 1L);
        parentEntity.setField("name", "Mr X");

        SampleParentDatabaseObject parentDatabaseEntity = new SampleParentDatabaseObject(1L);
        parentDatabaseEntity.setName("Mr X");

        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(2L);

        given(session.load(SampleParentDatabaseObject.class, 1L)).willReturn(parentDatabaseEntity);

        // when
        entityService.setField(databaseEntity, fieldDefinitionBelongsTo, parentEntity);

        // then
        assertNotNull(databaseEntity.getBelongsTo());
        assertEquals(parentDatabaseEntity, databaseEntity.getBelongsTo());
    }

    @Test
    public void shouldSetNullIfBelongsToFieldIsEmpty() throws Exception {
        // given
        SampleParentDatabaseObject parentDatabaseEntity = new SampleParentDatabaseObject(1L);
        parentDatabaseEntity.setName("Mr X");

        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(2L);
        databaseEntity.setBelongsTo(parentDatabaseEntity);

        // when
        entityService.setField(databaseEntity, fieldDefinitionBelongsTo, null);

        // then
        assertNull(databaseEntity.getBelongsTo());
    }

    @Test
    public void shouldConvertDatabaseEntityIntoGenericOne() throws Exception {
        // given
        SampleParentDatabaseObject parentDatabaseEntity = new SampleParentDatabaseObject(1L);
        parentDatabaseEntity.setName("Mr X");

        SampleParentDatabaseObject lazyParentDatabaseEntity = mock(SampleParentDatabaseObject.class, RETURNS_DEEP_STUBS);
        given(lazyParentDatabaseEntity.getHibernateLazyInitializer().getIdentifier()).willReturn(77L);

        SampleSimpleDatabaseObject databaseEntity = new SampleSimpleDatabaseObject(2L);
        databaseEntity.setAge(12);
        databaseEntity.setName("Mr T");
        databaseEntity.setBelongsTo(parentDatabaseEntity);
        databaseEntity.setLazyBelongsTo(lazyParentDatabaseEntity);

        // when
        Entity genericEntity = entityService.convertToGenericEntity(dataDefinition, databaseEntity);

        // then
        assertNotNull(genericEntity);
        assertEquals(Long.valueOf(2), genericEntity.getId());
        assertEquals(12, genericEntity.getField("age"));
        assertEquals("Mr T", genericEntity.getField("name"));
        isInstanceOf(DefaultEntity.class, genericEntity.getField("belongsTo"));
        assertEquals("Mr X", ((Entity) genericEntity.getField("belongsTo")).getField("name"));
        isInstanceOf(ProxyEntity.class, genericEntity.getField("lazyBelongsTo"));
        assertEquals(Long.valueOf(77), ((Entity) genericEntity.getField("lazyBelongsTo")).getId());
    }

    @Test
    public void shouldConvertDatabaseEntityIntoGenericOneWithHasMany() throws Exception {
        // given
        SampleParentDatabaseObject parentDatabaseEntity = new SampleParentDatabaseObject(1L);
        parentDatabaseEntity.setName("Mr X");

        // when
        Entity genericEntity = entityService.convertToGenericEntity(parentDataDefinition, parentDatabaseEntity);

        // then
        assertNotNull(genericEntity);
        assertEquals(Long.valueOf(1), genericEntity.getId());

        List<Entity> hasManyField = genericEntity.getHasManyField("entities");

        Field field = ReflectionUtils.findField(EntityListImpl.class, "entities");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, hasManyField, Collections.emptyList());

        assertThat(hasManyField, instanceOf(EntityListImpl.class));
        assertEquals(dataDefinition, ReflectionTestUtils.getField(hasManyField, "dataDefinition"));
        assertEquals(Long.valueOf(1), ReflectionTestUtils.getField(hasManyField, "parentId"));
        assertEquals(fieldDefinitionBelongsTo, ReflectionTestUtils.getField(hasManyField, "joinFieldDefinition"));
    }

    @Test
    public void shouldConvertGenericEntityIntoDatabaseOne() throws Exception {
        // given
        Entity genericEntity = new DefaultEntity(dataDefinition, 2L);
        genericEntity.setField("name", "Mr T");
        genericEntity.setField("age", 12);
        genericEntity.setField("belongsTo", 1L);

        SampleParentDatabaseObject parentDatabaseEntity = new SampleParentDatabaseObject(1L);
        parentDatabaseEntity.setName("Mr X");

        given(session.get(SampleParentDatabaseObject.class, 1L)).willReturn(parentDatabaseEntity);
        given(session.load(SampleParentDatabaseObject.class, 1L)).willReturn(parentDatabaseEntity);

        validationService.validateGenericEntity(dataDefinition, genericEntity, null);

        // when
        Object databaseEntity = entityService.convertToDatabaseEntity(dataDefinition, genericEntity, null);

        // then
        assertNotNull(databaseEntity);
        isInstanceOf(SampleSimpleDatabaseObject.class, databaseEntity);
        assertEquals(Long.valueOf(2), ((SampleSimpleDatabaseObject) databaseEntity).getId());
        assertEquals(Integer.valueOf(12), ((SampleSimpleDatabaseObject) databaseEntity).getAge());
        assertEquals("Mr T", ((SampleSimpleDatabaseObject) databaseEntity).getName());
        assertNotNull(((SampleSimpleDatabaseObject) databaseEntity).getBelongsTo());
        assertEquals("Mr X", ((SampleSimpleDatabaseObject) databaseEntity).getBelongsTo().getName());
    }

    @Test
    public void shouldConvertGenericEntityIntoDatabaseOneUsingExistingEntity() throws Exception {
        // given
        Entity genericEntity = new DefaultEntity(dataDefinition, 2L);
        genericEntity.setField("name", "Mr T");
        genericEntity.setField("age", 12);
        genericEntity.setField("belongsTo", 1L);

        SampleSimpleDatabaseObject existingDatabaseEntity = new SampleSimpleDatabaseObject(11L);

        SampleParentDatabaseObject parentDatabaseEntity = new SampleParentDatabaseObject(1L);
        parentDatabaseEntity.setName("Mr X");

        given(session.get(SampleParentDatabaseObject.class, 1L)).willReturn(parentDatabaseEntity);
        given(session.load(SampleParentDatabaseObject.class, 1L)).willReturn(parentDatabaseEntity);

        validationService.validateGenericEntity(dataDefinition, genericEntity, new DefaultEntity(dataDefinition, 2L));

        // when
        Object databaseEntity = entityService.convertToDatabaseEntity(dataDefinition, genericEntity, existingDatabaseEntity);

        // then
        assertNotNull(databaseEntity);
        isInstanceOf(SampleSimpleDatabaseObject.class, databaseEntity);
        assertEquals(Long.valueOf(11), ((SampleSimpleDatabaseObject) databaseEntity).getId());
        assertEquals(Integer.valueOf(12), ((SampleSimpleDatabaseObject) databaseEntity).getAge());
        assertEquals("Mr T", ((SampleSimpleDatabaseObject) databaseEntity).getName());
        assertNotNull(((SampleSimpleDatabaseObject) databaseEntity).getBelongsTo());
        assertEquals("Mr X", ((SampleSimpleDatabaseObject) databaseEntity).getBelongsTo().getName());
    }

    @Test
    public void shouldLazyLoadEntityUsingProxy() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class);

        DefaultEntity entity = new DefaultEntity(dataDefinition, 5L);
        entity.setField("test", "testValue");

        given(dataDefinition.get(5L)).willReturn(entity);

        ProxyEntity proxyEntity = new ProxyEntity(dataDefinition, 5L);

        // when
        String value = (String) proxyEntity.getField("test");

        // then
        assertEquals("testValue", value);
    }

    @Test
    public void shouldLazyLoadEntitiesUsingProxy() throws Exception {
        // given
        InternalDataDefinition dataDefinition = mock(InternalDataDefinition.class, RETURNS_DEEP_STUBS);
        FieldDefinition fieldDefinition = mock(FieldDefinition.class);
        BelongsToType fieldType = mock(BelongsToType.class);

        Entity entity1 = new DefaultEntity(dataDefinition, 1L);
        Entity entity2 = new DefaultEntity(dataDefinition, 2L);

        given(fieldDefinition.getName()).willReturn("joinField");
        given(fieldDefinition.getType()).willReturn(fieldType);
        given(fieldType.getDataDefinition()).willReturn(dataDefinition);
        given(dataDefinition.isEnabled()).willReturn(true);
        given(dataDefinition.getField("joinField")).willReturn(fieldDefinition);
        given(
                dataDefinition.find().createAlias(fieldDefinition.getName(), fieldDefinition.getName())
                        .add(SearchRestrictions.eq(fieldDefinition.getName() + ".id", 5L)).list().getEntities()).willReturn(
                Lists.newArrayList(entity1, entity2));

        List<Entity> entityList = new EntityListImpl(dataDefinition, "joinField", 5L);

        // then
        assertNotNull(entityList);
        assertEquals(2, entityList.size());
        assertThat(entityList, JUnitMatchers.hasItems(entity1, entity2));
    }

    @Test
    public void shouldReturnProxyIdWithoutHittingDatabase() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class);

        ProxyEntity proxyEntity = new ProxyEntity(dataDefinition, 5L);

        // when
        Long id = proxyEntity.getId();

        // then
        assertEquals(Long.valueOf(5), id);
        verify(dataDefinition, never()).get(anyLong());
    }

}
