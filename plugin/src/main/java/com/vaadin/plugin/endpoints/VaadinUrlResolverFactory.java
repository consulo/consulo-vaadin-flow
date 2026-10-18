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
import consulo.annotation.component.ExtensionImpl;
import consulo.endpoint.url.UrlResolver;
import consulo.endpoint.url.UrlResolverFactory;
import consulo.project.Project;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

@ExtensionImpl
public class VaadinUrlResolverFactory implements UrlResolverFactory {
    private final Project myProject;

    @Inject
    public VaadinUrlResolverFactory(Project project) {
        myProject = project;
    }

    @Override
    public @Nullable UrlResolver forProject() {
        return VaadinProjectUtil.hasVaadinModules(myProject) ? new VaadinUrlResolver(myProject) : null;
    }
}
