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

import com.intellij.java.indexing.search.searches.AnnotatedElementsSearch;
import com.intellij.java.language.psi.JavaPsiFacade;
import com.intellij.java.language.psi.PsiAnnotation;
import com.intellij.java.language.psi.PsiAnnotationMemberValue;
import com.intellij.java.language.psi.PsiClass;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiAnchorFactory;
import consulo.language.psi.PsiCompiledElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The route views and browser callable services declared in sources.
 * <p>
 * The IntelliJ plugin reads them through UAST, which skips compiled classes; this port reads Java PSI and skips compiled
 * classes as well. The model the plugin builds for Vaadin Copilot (components, entities, security) is not ported.
 */
public final class VaadinModel {
    public static final String VAADIN_ROUTE = "com.vaadin.flow.router.Route";
    public static final String VAADIN_APP_SHELL_CONFIGURATOR = "com.vaadin.flow.component.page.AppShellConfigurator";
    public static final String VAADIN_ID = "com.vaadin.flow.component.template.Id";
    public static final String VAADIN_TAG = "com.vaadin.flow.component.Tag";
    public static final String HILLA_BROWSER_CALLABLE = "com.vaadin.hilla.BrowserCallable";
    public static final String VAADIN_STYLESHEET = "com.vaadin.flow.component.dependency.StyleSheet";
    public static final String VAADIN_COMPONENT = "com.vaadin.flow.component.Component";

    private VaadinModel() {
    }

    @RequiredReadAction
    public static List<VaadinRoute> findFlowRoutes(Project project, GlobalSearchScope scope) {
        PsiClass vaadinRouteClass = JavaPsiFacade.getInstance(project).findClass(VAADIN_ROUTE, GlobalSearchScope.allScope(project));
        if (vaadinRouteClass == null) {
            return List.of();
        }
        PsiAnchorFactory anchorFactory = project.getInstance(PsiAnchorFactory.class);
        List<VaadinRoute> routes = new ArrayList<>();
        AnnotatedElementsSearch.searchPsiClasses(vaadinRouteClass, scope).forEach(psiClass -> {
            String className = psiClass.getName();
            if (psiClass instanceof PsiCompiledElement || className == null) {
                return true;
            }
            PsiAnnotation annotation = psiClass.getAnnotation(VAADIN_ROUTE);
            if (annotation == null) {
                return true;
            }
            String urlMapping = evaluateString(project, annotation.findAttributeValue("value"));
            routes.add(new VaadinRoute(urlMapping == null ? "" : urlMapping, className, anchorFactory.create(psiClass)));
            return true;
        });
        return routes;
    }

    @RequiredReadAction
    public static List<VaadinRoute> findHillaEndpoints(Project project, GlobalSearchScope scope) {
        PsiClass hillaBrowserCallableClass =
            JavaPsiFacade.getInstance(project).findClass(HILLA_BROWSER_CALLABLE, GlobalSearchScope.allScope(project));
        if (hillaBrowserCallableClass == null) {
            return List.of();
        }
        PsiAnchorFactory anchorFactory = project.getInstance(PsiAnchorFactory.class);
        List<VaadinRoute> endpoints = new ArrayList<>();
        AnnotatedElementsSearch.searchPsiClasses(hillaBrowserCallableClass, scope).forEach(psiClass -> {
            String className = psiClass.getName();
            if (psiClass instanceof PsiCompiledElement || className == null) {
                return true;
            }
            endpoints.add(new VaadinRoute(className, className, anchorFactory.create(psiClass)));
            return true;
        });
        return endpoints;
    }

    @RequiredReadAction
    private static @Nullable String evaluateString(Project project, @Nullable PsiAnnotationMemberValue value) {
        if (value == null) {
            return null;
        }
        Object constant = JavaPsiFacade.getInstance(project).getConstantEvaluationHelper().computeConstantExpression(value);
        return constant instanceof String string ? string : null;
    }
}
