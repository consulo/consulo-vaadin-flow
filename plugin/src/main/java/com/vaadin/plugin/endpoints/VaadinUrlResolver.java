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
import consulo.application.util.CachedValueProvider;
import consulo.application.util.CachedValuesManager;
import consulo.endpoint.url.HttpMethodConstants;
import consulo.endpoint.url.UrlConstants;
import consulo.endpoint.url.UrlPath;
import consulo.endpoint.url.UrlResolveRequest;
import consulo.endpoint.url.UrlResolver;
import consulo.endpoint.url.UrlTargetInfo;
import consulo.language.psi.PsiModificationTracker;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves HTTP URLs to the route views declared in the Vaadin modules of the project.
 * <p>
 * The IntelliJ plugin caches the routes per module through the JVM microservices caches; here they are cached per module
 * until the next PSI change.
 */
public class VaadinUrlResolver implements UrlResolver {
    private final Project myProject;

    public VaadinUrlResolver(Project project) {
        myProject = project;
    }

    @Override
    public List<String> getSupportedSchemes() {
        return UrlConstants.HTTP_SCHEMES;
    }

    @Override
    @RequiredReadAction
    public Iterable<UrlTargetInfo> getVariants() {
        return getAllModuleVariants();
    }

    @Override
    @RequiredReadAction
    public Iterable<UrlTargetInfo> resolve(UrlResolveRequest request) {
        if (!HttpMethodConstants.GET.equals(request.getMethod())) {
            return List.of();
        }
        List<UrlTargetInfo> allModuleVariants = getAllModuleVariants();
        List<UrlTargetInfo> result = new ArrayList<>();
        for (UrlPath path : UrlPath.combinations(request.getPath())) {
            for (UrlTargetInfo variant : allModuleVariants) {
                if (variant.getPath().isCompatibleWith(path)) {
                    result.add(variant);
                }
            }
        }
        return result;
    }

    @RequiredReadAction
    private List<UrlTargetInfo> getAllModuleVariants() {
        List<UrlTargetInfo> result = new ArrayList<>();
        for (Module module : ModuleManager.getInstance(myProject).getModules()) {
            for (VaadinRoute route : getVariants(module)) {
                result.add(new VaadinUrlTargetInfo(route));
            }
        }
        return result;
    }

    @RequiredReadAction
    private List<VaadinRoute> getVariants(Module module) {
        if (!VaadinProjectUtil.isVaadinModule(module)) {
            return List.of();
        }
        return CachedValuesManager.getManager(myProject).getCachedValue(module, () -> CachedValueProvider.Result.create(
            VaadinModel.findFlowRoutes(myProject, GlobalSearchScope.moduleScope(module)),
            PsiModificationTracker.MODIFICATION_COUNT
        ));
    }
}
