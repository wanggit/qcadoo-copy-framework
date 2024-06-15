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
<![CDATA[ERROR PAGE:LoginPage]]>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>

<html>

    <head>
        <sec:csrfMetaTags/>

        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

        <link rel="shortcut icon" href="/qcadooView/public/img/core/icons/favicon.png">

        <title>${applicationDisplayName} :: login</title>

        <link rel="stylesheet"
            href="${pageContext.request.contextPath}/qcadooView/public/css/core/lib/bootstrap.min.css?ver=${buildNumber}"
            type="text/css"/>
        <link rel="stylesheet"
            href="${pageContext.request.contextPath}/qcadooView/public/css/core/lib/languages.min.css?ver=${buildNumber}"
            type="text/css"/>
        <link rel="stylesheet"
            href="${pageContext.request.contextPath}/qcadooView/public/css/core/login-min.css?ver=${buildNumber}"
            type="text/css"/>

        <script type="text/javascript"
            src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/jquery-3.2.1.min.js?ver=${buildNumber}"></script>
        <script type="text/javascript"
            src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/popper.min.js?ver=${buildNumber}"></script>
        <script type="text/javascript"
            src="${pageContext.request.contextPath}/qcadooView/public/js/core/lib/bootstrap.min.js?ver=${buildNumber}"></script>
        <script type="text/javascript"
            src="${pageContext.request.contextPath}/qcadooView/public/js/core/qcd/utils/serializator.js?ver=${buildNumber}"></script>
        <script type="text/javascript"
            src="${pageContext.request.contextPath}/qcadooView/public/js/core/login-min.js?ver=${buildNumber}"></script>
    </head>

    <body class="text-center" role="document">
        <div class="container" role="main">
            <div id="messagePanel" class="alert" role="alert">
                <h6 class="alert-heading" id="messageHeader"></h5>
                <p id="messageContent"></p>
            </div>

            <div class="loginContainer">
                <c:if test="${! iframe && ! popup}">
                    <div class="text-right">
                        <div class="btn-group dropup">
                            <button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                                <span class="lang-sm" lang="${currentLanguage}"></span> <span class="caret"></span>
                            </button>
                            <ul class="dropdown-menu" role="menu">
                                <c:forEach items="${locales}" var="localesEntry">
                                    <li><span class="lang-sm lang-lbl" lang="${localesEntry.key}"></span></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                    <div class="mt-3"></div>
                </c:if>

                <form id="loginForm" name="loginForm" action="<c:url value='j_spring_security_check'/>" method="POST">
                <img class="logo mb-4" src="${logoPath}" alt="Logo"/>
                <h1 class="h3 mb-4 font-weight-normal">${translation["security.form.header"]}</h1>

                <div class="input-group">
                    <label for="usernameInput" class="sr-only">${translation["security.form.label.login"]}</label>
                    <input type="text" id="usernameInput" name="j_username" class="form-control" placeHolder="${translation["security.form.label.login"]}" value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>' autocomplete="off" required autofocus/>
                </div>

                <div class="input-group">
                    <label for="passwordInput" class="sr-only">${translation["security.form.label.password"]}</label>
                    <input type="password" id="passwordInput" name="j_password" class="form-control" placeHolder="${translation["security.form.label.password"]}" autocomplete="off" required/>
                    <div class="invalid-feedback" style="margin-top: -25px;">
                        ${translation["security.message.wrongLoginOrPassword"]}
                    </div>
                </div>

                <c:if test="${rememberMeAvailable}">
                    <div class="checkbox mb-3">
                        <label>
                            <input id="rememberMeCheckbox" type="checkbox" name="_spring_security_remember_me"> ${translation["security.form.label.rememberMe"]}
                        </label>
                    </div>
                </c:if>

                <button type="button" class="btn btn-lg btn-primary btn-block" id="loginButton"><span>${translation['security.form.button.logIn']}</button>

                <p class="mt-3 mb-3">
                    <a href="#" id="forgotPasswordLink">${translation['security.form.link.forgotPassword']}</a>
                </p>
                </form>
            </div>
        </div>

        <script type="text/javascript" charset="utf-8">
            var errorHeaderText = '${translation["security.message.errorHeader"]}';
            var errorContentText = '${translation["security.message.errorContent"]}';

            var wrongLoginOrPasswordText = '${translation["security.message.wrongLoginOrPassword"]}';
            var userBlockedText = '${translation["security.message.userBlockedText"]}';
    	    var maxUnsuccessfullAttemptsUserBlockedText = '${translation["security.message.maxUnsuccessfullAttemptsUserBlockedText"]}';

            var isPopup = "${popup}";
            var targetUrl = "${targetUrl}";

            var serverMessageType;
            var serverMessageHeader;
            var serverMessageContent;

            <c:if test="${messageType != null}">
            serverMessageType = '<c:out value="${messageType}"/>';
            serverMessageHeader = '<c:out value="${translation[messageHeader]}"/>';
            serverMessageContent = '<c:out value="${translation[messageContent]}"/>';
            </c:if>

            jQuery(document).ready(function() {
                QCD.login.init();
            });

            $(function () {
                var token = $("meta[name='_csrf']").attr("content");
                var header = $("meta[name='_csrf_header']").attr("content");
                $(document).ajaxSend(function(e, xhr, options) {
                    xhr.setRequestHeader(header, token);
                });
            });
        </script>
    </body>

</html>
