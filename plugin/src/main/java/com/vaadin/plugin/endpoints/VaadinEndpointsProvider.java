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

import com.vaadin.plugin.utils.VaadinProjectUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.component.util.ModificationTracker;
import consulo.endpoint.EndpointFilter;
import consulo.endpoint.EndpointProvider;
import consulo.endpoint.EndpointType;
import consulo.endpoint.ModuleEndpointFilter;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiModificationTracker;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * A Vaadin endpoint group is a single route or service, and so is its only endpoint.
 */
public abstract class VaadinEndpointsProvider implements EndpointProvider<VaadinRoute, VaadinRoute> {
    protected final Project myProject;

    protected VaadinEndpointsProvider(Project project) {
        myProject = project;
    }

    @RequiredReadAction
    protected abstract List<VaadinRoute> findEndpoints(GlobalSearchScope scope);

    @Override
    public EndpointType getEndpointType() {
        return EndpointType.HTTP_SERVER_TYPE;
    }

    @Override
    public Status getStatus() {
        if (VaadinProjectUtil.hasVaadinModules(myProject)) {
            return Status.HAS_ENDPOINTS;
        }
        return Status.UNAVAILABLE;
    }

    @Override
    public ModificationTracker getModificationTracker() {
        return PsiModificationTracker.getInstance(myProject);
    }

    @Override
    @RequiredReadAction
    public Iterable<VaadinRoute> getEndpointGroups(EndpointFilter filter) {
        if (!(filter instanceof ModuleEndpointFilter moduleFilter)) {
            return List.of();
        }
        if (!VaadinProjectUtil.isVaadinModule(moduleFilter.getModule())) {
            return List.of();
        }
        return findEndpoints(moduleFilter.getTransitiveSearchScope());
    }

    @Override
    public Iterable<VaadinRoute> getEndpoints(VaadinRoute group) {
        return List.of(group);
    }

    @Override
    @RequiredReadAction
    public boolean isValidEndpoint(VaadinRoute group, VaadinRoute endpoint) {
        return group.isValid();
    }

    @Override
    @RequiredReadAction
    public @Nullable PsiElement getDocumentationElement(VaadinRoute group, VaadinRoute endpoint) {
        return endpoint.getAnchor().retrieve();
    }
}
