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
package com.qcadoo.model.utils;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.Entity;
import com.qcadoo.model.api.ExpressionService;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.internal.DefaultEntity;
import com.qcadoo.model.internal.ExpressionServiceImpl;
import com.qcadoo.model.internal.FieldDefinitionImpl;
import com.qcadoo.model.internal.types.IntegerType;
import com.qcadoo.model.internal.types.StringType;

public class ExpressionUtilTest {

    private TranslationService translationService;

    private ExpressionService expressionService;

    @Before
    public void init() {
        expressionService = new ExpressionServiceImpl();
        translationService = mock(TranslationService.class);
        setField(expressionService, "translationService", translationService);
    }

    @Test
    public void shouldReturnStringRepresentationOfOneFieldWithoutExpression() throws Exception {
        // given
        Entity entity = new DefaultEntity(null, 1L);
        entity.setField("name", "Mr T");

        FieldDefinition fieldDefinition = new FieldDefinitionImpl(null, "name").withType(new StringType());

        // when
        String value = expressionService.getValue(entity, Lists.newArrayList(fieldDefinition), null);

        // then
        assertEquals("Mr T", value);
    }

    @Test
    public void shouldReturnJoinedStringRepresentationsOfMultipleFieldWithoutExpression() throws Exception {
        // given
        Entity entity = new DefaultEntity(null, 1L);
        entity.setField("name", "Mr T");
        entity.setField("age", 33);
        entity.setField("sex", "F");

        FieldDefinition fieldDefinitionName = new FieldDefinitionImpl(null, "name").withType(new StringType());
        FieldDefinition fieldDefinitionAge = new FieldDefinitionImpl(null, "age").withType(new IntegerType());
        FieldDefinition fieldDefinitionSex = new FieldDefinitionImpl(null, "sex").withType(new StringType());

        // when
        String value = expressionService.getValue(entity,
                Lists.newArrayList(fieldDefinitionName, fieldDefinitionAge, fieldDefinitionSex), Locale.ENGLISH);

        // then
        assertEquals("Mr T, 33, F", value);
    }

    @Test
    public void shouldGenerateValueOfTheSingleFieldColumn() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class, RETURNS_DEEP_STUBS);
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", "Mr T");

        given(dataDefinition.getField(eq("name")).getType().toString(eq("Mr T"), eq(Locale.ENGLISH))).willReturn("Mr X");

        // when
        String value = expressionService.getValue(entity, "#name.toUpperCase()", Locale.ENGLISH);

        // then
        assertEquals("MR X", value);
    }

    @Test
    public void shouldGenerateValueOfEmptyField() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class, RETURNS_DEEP_STUBS);
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", null);

        // when
        String value = expressionService.getValue(entity, "#name", null);

        // then
        assertNull(value);
    }

    @Test
    public void shouldGenerateValueOfTheMultiFieldColumn() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class, RETURNS_DEEP_STUBS);
        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("name", "Mr T");
        entity.setField("age", 33);
        entity.setField("sex", "F");

        given(dataDefinition.getField(eq("name")).getType().toString(eq("Mr T"), eq(Locale.ENGLISH))).willReturn("Mr X");
        given(dataDefinition.getField(eq("sex")).getType().toString(eq("F"), eq(Locale.ENGLISH))).willReturn("F");
        given(dataDefinition.getField(eq("age")).getType().toString(eq(33), eq(Locale.ENGLISH))).willReturn("34");

        // when
        String value = expressionService.getValue(entity,
                "#name + \" -> (\" + (#age) + \") -> \" + (#sex == \"F\" ? \"female\" : \"male\") + \".\"", Locale.ENGLISH);

        // then
        assertEquals("Mr X -> (34) -> female.", value);
    }

    @Test
    public void shouldGenerateValueOfTheBelongsToColumn() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class, RETURNS_DEEP_STUBS);
        Entity product = new DefaultEntity(dataDefinition, 1L);
        product.setField("name", "P1");

        BelongsToType belongsToType = mock(BelongsToType.class);
        given(dataDefinition.getField(eq("name")).getType().toString(eq("P1"), eq(Locale.ENGLISH))).willReturn("P1");
        given(dataDefinition.getField(eq("product")).getType()).willReturn(belongsToType);
        given(dataDefinition.getField(eq("product")).getType()).willReturn(belongsToType);
        given(belongsToType.getDataDefinition()).willReturn(dataDefinition);

        Entity entity = new DefaultEntity(dataDefinition, 1L);
        entity.setField("product", product);

        // when
        String value = expressionService.getValue(entity, "#product['name']", Locale.ENGLISH);

        // then
        assertEquals("P1", value);
    }

}
