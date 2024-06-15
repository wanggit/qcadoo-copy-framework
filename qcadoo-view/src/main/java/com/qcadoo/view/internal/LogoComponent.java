package com.qcadoo.view.internal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qcadoo.view.utils.ViewParametersAppender;

@Component
public class LogoComponent {

    private final static String L_DEFAULT_LOGO_BASE_PATH = "/qcadooView/public/css/core/images/login/new/";

    private final static String L_MENU_LOGO_BASE_PATH = "/qcadooView/public/css/core/menu/images-new/";

    private final static String L_LOGO_NAME = "qcadoo-logo.png";

    private final static String L_TEST_LOGO_NAME = "qcadoo-test-logo.png";

    private final static String L_TEST_APPLICATION_PROFILE = "TEST";

    private ViewParametersAppender viewParametersAppender;

    @Autowired
    public LogoComponent(ViewParametersAppender viewParametersAppender) {
        this.viewParametersAppender = viewParametersAppender;
    }

    public String prepareDefaultLogoPath() {
        return prepare(L_DEFAULT_LOGO_BASE_PATH);
    }

    public String prepareMenuLogoPath() {
        return prepare(L_MENU_LOGO_BASE_PATH);
    }

    private String prepare(String path) {
        StringBuilder logoBuilder = new StringBuilder(path);

        if (L_TEST_APPLICATION_PROFILE.equals(viewParametersAppender.getApplicationProfile())) {
            logoBuilder.append(L_TEST_LOGO_NAME);
        } else {
            logoBuilder.append(L_LOGO_NAME);
        }

        return logoBuilder.toString();
    }

}
