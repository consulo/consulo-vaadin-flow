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
package com.vaadin.plugin.symbols;

import com.intellij.java.indexing.search.searches.AnnotatedElementsSearch;
import com.intellij.java.language.psi.JavaPsiFacade;
import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiMethod;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.vaadin.plugin.utils.VaadinProjectUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.codeEditor.Editor;
import consulo.language.editor.navigation.GotoDeclarationHandler;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import consulo.typescript.TypeScriptFileType;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.vaadin.plugin.endpoints.VaadinModel.HILLA_BROWSER_CALLABLE;

/**
 * Navigates from the TypeScript client of a Hilla service to its Java side: {@code UserEndpoint} to the
 * {@code @BrowserCallable} class, {@code UserEndpoint.getUser} to its method.
 * <p>
 * The IntelliJ plugin attaches a symbol reference to every {@code JSReferenceExpression} of a TypeScript file
 * ({@code HillaSymbolReferenceProvider}, {@code HillaSymbolReference}, {@code HillaSymbol}). Consulo has no symbol API, and a
 * JavaScript reference expression is a reference itself, which contributed references do not extend, so the same lookup
 * answers Go to Declaration instead.
 */
@ExtensionImpl
public class HillaGotoDeclarationHandler implements GotoDeclarationHandler {
    @Override
    @RequiredReadAction
    public PsiElement @Nullable [] getGotoDeclarationTargets(PsiElement sourceElement, int offset, Editor editor) {
        PsiFile file = sourceElement.getContainingFile();
        if (file == null || !file.getLanguage().isKindOf(TypeScriptFileType.INSTANCE.getLanguage())) {
            return null;
        }
        if (!(sourceElement.getParent() instanceof JSReferenceExpression referenceExpression)) {
            return null;
        }
        if (!VaadinProjectUtil.isInVaadinModule(file)) {
            return null;
        }
        List<PsiElement> targets = resolveReference(referenceExpression);
        return targets.isEmpty() ? null : targets.toArray(PsiElement.EMPTY_ARRAY);
    }

    @RequiredReadAction
    private static List<PsiElement> resolveReference(PsiElement element) {
        Project project = element.getProject();
        PsiClass hillaBrowserCallableClass =
            JavaPsiFacade.getInstance(project).findClass(HILLA_BROWSER_CALLABLE, GlobalSearchScope.allScope(project));
        if (hillaBrowserCallableClass == null) {
            return List.of();
        }

        String text = element.getText();
        String className = text;
        String methodName = null;
        // both ClassName and ClassName.methodName are JSReferenceExpressions
        if (text.indexOf('.') >= 0) {
            String[] parts = text.split("\\.");
            className = parts[0];
            methodName = parts.length > 1 ? parts[1] : null;
        }

        List<PsiClass> psiClasses = new ArrayList<>();
        for (PsiClass psiClass : AnnotatedElementsSearch.searchPsiClasses(hillaBrowserCallableClass, GlobalSearchScope.allScope(project)).findAll()) {
            String name = psiClass.getName();
            if (name != null && name.endsWith(className)) {
                psiClasses.add(psiClass);
            }
        }

        List<PsiElement> result = new ArrayList<>();
        if (methodName != null) {
            for (PsiClass psiClass : psiClasses) {
                PsiMethod[] methods = psiClass.findMethodsByName(methodName, true);
                if (methods.length > 0) {
                    result.add(methods[0]);
                }
            }
            return result;
        }
        result.addAll(psiClasses);
        return result;
    }
}
