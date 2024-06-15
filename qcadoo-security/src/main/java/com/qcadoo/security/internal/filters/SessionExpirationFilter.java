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
package com.qcadoo.security.internal.filters;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.security.core.AuthenticationException;

public final class SessionExpirationFilter implements Filter {

    private final Pattern logoutPattern = Pattern.compile("login\\.html\\?logout=true$");

    private final Pattern basicLoginPattern = Pattern.compile("login\\.html");

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            return;
        }
        if (!(response instanceof HttpServletResponse)) {
            return;
        }

        HttpServletResponse httpResponse = (HttpServletResponse) response;
        RedirectResponseWrapper redirectResponseWrapper = new RedirectResponseWrapper(httpResponse);

        try {
            chain.doFilter(request, redirectResponseWrapper);
        } catch (AuthenticationException e) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            redirectToLoginPage(httpRequest, httpResponse);
        }

        if (redirectResponseWrapper.getRedirect() != null) {
            Matcher logoutMatcher = logoutPattern.matcher(redirectResponseWrapper.getRedirect());
            Matcher basicLoginMatcher = basicLoginPattern.matcher(redirectResponseWrapper.getRedirect());

            if (basicLoginMatcher.find() && !logoutMatcher.find()) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;
                redirectToLoginPage(httpRequest, httpResponse);
            } else {
                httpResponse.sendRedirect(redirectResponseWrapper.getRedirect());
            }
        }
    }

    private void redirectToLoginPage(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.getOutputStream().println("sessionExpired");
        } else if ("true".equals(request.getParameter("popup"))) {
            String targetUrl = request.getContextPath() + "/login.html?popup=true&targetUrl="
                    + URLEncoder.encode(request.getRequestURL().toString() + "?" + request.getQueryString(), "UTF-8");
            response.sendRedirect(response.encodeRedirectURL(targetUrl));
        } else if ("true".equals(request.getParameter("iframe"))) {
            String targetUrl = request.getContextPath() + "/login.html?iframe=true";
            response.sendRedirect(response.encodeRedirectURL(targetUrl));
        } else {
            String targetUrl = request.getContextPath() + "/login.html?timeout=true";
            response.sendRedirect(response.encodeRedirectURL(targetUrl));
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    private static final class RedirectResponseWrapper extends HttpServletResponseWrapper {

        private String redirect = null;

        public RedirectResponseWrapper(final HttpServletResponse httpServletResponse) {
            super(httpServletResponse);
        }

        @Override
        public void sendRedirect(final String string) throws IOException {
            redirect = string;
        }

        public String getRedirect() {
            return redirect;
        }
    }

}
