/*
 * Copyright 2024-2026 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vaadin.plugin.endpoints;

import com.vaadin.plugin.icon.VaadinIconGroup;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.endpoint.FrameworkPresentation;
import consulo.endpoint.presentation.HttpUrlPresentation;
import consulo.endpoint.url.PathSegmentRenderer;
import consulo.endpoint.url.UrlPath;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.navigation.ItemPresentation;
import consulo.project.Project;
import jakarta.inject.Inject;

import java.util.List;

/**
 * The {@code @Route} views of Vaadin Flow.
 */
@ExtensionImpl
public class VaadinFlowEndpointsProvider extends VaadinEndpointsProvider {
    private static final FrameworkPresentation PRESENTATION =
        new FrameworkPresentation("Vaadin-Flow", "Vaadin Flow", VaadinIconGroup.vaadin());

    /**
     * Shows a route parameter the way URL templates usually do, {@code user/{id}}.
     */
    private static final PathSegmentRenderer VAADIN_URL_RENDERER = new PathSegmentRenderer() {
        @Override
        public String visitVariable(UrlPath.PathSegment.Variable variable) {
            return "{" + variable.getVariableName() + "}";
        }
    };

    @Inject
    public VaadinFlowEndpointsProvider(Project project) {
        super(project);
    }

    @Override
    public FrameworkPresentation getPresentation() {
        return PRESENTATION;
    }

    @Override
    @RequiredReadAction
    protected List<VaadinRoute> findEndpoints(GlobalSearchScope scope) {
        return VaadinModel.findFlowRoutes(myProject, scope);
    }

    @Override
    public ItemPresentation getEndpointPresentation(VaadinRoute group, VaadinRoute endpoint) {
        return new HttpUrlPresentation(normalizeUrl(group.getUrlMapping()), group.getLocationString(), VaadinIconGroup.vaadin());
    }

    private static String normalizeUrl(String urlMapping) {
        String urlString;
        if (urlMapping.isBlank()) {
            urlString = "/";
        }
        else if (!urlMapping.startsWith("/")) {
            urlString = "/" + urlMapping;
        }
        else {
            urlString = urlMapping;
        }
        return VaadinUrlParser.parseVaadinUrlMapping(urlString).getPresentation(VAADIN_URL_RENDERER);
    }
}
