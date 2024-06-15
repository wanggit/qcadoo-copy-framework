<%--

    ***************************************************************************
    Copyright (c) 2010 Qcadoo Limited
    Project: Qcadoo Framework
    Version: 1.4

    This file is part of Qcadoo.

    Qcadoo is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation; either version 3 of the License,
    or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty
    of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    ***************************************************************************

--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

    <head>
        <c:choose>
            <c:when test="${useCompressedStaticResources}">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/qcadoo-min.css?ver=${buildNumber}" type="text/css" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/custom.css?ver=${buildNumber}" type="text/css" />
                <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/_jquery-1.4.2.min.js?ver=${buildNumber}"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery-ui-1.8.5.custom.min.js?ver=${buildNumber}"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery.jqGrid.min.js?ver=${buildNumber}"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/qcadoo-min.js?ver=${buildNumber}"></script>
            </c:when>
            <c:otherwise>
                <link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/core/login.css?ver=${buildNumber}" type="text/css" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/crud/components/form.css?ver=${buildNumber}" type="text/css" />
                <link rel="stylesheet" href="${pageContext.request.contextPath}/qcadooView/public/css/custom.css?ver=${buildNumber}" type="text/css" />

                <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/_jquery-1.4.2.min.js?ver=${buildNumber}"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/utils/serializator.js?ver=${buildNumber}"></script>
                <script type="text/javascript" src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/utils/logger.js?ver=${buildNumber}"></script>
            </c:otherwise>
        </c:choose>

        <link rel="shortcut icon" href="/qcadooView/public/img/core/icons/favicon.png">

        <title>${applicationDisplayName} :: login</title>

        <script type="text/javascript">

            var QCD = QCD || {};
            QCD.global = QCD.global || {};
            QCD.global.isSonowOnPage = false;

            var serverMessageType;
            var serverMessageHeader;
            var serverMessageContent;

            var messagePanel;
            var messagePanelHeader;
            var messagePanelContent;

            var loginErrorMessagePanel;
            var passwordErrorMessagePanel;

            var wrongLoginOrPasswordText = '${translation["security.message.wrongLoginOrPassword"]}';
            var userBlockedText = '${translation["security.message.userBlockedText"]}';
    	    var maxUnsuccessfullAttemptsUserBlockedText = '${translation["security.message.maxUnsuccessfullAttemptsUserBlockedText"]}';

            var errorHeaderText = '${translation["security.message.errorHeader"]}';
            var errorContentText = '${translation["security.message.errorContent"]}';

            var isPopup = ${popup};
            var targetUrl = "${targetUrl}";

            var usernameInput;

            <c:if test="${messageType != null }">
                serverMessageType = '<c:out value="${messageType}"/>';
                serverMessageHeader = '<c:out value="${translation[messageHeader]}"/>';
                serverMessageContent = '<c:out value="${translation[messageContent]}"/>';
            </c:if>

            jQuery(document).ready(function(){

                messagePanel = $("#messagePanel");
                messagePanelHeader = $("#messageHeader");
                messagePanelContent = $("#messageContent");

                usernameInput = $("#usernameInput");

                var passwordInput = $("#passwordInput");
                var loginButton = $("#loginButton");

                if (serverMessageType) {
                    showMessageBox(serverMessageType, serverMessageHeader, serverMessageContent);
                }

                var currentLogin = null;
                if (window.parent && window.parent.getCurrentUserLogin) {
                    currentLogin = window.parent.getCurrentUserLogin();
                } else if (window.opener && window.opener.controller && window.opener.controller.getCurrentUserLogin) {
                    currentLogin = window.opener.controller.getCurrentUserLogin();
                }

                if (currentLogin) {
                    usernameInput.val(currentLogin);
                    usernameInput.attr("disabled", "disabled");
                    $("#usernameInput_component_container_form_w").addClass("disabled");
                    passwordInput.focus();
                    $("#changeUserButton").show();
                } else {
                    $("#changeUserButton").hide();
                    usernameInput.focus();
                    loginButton.css("display", "block");
                    loginButton.css("margin", "auto");
                    usernameInput.keypress(function(e) {
                        var key=e.keyCode || e.which;
                        if (key==13) {
                            ajaxLogin();
                        }
                    });
                }

                $("#languageSelect").val("${currentLanguage}");

                passwordInput.keypress(function(e) {
                    var key=e.keyCode || e.which;
                    if (key==13) {
                        ajaxLogin();
                    }
                });
            });

            changeLanguage = function(language) {
                window.location = "login.html?lang="+language;
            }

            ajaxLogin = function() {
                usernameInput.attr("disabled", ""); // enable field to send it in form (and disable it later if neceserry)
                var formData = QCDSerializator.serializeForm($("#loginForm"));
                if (window.parent.getCurrentUserLogin) {
                    usernameInput.attr("disabled", "disabled");
                }
                var url = "j_spring_security_check";

                $.ajax({
                    url: url,
                    type: 'POST',
                    data: formData,
                    success: function(response) {
                        response = $.trim(response);
                        if (response == "loginSuccessfull") {
                            if (isPopup) {
                                window.location = targetUrl;
                            } else if (window.parent.onLoginSuccess) {
                                window.parent.onLoginSuccess();
                            } else {
                                window.location = "main.html"
                            }
                        } else if (response == "loginUnsuccessfull") {
                            showMessageBox("error", errorHeaderText, wrongLoginOrPasswordText);
                        } else if (response == "userBlocked") {
                            showMessageBox("error", errorHeaderText, userBlockedText);
                        } else if (response == "maxUnsuccessfullAttemptsUserBlocked") {
                            showMessageBox("error", errorHeaderText, maxUnsuccessfullAttemptsUserBlockedText);
                        } else {
                            showMessageBox("error", errorHeaderText, errorContentText);
                        }
                    },
                    error: function(xhr, textStatus, errorThrown){
                        showMessageBox("error", errorHeaderText, errorMessage);
                    }

                });
            }

            showMessageBox = function(type, header, content) {
                messagePanel.removeClass("info");
                messagePanel.removeClass("success");
                messagePanel.removeClass("error");
                messagePanel.addClass(type);
                messagePanelHeader.html(header);
                messagePanelContent.html(content);
                messagePanel.css("display", "block");
            }

            hideMessageBox = function() {
                messagePanel.css("display", "none");
            }
        </script>
    </head>

    <body>
        <div id="contentWrapperMiddle">
            <div id="contentWrapper">

                <div id="messagePanel" style="display: none;">
                    <div id="messageHeader"></div>
                    <div id="messageContent"></div>
                </div>

                <div id="loginContentWrapper">

                    <div id="loginHeader">
                        ${translation["security.form.header"]}
                        <c:if test="${! iframe && ! popup}">
                            <div id="languageDiv">
                                <select id="languageSelect" onchange="changeLanguage(this.value)">
                                    <c:forEach items="${locales}" var="localesEntry">
                                        <option value="${localesEntry.key}">${localesEntry.value}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </c:if>
                    </div>

                    <div id="loginFormWrapper">
                        <form id="loginForm" name="loginForm" action="<c:url value='j_spring_security_check'/>" method="POST">
                            <div>
                                <label>${translation["security.form.label.login"]}</label>
                                <div class="component_form_element" style="height: 20px; width: 200px; vertical-align: middle; display: inline-block;">
                                <div class="component_container_form_w" id="usernameInput_component_container_form_w" style="left: 0; right: 0;">
                                    <div class="component_container_form_inner">
                                        <div class="component_container_form_x"></div>
                                        <div class="component_container_form_y"></div>
                                        <input type="text" id="usernameInput" name='j_username' value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>' autocomplete="off"/>
                                    </div>
                                </div>
                                </div>
                            </div>
                            <div>
                                <label>${translation["security.form.label.password"]}</label>
                                <div class="component_form_element" style="height: 20px; width: 200px; vertical-align: middle; display: inline-block;">
                                <div class="component_container_form_w" style="left: 0; right: 0;">
                                    <div class="component_container_form_inner">
                                        <div class="component_container_form_x"></div>
                                        <div class="component_container_form_y"></div>
                                        <input type="password" id="passwordInput" name='j_password' autocomplete="off"/>
                                    </div>
                                </div>
                                </div>
                            </div>
                            <div id="rememberMeRow">
                                <label></label><input id="rememberMeCheckbox" type="checkbox" name="_spring_security_remember_me" /><label id="rememberMeLabel">${translation["security.form.label.rememberMe"]}</label>
                            </div>
                            <div id="loginButtonWrapper">
                                <a href="#" id="changeUserButton" onclick="window.parent.location='login.html'">${translation["security.form.button.changeUser"]}</a>
                                <a href="#" id="loginButton" onclick="ajaxLogin(); return false;"><span>${translation['security.form.button.logIn']}</span></a>
                            </div>
                        </form>
                    </div>

                    <div id="loginFooter">
                        <div id="loginFooterLine"></div>
                        <div id="loginFooterLogo"></div>
                    </div>
                </div>
            </div>
        </div>
    </body>

</html>