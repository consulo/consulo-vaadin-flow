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

import com.intellij.java.language.codeInsight.AnnotationUtil;
import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiField;
import com.intellij.java.language.psi.PsiModifier;
import com.intellij.java.language.psi.util.InheritanceUtil;
import com.vaadin.plugin.utils.VaadinProjectUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.ImplicitUsageProvider;
import consulo.language.psi.PsiElement;

import static com.vaadin.plugin.endpoints.VaadinModel.HILLA_BROWSER_CALLABLE;
import static com.vaadin.plugin.endpoints.VaadinModel.VAADIN_APP_SHELL_CONFIGURATOR;
import static com.vaadin.plugin.endpoints.VaadinModel.VAADIN_ID;
import static com.vaadin.plugin.endpoints.VaadinModel.VAADIN_ROUTE;
import static com.vaadin.plugin.endpoints.VaadinModel.VAADIN_TAG;

/**
 * Vaadin instantiates route views, custom elements, browser callable services and the app shell itself, and writes the
 * fields bound to template elements with {@code @Id}.
 */
@ExtensionImpl
public class VaadinImplicitUsageProvider implements ImplicitUsageProvider {
    @Override
    @RequiredReadAction
    public boolean isImplicitUsage(PsiElement element) {
        return element instanceof PsiClass psiClass
            && !psiClass.isInterface()
            && !psiClass.isEnum()
            && !psiClass.hasModifierProperty(PsiModifier.ABSTRACT)
            && !psiClass.isAnnotationType()
            && VaadinProjectUtil.isInVaadinModule(psiClass)
            && (AnnotationUtil.isAnnotated(psiClass, VAADIN_ROUTE, 0)
            || AnnotationUtil.isAnnotated(psiClass, VAADIN_TAG, 0)
            || AnnotationUtil.isAnnotated(psiClass, HILLA_BROWSER_CALLABLE, 0)
            || InheritanceUtil.isInheritor(psiClass, VAADIN_APP_SHELL_CONFIGURATOR));
    }

    @Override
    public boolean isImplicitRead(PsiElement element) {
        return false;
    }

    @Override
    @RequiredReadAction
    public boolean isImplicitWrite(PsiElement element) {
        return element instanceof PsiField field
            && VaadinProjectUtil.isInVaadinModule(field)
            && AnnotationUtil.isAnnotated(field, VAADIN_ID, 0);
    }
}
