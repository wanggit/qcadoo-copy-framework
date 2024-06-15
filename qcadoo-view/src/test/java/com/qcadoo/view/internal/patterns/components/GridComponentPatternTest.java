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
package com.qcadoo.view.internal.patterns.components;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.google.common.collect.ImmutableMap;
import com.qcadoo.localization.api.TranslationService;
import com.qcadoo.model.api.DataDefinition;
import com.qcadoo.model.api.FieldDefinition;
import com.qcadoo.model.api.types.BelongsToType;
import com.qcadoo.model.api.types.HasManyType;
import com.qcadoo.model.internal.types.EnumType;
import com.qcadoo.model.internal.types.IntegerType;
import com.qcadoo.model.internal.types.StringType;
import com.qcadoo.plugin.api.PluginStateResolver;
import com.qcadoo.plugin.internal.PluginUtilsService;
import com.qcadoo.security.api.SecurityRolesService;
import com.qcadoo.view.api.ComponentState;
import com.qcadoo.view.api.components.GridComponent;
import com.qcadoo.view.constants.QcadooViewConstants;
import com.qcadoo.view.internal.ComponentDefinition;
import com.qcadoo.view.internal.ComponentOption;
import com.qcadoo.view.internal.api.InternalViewDefinition;
import com.qcadoo.view.internal.api.InternalViewDefinitionState;
import com.qcadoo.view.internal.components.TextInputComponentPattern;
import com.qcadoo.view.internal.components.grid.GridComponentColumn;
import com.qcadoo.view.internal.components.grid.GridComponentPattern;
import com.qcadoo.view.internal.module.gridColumn.ViewGridColumnModuleColumnModel;
import com.qcadoo.view.internal.patterns.AbstractComponentPattern;
import com.qcadoo.view.internal.patterns.AbstractPatternTest;
import com.qcadoo.view.internal.xml.ViewDefinitionParser;
import com.qcadoo.view.internal.xml.ViewDefinitionParserImpl;

public class GridComponentPatternTest extends AbstractPatternTest {

    private ApplicationContext applicationContext;

    private TranslationService translationService;

    @Before
    public void init() throws Exception {
        applicationContext = mock(ApplicationContext.class);
        translationService = mock(TranslationService.class);
    }

    @Test
    public void shouldInitializeOptions() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class);
        InternalViewDefinition viewDefinition = mock(InternalViewDefinition.class);

        given(translationService.translate(Mockito.anyString(), Mockito.any(Locale.class))).willReturn("i18n");
        FieldDefinition nameFieldDefinition = mock(FieldDefinition.class);
        given(nameFieldDefinition.getType()).willReturn(new EnumType(translationService, "", true, "v1", "v2"));
        given(nameFieldDefinition.getName()).willReturn("name");

        FieldDefinition numberFieldDefinition = mock(FieldDefinition.class);
        given(numberFieldDefinition.getType()).willReturn(new IntegerType());
        given(numberFieldDefinition.getName()).willReturn("number");

        FieldDefinition productFieldDefinition = mock(FieldDefinition.class);
        given(productFieldDefinition.getType()).willReturn(new StringType());
        given(productFieldDefinition.getName()).willReturn("product");

        given(dataDefinition.getField("name")).willReturn(nameFieldDefinition);
        given(dataDefinition.getField("number")).willReturn(numberFieldDefinition);
        given(dataDefinition.getField("product")).willReturn(productFieldDefinition);
        given(dataDefinition.isPrioritizable()).willReturn(true);

        given(viewDefinition.getDataDefinition()).willReturn(dataDefinition);
        ComponentDefinition componentDefinition = getComponentDefinition(QcadooViewConstants.L_GRID, viewDefinition);
        componentDefinition.setTranslationService(translationService);
        componentDefinition.setApplicationContext(applicationContext);
        GridComponentPattern pattern = new GridComponentPattern(componentDefinition);

        pattern.addOption(new ComponentOption("correspondingView", ImmutableMap.of("value", "plugin/details")));
        pattern.addOption(new ComponentOption("correspondingComponent", ImmutableMap.of("value", "window.form")));
        pattern.addOption(new ComponentOption("paginable", ImmutableMap.of("value", "true")));
        pattern.addOption(new ComponentOption("creatable", ImmutableMap.of("value", "true")));
        pattern.addOption(new ComponentOption("deletable", ImmutableMap.of("value", "true")));
        pattern.addOption(new ComponentOption("width", ImmutableMap.of("value", "200")));
        pattern.addOption(new ComponentOption("height", ImmutableMap.of("value", "400")));
        pattern.addOption(new ComponentOption("lookup", ImmutableMap.of("value", "true")));
        pattern.addOption(new ComponentOption("searchable", ImmutableMap.of("value", "name,number,product")));
        pattern.addOption(new ComponentOption("orderable", ImmutableMap.of("value", "name,number")));
        pattern.addOption(new ComponentOption("order", ImmutableMap.of("column", "name", "direction", "asc")));
        pattern.addOption(new ComponentOption("column", ImmutableMap.of("name", "number", "fields", "number", "link", "true",
                "width", "200")));
        pattern.addOption(new ComponentOption("column", ImmutableMap.of("name", "name", "fields", "name", "hidden", "true")));
        pattern.addOption(new ComponentOption("column", ImmutableMap.of("name", "product", "expression", "#product['name']")));

        // when
        pattern.initialize();

        // then
        JSONObject options = getJsOptions(pattern);

        assertEquals("plugin/details", options.getString("correspondingView"));
        assertEquals("window.form", options.getString("correspondingComponent"));
        assertFalse(options.getBoolean("fullscreen"));
        assertTrue(options.getBoolean("paginable"));
        assertTrue(options.getBoolean("lookup"));
        assertTrue(options.getBoolean("creatable"));
        assertTrue(options.getBoolean("deletable"));
        assertTrue(options.getBoolean("prioritizable"));
        assertEquals(200, options.getInt("width"));
        assertEquals(400, options.getInt("height"));
        assertEquals(2, options.getJSONArray("orderableColumns").length());
        assertEquals("number", options.getJSONArray("orderableColumns").getString(0));
        assertEquals("name", options.getJSONArray("orderableColumns").getString(1));
        assertEquals(3, options.getJSONArray("searchableColumns").length());
        assertEquals("number", options.getJSONArray("searchableColumns").getString(0));
        assertEquals("product", options.getJSONArray("searchableColumns").getString(1));
        assertEquals("name", options.getJSONArray("searchableColumns").getString(2));
        assertFalse(options.has("belongsToFieldName"));
        assertEquals(3, options.getJSONArray("columns").length());

        JSONObject number = options.getJSONArray("columns").getJSONObject(0);
        JSONObject name = options.getJSONArray("columns").getJSONObject(1);
        JSONObject product = options.getJSONArray("columns").getJSONObject(2);

        assertEquals("number", number.getString("name"));
        assertTrue(number.getBoolean("link"));
        assertFalse(number.getBoolean("hidden"));
        assertEquals(200, number.getInt("width"));
        assertEquals("right", number.getString("align"));
        assertFalse(number.has("filterValues"));

        assertEquals("name", name.getString("name"));
        assertFalse(name.getBoolean("link"));
        assertTrue(name.getBoolean("hidden"));
        assertEquals(100, name.getInt("width"));
        assertEquals("left", name.getString("align"));
        assertEquals(2, name.getJSONArray("filterValues").length());

        assertEquals("v1", name.getJSONArray("filterValues").getJSONObject(0).getString("key"));
        assertEquals("i18n", name.getJSONArray("filterValues").getJSONObject(0).getString("value"));
        assertEquals("v2", name.getJSONArray("filterValues").getJSONObject(1).getString("key"));
        assertEquals("i18n", name.getJSONArray("filterValues").getJSONObject(1).getString("value"));

        assertEquals("product", product.getString("name"));
        assertFalse(product.getBoolean("link"));
        assertFalse(product.getBoolean("hidden"));
        assertEquals(100, product.getInt("width"));
        assertEquals("left", product.getString("align"));
        assertFalse(product.has("filterValues"));
    }

    @Test
    public void shouldHaveDefaultValues() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class);
        InternalViewDefinition viewDefinition = mock(InternalViewDefinition.class);

        given(viewDefinition.getDataDefinition()).willReturn(dataDefinition);
        ComponentDefinition componentDefinition = getComponentDefinition(QcadooViewConstants.L_GRID, viewDefinition);
        componentDefinition.setTranslationService(translationService);
        componentDefinition.setApplicationContext(applicationContext);
        GridComponentPattern pattern = new GridComponentPattern(componentDefinition);
        pattern.addOption(new ComponentOption("order", ImmutableMap.of("column", "name", "direction", "asc")));

        // when
        pattern.initialize();

        // then
        JSONObject options = getJsOptions(pattern);

        assertFalse(options.has("correspondingView"));
        assertFalse(options.has("correspondingComponent"));
        assertFalse(options.has("belongsToFieldName"));
        assertTrue(options.getBoolean("paginable"));
        assertFalse(options.getBoolean("lookup"));
        assertFalse(options.getBoolean("creatable"));
        assertFalse(options.getBoolean("deletable"));
        assertFalse(options.getBoolean("prioritizable"));
        assertFalse(options.getBoolean("fullscreen"));
        assertEquals(300, options.getInt("width"));
        assertEquals(300, options.getInt("height"));
        assertEquals(0, options.getJSONArray("orderableColumns").length());
        assertEquals(0, options.getJSONArray("searchableColumns").length());
        assertEquals(0, options.getJSONArray("columns").length());
    }

    @Test
    public void shouldBeFullscreen() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class);
        InternalViewDefinition viewDefinition = mock(InternalViewDefinition.class);

        given(viewDefinition.getDataDefinition()).willReturn(dataDefinition);
        ComponentDefinition componentDefinition = getComponentDefinition(QcadooViewConstants.L_GRID, viewDefinition);
        componentDefinition.setTranslationService(translationService);
        componentDefinition.setApplicationContext(applicationContext);
        GridComponentPattern pattern = new GridComponentPattern(componentDefinition);

        pattern.addOption(new ComponentOption("fullscreen", ImmutableMap.of("value", "true")));
        pattern.addOption(new ComponentOption("order", ImmutableMap.of("column", "name", "direction", "asc")));

        // when
        pattern.initialize();

        // then
        JSONObject options = getJsOptions(pattern);

        assertTrue(options.getBoolean("fullscreen"));
        assertEquals(0, options.getInt("width"));
        assertEquals(0, options.getInt("height"));
    }

    @Test
    public void shouldHaveScopeFieldName() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class);

        BelongsToType belongsToFieldType = mock(BelongsToType.class);

        FieldDefinition belongsToFieldDefinition = mock(FieldDefinition.class);
        given(belongsToFieldDefinition.getName()).willReturn("joinName");
        given(belongsToFieldDefinition.getType()).willReturn(belongsToFieldType);
        given(belongsToFieldDefinition.getDataDefinition()).willReturn(dataDefinition);

        HasManyType hasManyFieldType = mock(HasManyType.class);
        given(hasManyFieldType.getJoinFieldName()).willReturn("joinName");
        given(hasManyFieldType.getDataDefinition()).willReturn(dataDefinition);

        FieldDefinition hasManyFieldDefinition = mock(FieldDefinition.class);
        given(hasManyFieldDefinition.getName()).willReturn("fieldName");
        given(hasManyFieldDefinition.getType()).willReturn(hasManyFieldType);
        given(hasManyFieldDefinition.getDataDefinition()).willReturn(dataDefinition);

        given(dataDefinition.getField("field")).willReturn(hasManyFieldDefinition);
        given(dataDefinition.getField("joinName")).willReturn(belongsToFieldDefinition);

        InternalViewDefinition viewDefinition = mock(InternalViewDefinition.class);

        AbstractComponentPattern sourceComponent = new TextInputComponentPattern(getComponentDefinition("component",
                viewDefinition));
        setField(sourceComponent, "dataDefinition", dataDefinition);
        setField(sourceComponent, "initialized", true);

        given(viewDefinition.getComponentByReference("component")).willReturn(sourceComponent);

        ComponentDefinition componentDefinition = getComponentDefinition(QcadooViewConstants.L_GRID, null, "#{component}.field", null, viewDefinition);
        componentDefinition.setTranslationService(translationService);
        componentDefinition.setApplicationContext(applicationContext);
        GridComponentPattern pattern = new GridComponentPattern(componentDefinition);

        pattern.addOption(new ComponentOption("order", ImmutableMap.of("column", "name", "direction", "asc")));

        // when
        pattern.initialize();

        // then
        JSONObject options = getJsOptions(pattern);

        assertEquals("joinName", options.getString("belongsToFieldName"));
    }

    @Test
    public void shouldReturnState() throws Exception {
        // given
        InternalViewDefinitionState viewDefinitionState = mock(InternalViewDefinitionState.class);
        DataDefinition dataDefinition = mock(DataDefinition.class);

        BelongsToType belongsToFieldType = mock(BelongsToType.class);

        FieldDefinition belongsToFieldDefinition = mock(FieldDefinition.class);
        given(belongsToFieldDefinition.getName()).willReturn("joinName");
        given(belongsToFieldDefinition.getType()).willReturn(belongsToFieldType);

        HasManyType hasManyFieldType = mock(HasManyType.class);
        given(hasManyFieldType.getJoinFieldName()).willReturn("joinName");
        given(hasManyFieldType.getDataDefinition()).willReturn(dataDefinition);

        FieldDefinition hasManyFieldDefinition = mock(FieldDefinition.class);
        given(hasManyFieldDefinition.getName()).willReturn("fieldName");
        given(hasManyFieldDefinition.getType()).willReturn(hasManyFieldType);

        given(dataDefinition.getField("field")).willReturn(hasManyFieldDefinition);
        given(dataDefinition.getField("joinName")).willReturn(belongsToFieldDefinition);

        InternalViewDefinition viewDefinition = mock(InternalViewDefinition.class);

        AbstractComponentPattern sourceComponent = new TextInputComponentPattern(getComponentDefinition("component",
                viewDefinition));
        setField(sourceComponent, "dataDefinition", dataDefinition);
        setField(sourceComponent, "initialized", true);

        given(viewDefinition.getComponentByReference("component")).willReturn(sourceComponent);

        ComponentDefinition componentDefinition = getComponentDefinition(QcadooViewConstants.L_GRID, null, "#{component}.field", null, viewDefinition);
        componentDefinition.setTranslationService(translationService);
        componentDefinition.setApplicationContext(applicationContext);
        GridComponentPattern pattern = new GridComponentPattern(componentDefinition);

        pattern.addOption(new ComponentOption("order", ImmutableMap.of("column", "name", "direction", "asc")));

        pattern.initialize();

        // when
        ComponentState state = pattern.createComponentState(viewDefinitionState);

        // then
        assertTrue(state instanceof GridComponent);

        assertEquals(belongsToFieldDefinition, getField(state, "belongsToFieldDefinition"));
        assertEquals(getField(pattern, "columns"), getField(state, "columns"));

    }

    @Test
    public void shouldParsePredefinedFilters() throws Exception {
        // given
        DataDefinition dataDefinition = mock(DataDefinition.class);
        InternalViewDefinition viewDefinition = mock(InternalViewDefinition.class);
        SecurityRolesService securityRolesService = mock(SecurityRolesService.class);

        given(viewDefinition.getDataDefinition()).willReturn(dataDefinition);
        ComponentDefinition componentDefinition = getComponentDefinition(QcadooViewConstants.L_GRID, viewDefinition);
        componentDefinition.setTranslationService(translationService);
        componentDefinition.setApplicationContext(applicationContext);
        GridComponentPattern pattern = new GridComponentPattern(componentDefinition);

        pattern.addOption(new ComponentOption("order", ImmutableMap.of("column", "name", "direction", "asc")));

        ViewDefinitionParser parser = new ViewDefinitionParserImpl();
        setField(parser, "securityRolesService", securityRolesService);

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.newDocument();
        Node componentNode = doc.createElement("root");

        Node predefinedFiltersNode = doc.createElement("predefinedFilters");
        componentNode.appendChild(predefinedFiltersNode);

        Element predefinedFilter1 = doc.createElement("predefinedFilter");
        predefinedFilter1.setAttribute("name", "filter1");
        predefinedFiltersNode.appendChild(predefinedFilter1);

        Element predefinedFilter2 = doc.createElement("predefinedFilter");
        predefinedFilter2.setAttribute("name", "filter2");
        predefinedFiltersNode.appendChild(predefinedFilter2);
        Element orderNode = doc.createElement("filterOrder");
        orderNode.setAttribute("column", "testCol");
        orderNode.setAttribute("direction", "desc");
        predefinedFilter2.appendChild(orderNode);
        Element filterNode1 = doc.createElement("filterRestriction");
        filterNode1.setAttribute("column", "testCol1");
        filterNode1.setAttribute("value", "testVal1");
        predefinedFilter2.appendChild(filterNode1);
        Element filterNode2 = doc.createElement("filterRestriction");
        filterNode2.setAttribute("column", "testCol2");
        filterNode2.setAttribute("value", "testVal2");
        predefinedFilter2.appendChild(filterNode2);

        pattern.initialize();

        // when
        pattern.parse(componentNode, parser);

        // then
        JSONObject options = getJsOptions(pattern);
        JSONArray filtersArray = options.getJSONArray("predefinedFilters");
        assertNotNull(filtersArray);

        assertEquals(2, filtersArray.length());

        assertEquals("filter1", filtersArray.getJSONObject(0).get("label"));
        assertEquals("name", filtersArray.getJSONObject(0).get("orderColumn"));
        assertEquals("asc", filtersArray.getJSONObject(0).get("orderDirection"));
        assertEquals(0, filtersArray.getJSONObject(0).getJSONObject("filter").length());

        assertEquals("filter2", filtersArray.getJSONObject(1).get("label"));
        assertEquals("testCol", filtersArray.getJSONObject(1).get("orderColumn"));
        assertEquals("desc", filtersArray.getJSONObject(1).get("orderDirection"));
        assertEquals(2, filtersArray.getJSONObject(1).getJSONObject("filter").length());
        assertEquals("testVal1", filtersArray.getJSONObject(1).getJSONObject("filter").getString("testCol1"));
        assertEquals("testVal2", filtersArray.getJSONObject(1).getJSONObject("filter").getString("testCol2"));
    }

    @Test
    public void shouldFilterOutColumnsInvisibleForTenant() throws Exception {
        // given
        PluginStateResolver pluginStateResolver = mock(PluginStateResolver.class);
        PluginUtilsService pluginUtil = new PluginUtilsService(pluginStateResolver);
        pluginUtil.init();

        given(pluginStateResolver.isEnabled("disabledPlugin")).willReturn(false);

        InternalViewDefinitionState viewDefinitionState = mock(InternalViewDefinitionState.class);
        DataDefinition dataDefinition = mock(DataDefinition.class);
        InternalViewDefinition viewDefinition = mock(InternalViewDefinition.class);

        given(viewDefinition.getDataDefinition()).willReturn(dataDefinition);
        ComponentDefinition componentDefinition = getComponentDefinition(QcadooViewConstants.L_GRID, viewDefinition);
        componentDefinition.setTranslationService(translationService);
        componentDefinition.setApplicationContext(applicationContext);
        componentDefinition.setDataDefinition(dataDefinition);
        GridComponentPattern pattern = new GridComponentPattern(componentDefinition);

        FieldDefinition nameFieldDefinition = mock(FieldDefinition.class);
        given(nameFieldDefinition.getType()).willReturn(new EnumType(translationService, "", true, "v1", "v2"));

        given(dataDefinition.getField("name")).willReturn(nameFieldDefinition);

        pattern.addOption(new ComponentOption("column", ImmutableMap.of("name", "name", "fields", "name", "hidden", "true")));
        pattern.addOption(new ComponentOption("order", ImmutableMap.of("column", "name", "direction", "asc")));

        ViewGridColumnModuleColumnModel columnModel = new ViewGridColumnModuleColumnModel("invisible", "name");
        columnModel.setWidth(100);
        pattern.addColumn("disabledPlugin", columnModel);

        pattern.initialize();

        // when
        ComponentState state = pattern.createComponentState(viewDefinitionState);

        // then
        assertTrue(state instanceof GridComponent);
        @SuppressWarnings("unchecked")
        Map<String, GridComponentColumn> patternColumns = (Map<String, GridComponentColumn>) getField(pattern, "columns");
        @SuppressWarnings("unchecked")
        Map<String, GridComponentColumn> stateColumns = (Map<String, GridComponentColumn>) getField(state, "columns");
        assertEquals(2, patternColumns.size());
        assertEquals(1, stateColumns.size());

        assertTrue(patternColumns.keySet().contains("name"));
        assertNotNull(patternColumns.get("name"));
        assertTrue(patternColumns.keySet().contains("invisible"));
        assertNotNull(patternColumns.get("invisible"));

        assertTrue(stateColumns.keySet().contains("name"));
        assertNotNull(stateColumns.get("name"));
        assertFalse(stateColumns.keySet().contains("invisible"));
        assertNull(stateColumns.get("invisible"));

    }

    @Test
    public void shouldNotFilterOutColumnsVisibleForTenant() throws Exception {
        // given
        PluginStateResolver pluginStateResolver = mock(PluginStateResolver.class);
        PluginUtilsService pluginUtil = new PluginUtilsService(pluginStateResolver);
        pluginUtil.init();

        given(pluginStateResolver.isEnabled("disabledPlugin")).willReturn(true);

        InternalViewDefinitionState viewDefinitionState = mock(InternalViewDefinitionState.class);
        DataDefinition dataDefinition = mock(DataDefinition.class);
        InternalViewDefinition viewDefinition = mock(InternalViewDefinition.class);

        given(viewDefinition.getDataDefinition()).willReturn(dataDefinition);
        ComponentDefinition componentDefinition = getComponentDefinition(QcadooViewConstants.L_GRID, viewDefinition);
        componentDefinition.setTranslationService(translationService);
        componentDefinition.setApplicationContext(applicationContext);
        componentDefinition.setDataDefinition(dataDefinition);
        GridComponentPattern pattern = new GridComponentPattern(componentDefinition);

        FieldDefinition nameFieldDefinition = mock(FieldDefinition.class);
        given(nameFieldDefinition.getType()).willReturn(new EnumType(translationService, "", true, "v1", "v2"));

        given(dataDefinition.getField("name")).willReturn(nameFieldDefinition);

        pattern.addOption(new ComponentOption("column", ImmutableMap.of("name", "name", "fields", "name", "hidden", "true")));
        pattern.addOption(new ComponentOption("order", ImmutableMap.of("column", "name", "direction", "asc")));

        ViewGridColumnModuleColumnModel columnModel = new ViewGridColumnModuleColumnModel("invisible", "name");
        columnModel.setWidth(100);
        pattern.addColumn("disabledPlugin", columnModel);

        pattern.initialize();

        // when
        ComponentState state = pattern.createComponentState(viewDefinitionState);

        // then
        assertTrue(state instanceof GridComponent);
        @SuppressWarnings("unchecked")
        Map<String, GridComponentColumn> patternColumns = (Map<String, GridComponentColumn>) getField(pattern, "columns");
        @SuppressWarnings("unchecked")
        Map<String, GridComponentColumn> stateColumns = (Map<String, GridComponentColumn>) getField(state, "columns");
        assertEquals(2, patternColumns.size());
        assertEquals(2, stateColumns.size());

        assertTrue(patternColumns.keySet().contains("name"));
        assertNotNull(patternColumns.get("name"));
        assertTrue(patternColumns.keySet().contains("invisible"));
        assertNotNull(patternColumns.get("invisible"));

        assertTrue(stateColumns.keySet().contains("name"));
        assertNotNull(stateColumns.get("name"));
        assertTrue(stateColumns.keySet().contains("invisible"));
        assertNotNull(stateColumns.get("invisible"));

    }

}
