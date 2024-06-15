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

QCD.passwordReset = (function () {
    var messagePanel;
    var messagePanelHeader;
    var messagePanelContent;

    var passwordResetForm;

    var usernameInput;

    var cancelButton;
    var passwordResetButton;

    function init() {
        if (!isSupportedBrowser()) {
            window.location = "browserNotSupported.html";
        }

        $(".dropdown-menu li span").each(onDropdownMenuLiEach);

        messagePanel = $("#messagePanel");
        messagePanelHeader = $("#messageHeader");
        messagePanelContent = $("#messageContent");

        passwordResetForm = $("#passwordResetForm");

        usernameInput = $("#usernameInput");

        cancelButton = $("#cancelButton");
        passwordResetButton = $("#passwordResetButton");

        usernameInput.focus();
        usernameInput.keypress(onUsernameInputKeyPress);

        cancelButton.click(onCancelClick);
        passwordResetButton.click(onPasswordResetClick);
    }

    function getBrowser() {
        var userAgent = navigator.userAgent, version;
        var browser = userAgent.match(/(opera|chrome|safari|firefox|msie|trident(?=\/))\/?\s*(\d+)/i) || [];

        if (/trident/i.test(browser[1])) {
            version =  /\brv[ :]+(\d+)/g.exec(userAgent) || [];

            return { name: "IE ", version: (version[1] || "") };
        }

        if (browser[1] === "Chrome") {
            version = userAgent.match(/\b(OPR|Edge)\/(\d+)/);

            if (version != null) {
                return { name: version[1].replace("OPR", "Opera"), version: version[2] };
            }
        }

        browser = browser[2] ? [browser[1], browser[2]] : [navigator.appName, navigator.appVersion, "-?"];

        if ((version = userAgent.match(/version\/(\d+)/i)) != null) {
            browser.splice(1, 1, version[1]);
        }

        return { name: browser[0], version: browser[1] };
    }

    function isSupportedBrowser() {
        var browser = getBrowser();

        if (
            browser.name.match(/Opera|Chrome|Safari/i) ||
            (browser.name == "IE" && browser.version >= 8) ||
            (browser.name == "Firefox" && browser.version >= 3)
        ) {
            return true;
        } else {
            return false;
        }
    }

    function onDropdownMenuLiEach(i, li) {
        $(li).click(function () {
            changeLanguage(this.lang);
        });
    }

    function changeLanguage(language) {
        window.location = "passwordReset.html?lang=" + language;
    }

    function onUsernameInputKeyPress(e) {
        var key = e.keyCode || e.which;

        if (key == 13) {
            onPasswordResetClick();

            return false;
        }
    }

    function onCancelClick() {
        window.parent.location = "login.html";
    }

    function onPasswordResetClick() {
        hideMessagePanel();

        usernameInput.removeClass("is-invalid");

        var formData = QCDSerializator.serializeForm(passwordResetForm);
        var url = "passwordReset.html";

        lockForm(true);

        $.ajax({
            url: url,
            type: "POST",
            data: formData,
            success: function(response) {
                response = $.trim(response);

                switch(response) {
                    case "success":
                        window.location = "login.html?passwordReseted=true";

                    break;

                    case "loginIsBlank":
                        usernameInput.addClass("is-invalid");

                        lockForm(false);
                    break;

                    case "userNotFound":
                        window.location = "login.html?passwordReseted=true";

                    break;

                    case "invalidMailAddress":
                        window.location = "login.html?passwordReseted=true";

                    break;

                    case "invalidMailConfig":
                        window.location = "login.html?passwordReseted=true";

                    break;

                    default:
                        showMessagePanel("alert-danger", errorHeaderText, errorContentText);

                        lockForm(false);
                    break;
                }
            },
            error: function(xhr, textStatus, errorThrown){
                showMessagePanel("alert-danger", errorHeaderText, errorMessage);

                lockForm(false);
            }
        });
    }

    function showMessagePanel(type, header, content) {
        messagePanel.removeClass("alert-info");
        messagePanel.removeClass("alert-success");
        messagePanel.removeClass("alert-danger");

        messagePanel.addClass(type);

        messagePanelHeader.html(header);
        messagePanelContent.html(content);

        messagePanel.show();
    }

    function hideMessagePanel() {
        messagePanel.hide();
    }

    function lockForm(disabled) {
        usernameInput.prop("disabled", disabled);

        passwordResetButton.prop("disabled", disabled);
    }

    return {
        init: init
    };
})();
