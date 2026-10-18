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
package com.vaadin.plugin.psi;

import com.intellij.java.indexing.search.searches.MethodReferencesSearch;
import com.intellij.java.indexing.search.searches.MethodReferencesSearchExecutor;
import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiMethod;
import com.intellij.lang.javascript.JSElementTypes;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.ReadAction;
import consulo.document.util.TextRange;
import consulo.language.file.FileTypeManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceBase;
import consulo.language.psi.PsiUtilCore;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.search.PsiSearchHelper;
import consulo.language.psi.search.UsageSearchContext;
import consulo.project.Project;
import consulo.project.util.query.QueryExecutorBase;
import consulo.virtualFileSystem.fileType.FileType;

import java.util.function.Predicate;

import static com.vaadin.plugin.endpoints.VaadinModel.HILLA_BROWSER_CALLABLE;

/**
 * Finds the calls of a {@code @BrowserCallable} service method in the TypeScript and JavaScript client code Hilla generates
 * for it, by the name of the method.
 */
@ExtensionImpl
public class HillaReferenceSearcher extends QueryExecutorBase<PsiReference, MethodReferencesSearch.SearchParameters>
    implements MethodReferencesSearchExecutor {

    @Override
    public void processQuery(MethodReferencesSearch.SearchParameters queryParameters, Predicate<? super PsiReference> consumer) {
        PsiMethod psiMethod = queryParameters.getMethod();
        ReadAction.run(() -> {
            PsiClass containingClass = psiMethod.getContainingClass();
            if (containingClass == null || !containingClass.hasAnnotation(HILLA_BROWSER_CALLABLE)) {
                return;
            }
            Project project = queryParameters.getProject();
            PsiSearchHelper searchHelper = PsiSearchHelper.getInstance(project);
            GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
            FileTypeManager fileTypeManager = FileTypeManager.getInstance();
            FileType[] clientFileTypes = {
                fileTypeManager.getFileTypeByExtension("ts"),
                fileTypeManager.getFileTypeByExtension("tsx"),
                fileTypeManager.getFileTypeByExtension("js"),
                fileTypeManager.getFileTypeByExtension("jsx")
            };
            GlobalSearchScope filteredScope = GlobalSearchScope.getScopeRestrictedByFileTypes(scope, clientFileTypes);
            String methodName = psiMethod.getName();
            searchHelper.processElementsWithWord(
                (psiElement, offset) -> {
                    if (PsiUtilCore.getElementType(psiElement) == JSElementTypes.REFERENCE_EXPRESSION) {
                        TextRange range = new TextRange(offset, offset + methodName.length());
                        consumer.test(new PsiReferenceBase<>(psiElement, range) {
                            @Override
                            public PsiElement resolve() {
                                return psiElement;
                            }
                        });
                    }
                    return true; // return false to stop the search early
                },
                filteredScope,
                methodName,
                UsageSearchContext.IN_CODE,
                true
            );
        });
    }
}
