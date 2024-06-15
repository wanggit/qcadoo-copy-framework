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
package com.qcadoo.view.internal.states.components;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.*;
import com.qcadoo.model.api.validators.ErrorMessage;
import com.qcadoo.model.internal.DefaultEntity;
import com.qcadoo.model.internal.ExpressionServiceImpl;
import com.qcadoo.model.internal.types.StringType;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.ViewDefinitionState;
import com.qcadoo.view.api.utils.SecurityEscapeService;
import com.qcadoo.view.internal.api.InternalComponentState;
import com.qcadoo.view.internal.components.FieldComponentPattern;
import com.qcadoo.view.internal.components.FieldComponentState;
import com.qcadoo.view.internal.components.form.FormComponentPattern;
import com.qcadoo.view.internal.components.form.FormComponentState;
import com.qcadoo.view.internal.states.AbstractComponentState;
import com.qcadoo.view.internal.states.AbstractStateTest;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class FormComponentStateTest extends AbstractStateTest {

    private FieldComponentState name;

    private FormComponentState form;

    @Mock
    private Entity entity;

    @Mock
    private ViewDefinitionState viewDefinitionState;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private DataDefinition dataDefinition;

    @Mock
    private FieldDefinition fieldDefinition;

    @Mock
    private TranslationService translationService;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SecurityRolesService securityRolesService;

    @Mock
    private SecurityEscapeService securityEscapeService;

    @Mock
    private FieldComponentPattern namePattern;

    @Mock
    private FormComponentPattern pattern;

    @Mock
    private EntityMessagesHolder messagesHolder;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);

        given(entity.getField("name")).willReturn("text");

        given(fieldDefinition.getType()).willReturn(new StringType());
        given(fieldDefinition.getName()).willReturn("name");

        given(dataDefinition.get(12L)).willReturn(null);
        given(dataDefinition.get(13L)).willReturn(entity);
        given(dataDefinition.getPluginIdentifier()).willReturn("plugin");
        given(dataDefinition.getName()).willReturn("name");
        given(dataDefinition.getField("name")).willReturn(fieldDefinition);
        given(dataDefinition.delete(any(Long.class))).willReturn(EntityOpResult.successfull());

        given(dataDefinition.create(anyLong())).willAnswer(new Answer<Entity>() {

            @Override
            public Entity answer(final InvocationOnMock invocation) throws Throwable {
                Long id = (Long) invocation.getArguments()[0];

                return new DefaultEntity(dataDefinition, id);
            }
        });

        setField(namePattern, "applicationContext", applicationContext);

        given(namePattern.isRequired()).willReturn(false);
        given(namePattern.isPersistent()).willReturn(true);

        setField(pattern, "applicationContext", applicationContext);

        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn("'static expression'");

        given(applicationContext.getBean(SecurityRolesService.class)).willReturn(securityRolesService);
        given(applicationContext.getBean(SecurityEscapeService.class)).willReturn(securityEscapeService);

        name = new FieldComponentState(namePattern);
        name.setTranslationService(translationService);
        name.setName("name");
        name.initialize(new JSONObject(), Locale.ENGLISH);

        form = new FormComponentState(pattern);
        form.setDataDefinition(dataDefinition);
        form.setTranslationService(translationService);
        form.addFieldEntityIdChangeListener("name", name);
        form.initialize(new JSONObject(ImmutableMap.of("components", new JSONObject())), Locale.ENGLISH);

        new ExpressionServiceImpl().init();
    }

    @Test
    public void shouldInitializeFormWithEntityId() throws Exception {
        // given
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);

        InternalComponentState componentState = new FormComponentState(pattern);

        JSONObject json = new JSONObject();
        JSONObject jsonContent = new JSONObject();
        jsonContent.put(FormComponentState.JSON_ENTITY_ID, 13L);
        json.put(AbstractComponentState.JSON_CONTENT, jsonContent);
        JSONObject jsonChildren = new JSONObject();
        json.put(AbstractComponentState.JSON_CHILDREN, jsonChildren);

        // when
        componentState.initialize(json, Locale.ENGLISH);

        // then
        assertEquals(13L, componentState.getFieldValue());
    }

    @Test
    public void shouldInitializeFormWithNullEntityId() throws Exception {
        // given
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn(null);

        InternalComponentState componentState = new FormComponentState(pattern);

        JSONObject json = new JSONObject();
        JSONObject jsonContent = new JSONObject();
        jsonContent.put(FormComponentState.JSON_ENTITY_ID, (String) null);
        json.put(AbstractComponentState.JSON_CONTENT, jsonContent);
        JSONObject jsonChildren = new JSONObject();
        json.put(AbstractComponentState.JSON_CHILDREN, jsonChildren);

        // when
        componentState.initialize(json, Locale.ENGLISH);

        // then
        assertNull(componentState.getFieldValue());
    }

    @Test
    public void shouldRenderFormEntityId() throws Exception {
        // given
        given(pattern.getExpressionNew()).willReturn(null);
        given(pattern.getExpressionEdit()).willReturn("2");

        AbstractComponentState componentState = new FormComponentState(pattern);

        componentState.setTranslationService(translationService);
        componentState.setDataDefinition(dataDefinition);
        componentState.setFieldValue(13L);
        componentState.initialize(new JSONObject(ImmutableMap.of("components", new JSONObject())), Locale.ENGLISH);

        // when
        JSONObject json = componentState.render();

        // then
        assertEquals(13L, json.getJSONObject(AbstractComponentState.JSON_CONTENT).getLong(FormComponentState.JSON_ENTITY_ID));
    }

    @Test
    public void shouldHaveMessageIfEntityIsNotExistsAndEntityIdIsNotNull() throws Exception {
        // given
        form.setFieldValue(12L);
        given(translationService.translate(eq("null.entityNotFound"), eq("qcadooView.message.entityNotFound"), any(Locale.class)))
                .willReturn("translated entityNotFound");

        // when
        form.performEvent(viewDefinitionState, "initialize", new String[0]);

        // then
        assertFalse(form.isValid());
        assertTrue(form.render().toString().contains("translated entityNotFound"));
    }

    @Test
    public void shouldClearFormIfEntityIsNotExistsAndEntityIdIsNull() throws Exception {
        // given
        form.setFieldValue(null);

        // when
        form.performEvent(viewDefinitionState, "initialize", new String[0]);

        // then
        assertNull(form.getFieldValue());
    }

    @Test
    public void shouldResetForm() throws Exception {
        // given
        form.setFieldValue(13L);

        // when
        form.performEvent(viewDefinitionState, "reset", new String[0]);

        // then
        assertEquals("text", name.getFieldValue());
    }

    @Test
    public void shouldClearFormEntity() throws Exception {
        // given
        form.setFieldValue(13L);

        // when
        form.performEvent(viewDefinitionState, "clear", new String[0]);

        // then
        assertNull(name.getFieldValue());
        assertNull(form.getFieldValue());
    }

    @Test
    public void shouldDeleteFormEntity() throws Exception {
        // given
        name.setFieldValue("text");

        form.setFieldValue(13L);

        // when
        form.performEvent(viewDefinitionState, "delete", new String[0]);

        // then
        assertNull(name.getFieldValue());
        verify(dataDefinition).delete(13L);
        assertNull(form.getFieldValue());
    }

    @Test
    public void shouldNotDeleteFormEntity() throws Exception {
        // given
        final String message = "some.message";
        ErrorMessage errorMessage = new ErrorMessage(message);
        given(messagesHolder.getGlobalErrors()).willReturn(Lists.newArrayList(errorMessage));

        Map<String, ErrorMessage> fieldErrors = Maps.newHashMap();
        fieldErrors.put("name", errorMessage);
        given(messagesHolder.getErrors()).willReturn(fieldErrors);

        EntityOpResult result = EntityOpResult.failure(messagesHolder);
        given(dataDefinition.delete(13L)).willReturn(result);

        name.setFieldValue("text");

        form.setFieldValue(13L);

        // when
        form.performEvent(viewDefinitionState, "delete", new String[0]);

        // then
        assertNotNull(name.getFieldValue());
        verify(dataDefinition).delete(13L);
        assertEquals(13L, form.getFieldValue());
    }

    @Test
    public void shouldSaveFormEntity() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, null, Collections.singletonMap("name", (Object) "text"));
        Entity savedEntity = new DefaultEntity(dataDefinition, 13L, Collections.singletonMap("name", (Object) "text2"));

        given(dataDefinition.create(null)).willReturn(new DefaultEntity(dataDefinition));
        given(dataDefinition.save(eq(entity))).willReturn(savedEntity);

        name.setFieldValue("text");

        form.setFieldValue(null);

        // when
        form.performEvent(viewDefinitionState, "save", new String[0]);

        // then
        verify(dataDefinition).save(eq(entity));
        assertEquals("text2", name.getFieldValue());
        assertEquals(13L, form.getFieldValue());
        assertTrue(form.isValid());
    }

    @Test
    public void shouldCopyFormEntity() throws Exception {
        // given
        Entity copiedEntity = new DefaultEntity(dataDefinition, 14L, Collections.singletonMap("name", (Object) "text(1)"));

        given(dataDefinition.copy(13L)).willReturn(Collections.singletonList(copiedEntity));
        given(dataDefinition.get(14L)).willReturn(copiedEntity);

        name.setFieldValue("text");
        form.setFieldValue(13L);

        // when
        form.performEvent(viewDefinitionState, "copy", new String[0]);

        // then
        verify(dataDefinition).copy(13L);
        verify(dataDefinition).get(14L);
        assertEquals("text(1)", name.getFieldValue());
        assertEquals(14L, form.getFieldValue());
    }

    @Test
    public void shouldUseContextWhileSaving() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, 13L, Collections.singletonMap("name", (Object) "text2"));
        Entity savedEntity = new DefaultEntity(dataDefinition, 13L, Collections.singletonMap("name", (Object) "text2"));

        given(dataDefinition.create(13L)).willReturn(new DefaultEntity(dataDefinition, 13L));
        given(dataDefinition.save(eq(entity))).willReturn(savedEntity);
        given(dataDefinition.getFields().keySet()).willReturn(Collections.singleton("name"));

        name.setFieldValue("text");

        JSONObject json = new JSONObject();
        JSONObject jsonContext = new JSONObject();
        jsonContext.put("id", 14L);
        jsonContext.put("name", "text2");
        JSONObject jsonContent = new JSONObject();
        jsonContent.put(FormComponentState.JSON_ENTITY_ID, 13L);
        json.put(AbstractComponentState.JSON_CONTEXT, jsonContext);
        json.put(AbstractComponentState.JSON_CONTENT, jsonContent);
        json.put(AbstractComponentState.JSON_CHILDREN, new JSONObject());

        form.initialize(json, Locale.ENGLISH);

        // when
        form.performEvent(viewDefinitionState, "save", new String[0]);

        // then
        verify(dataDefinition).save(eq(entity));
        assertEquals("text2", name.getFieldValue());
        assertEquals(13L, form.getFieldValue());
        assertTrue(form.isValid());
    }

    @Test
    public void shouldHaveValidationErrors() throws Exception {
        // given
        Entity entity = new DefaultEntity(dataDefinition, null, Collections.singletonMap("name", (Object) "text"));
        Entity savedEntity = new DefaultEntity(dataDefinition, null, Collections.singletonMap("name", (Object) "text2"));

        savedEntity.addGlobalError("global.error");
        savedEntity.addError(fieldDefinition, "field.error");

        given(translationService.translate(eq("global.error"), any(Locale.class))).willReturn("translated global error");
        given(translationService.translate(eq("field.error"), any(Locale.class))).willReturn("translated field error");
        given(dataDefinition.create(null)).willReturn(new DefaultEntity(dataDefinition));
        given(dataDefinition.save(eq(entity))).willReturn(savedEntity);

        name.setFieldValue("text");

        form.setFieldValue(null);

        // when
        form.performEvent(viewDefinitionState, "save", new String[0]);

        // then
        verify(dataDefinition).save(eq(entity));
        assertFalse(form.isValid());
        assertTrue(form.render().toString().contains("translated global error"));
    }

    @Test
    public void shouldReturnPersistedEntityWithIncludedFormValues() throws Exception {
        // given
        Long id = 14L;
        String nameFieldName = "name";
        String nameFieldFormValue = "new name";
        String numberFieldName = "number";
        String numberFieldValue = "0003";

        Entity formEntity = new DefaultEntity(dataDefinition, id, Maps.<String, Object> newHashMap());
        formEntity.setField(nameFieldName, nameFieldFormValue);

        Entity alreadyPersistedEntity = new DefaultEntity(dataDefinition, id, Maps.<String, Object> newHashMap());
        alreadyPersistedEntity.setField(nameFieldName, "old name value");
        alreadyPersistedEntity.setField(numberFieldName, numberFieldValue);

        given(dataDefinition.get(id)).willReturn(alreadyPersistedEntity);

        form.setEntity(formEntity);

        // when
        Entity resultEntity = form.getPersistedEntityWithIncludedFormValues();

        // then
        verify(dataDefinition).get(id);
        assertEquals(id, resultEntity.getId());
        assertEquals(nameFieldFormValue, resultEntity.getStringField(nameFieldName));
        assertEquals(numberFieldValue, resultEntity.getStringField(numberFieldName));
        assertEquals(2, resultEntity.getFields().size());
    }

    @Test
    public void shouldReturnEntityWithFormValuesIfEntityWasDeletedByAnotherUser() throws Exception {
        // given
        Long id = 14L;
        String nameFieldName = "name";
        String nameFieldFormValue = "new name";
        String numberFieldName = "number";

        Entity formEntity = new DefaultEntity(dataDefinition, id, Maps.<String, Object> newHashMap());
        formEntity.setField(nameFieldName, nameFieldFormValue);

        given(dataDefinition.get(id)).willReturn(null);

        form.setEntity(formEntity);

        // when
        Entity resultEntity = form.getPersistedEntityWithIncludedFormValues();

        // then
        verify(dataDefinition).get(id);
        assertEquals(id, resultEntity.getId());
        assertEquals(nameFieldFormValue, resultEntity.getStringField(nameFieldName));
        assertNull(resultEntity.getStringField(numberFieldName));
        assertEquals(1, resultEntity.getFields().size());
    }

    @Test
    public void shouldReturnEntityWithFormValuesIfEntityDoesNotHaveId() throws Exception {
        // given
        String nameFieldName = "name";
        String nameFieldFormValue = "new name";
        String numberFieldName = "number";

        Entity formEntity = new DefaultEntity(dataDefinition, null, Maps.<String, Object> newHashMap());
        formEntity.setField(nameFieldName, nameFieldFormValue);

        given(dataDefinition.get(anyLong())).willReturn(null);

        form.setEntity(formEntity);

        // when
        Entity resultEntity = form.getPersistedEntityWithIncludedFormValues();

        // then
        verify(dataDefinition, never()).get(anyLong());
        verify(dataDefinition, never()).get(null);
        assertNull(resultEntity.getId());
        assertEquals(nameFieldFormValue, resultEntity.getStringField(nameFieldName));
        assertNull(resultEntity.getStringField(numberFieldName));
        assertEquals(1, resultEntity.getFields().size());
    }

    @Test
    public void shouldReturnEntityWithOnlyFormValues() throws Exception {
        // given
        String nameFieldName = "name";
        String nameFieldFormValue = "new name";
        String numberFieldName = "number";

        Entity formEntity = new DefaultEntity(dataDefinition, null, Maps.<String, Object> newHashMap());
        formEntity.setField(nameFieldName, nameFieldFormValue);

        form.setEntity(formEntity);

        // when
        Entity resultEntity = form.getEntity();

        // then
        verify(dataDefinition, never()).get(anyLong());
        verify(dataDefinition, never()).get(null);
        assertNull(resultEntity.getId());
        assertEquals(nameFieldFormValue, resultEntity.getStringField(nameFieldName));
        assertNull(resultEntity.getStringField(numberFieldName));
        assertEquals(1, resultEntity.getFields().size());
    }

    @Test
    public void shouldReturnEntityWithOnlyFormValuesEvenIfEntityIsAlreadyPersisted() throws Exception {
        // given
        Long id = 14L;
        String nameFieldName = "name";
        String nameFieldFormValue = "new name";
        String numberFieldName = "number";
        String numberFieldValue = "0003";

        Entity formEntity = new DefaultEntity(dataDefinition, id, Maps.<String, Object> newHashMap());
        formEntity.setField(nameFieldName, nameFieldFormValue);

        Entity alreadyPersistedEntity = new DefaultEntity(dataDefinition, id, Maps.<String, Object> newHashMap());
        alreadyPersistedEntity.setField(nameFieldName, "old name value");
        alreadyPersistedEntity.setField(numberFieldName, numberFieldValue);

        given(dataDefinition.get(id)).willReturn(alreadyPersistedEntity);

        form.setEntity(formEntity);

        // when
        Entity resultEntity = form.getEntity();

        // then
        verify(dataDefinition, never()).get(id);
        assertEquals(id, resultEntity.getId());
        assertEquals(nameFieldFormValue, resultEntity.getStringField(nameFieldName));
        assertNull(resultEntity.getStringField(numberFieldName));
        assertEquals(1, resultEntity.getFields().size());
    }

}
