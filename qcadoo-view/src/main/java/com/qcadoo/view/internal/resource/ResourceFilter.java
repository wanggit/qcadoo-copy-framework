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
package com.qcadoo.view.internal.resource;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class ResourceFilter implements Filter {

    private boolean useJarStaticResources;

    private static final List<String> NOT_STATIC_EXTENSIONS = Arrays.asList(new String[] { "html", "pdf", "xls", "xlsx", "json", "" });

    @Autowired
    private ResourceService resourceService;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();
        if (uri.charAt(0) == '/') {
            uri = uri.substring(1);
        }

        String[] uriParts = uri.split("/");
        if ("files".equals(uriParts[0]) || "rest".equals(uriParts[0])) {
            chain.doFilter(request, response);
            return;
        }

        String[] arr = uri.split("\\.");
        String ext = arr[arr.length - 1];
        if (!NOT_STATIC_EXTENSIONS.contains(ext) && useJarStaticResources) {
            resourceService.serveResource(httpRequest, (HttpServletResponse) response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // empty
    }

    @Override
    public void destroy() {
        // empty
    }

    public void setUseJarStaticResources(final boolean useJarStaticResources) {
        this.useJarStaticResources = useJarStaticResources;
    }

}
