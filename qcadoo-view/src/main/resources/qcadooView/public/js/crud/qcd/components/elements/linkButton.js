/*
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
var QCD = QCD || {};
QCD.components = QCD.components || {};
QCD.components.elements = QCD.components.elements || {};

QCD.components.elements.LinkButton = function(_element, _mainController) {
	$.extend(this, new QCD.components.Component(_element, _mainController));
	
	if (!(this instanceof QCD.components.elements.LinkButton)) {
	    return new QCD.components.elements.LinkButton(_element, _mainController);
	}

	var mainController = _mainController;
	
	var element = _element;
	
	var elementPath = this.elementPath;
	var elementName = this.elementName;
	
	var pageUrl = "";
	var openInModal = true;
	var modalDimensions = {
	    width: 1000,
	    height: 560
	};
	
	var button = $("#"+this.elementSearchName+"_buttonDiv");
	var buttonLink = $("#"+this.elementSearchName+"_buttonLink");
	
	if (this.options.referenceName) {
		mainController.registerReferenceName(this.options.referenceName, this);
	}
	
	this.getComponentValue = function() {
		return { 
		    value : pageUrl,
		    openInModal : openInModal,
		    modalWidth : modalDimensions.width,
		    modalHeight : modalDimensions.height
		};
	};
	
	this.setComponentValue = function(value) {
		insertValue(value);
	};
	
	this.setComponentState = function(state) {
		insertValue(state);
	};
	
	function insertValue(value) {
		pageUrl = value.value;
		openInModal = value.openInModal;
		modalDimensions = {
			width : value.modalWidth,
			height : value.modalHeight
		};
	}
	
	this.setComponentEnabled = function(isEnabled) {
		if (isEnabled) {
			button.addClass('activeButton');
		} else {
			button.removeClass('activeButton');
		}
	};
	
	this.setComponentLoading = function(isLoadingVisible) {

	};
	
	function onButtonClick(e) {
		buttonLink.blur();
		if (button.hasClass('activeButton')) {
			if (openInModal) {
				mainController.openModal(elementPath, pageUrl, null, null, null, modalDimensions);
			} else {
				mainController.goToPage(pageUrl);
			}
		}
	}
	
	function constructor(_this) {		
		buttonLink.click(onButtonClick);
	}
	
	constructor(this);
}