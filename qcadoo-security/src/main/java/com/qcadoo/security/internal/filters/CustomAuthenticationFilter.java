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

import com.qcadoo.security.internal.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public final class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final Authentication authResult) throws IOException, ServletException {

        RedirectResponseWrapper redirectResponseWrapper = new RedirectResponseWrapper(response);

        super.successfulAuthentication(request, redirectResponseWrapper, authResult);

        response.getOutputStream().println("loginSuccessfull");

    }

    @Override
    protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                              final AuthenticationException failed) throws IOException, ServletException {

        RedirectResponseWrapper redirectResponseWrapper = new RedirectResponseWrapper(response);

        super.unsuccessfulAuthentication(request, redirectResponseWrapper, failed);

        if (failed instanceof LockedException
                || loginAttemptService.isBlocked(failed.getAuthentication().getPrincipal().toString(),
                loginAttemptService.getClientIP(request))) {
            response.getOutputStream().println("maxUnsuccessfullAttemptsUserBlocked");
        } else {
            response.getOutputStream().println("loginUnsuccessfull");
        }
    }

    private static final class RedirectResponseWrapper extends HttpServletResponseWrapper {

        public RedirectResponseWrapper(final HttpServletResponse httpServletResponse) {
            super(httpServletResponse);
        }

        @Override
        public void sendRedirect(final String string) throws IOException {
            // this method should be empty to prevent setting redirect by parent
        }

    }

}
