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

import consulo.annotation.access.RequiredReadAction;
import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementResolveResult;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.language.psi.PsiPolyVariantReference;
import consulo.language.psi.PsiReferenceBase;
import consulo.language.psi.ResolveResult;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom PSI reference for {@code @StyleSheet} annotations that resolves file paths in standard Spring Boot resource
 * locations.
 * <p>
 * This enables Cmd/Ctrl+click navigation from {@code @StyleSheet} annotation values to their corresponding CSS/SCSS files.
 */
public class VaadinStyleSheetReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    /**
     * Standard Spring Boot resource locations (matching VSCode implementation).
     */
    private static final List<String> RESOURCE_PATHS = List.of(
        "src/main/webapp/",
        "src/main/resources/META-INF/resources/",
        "src/main/resources/static/",
        "src/main/resources/public/",
        "src/main/resources/resources/"
    );

    private final String myPath;

    public VaadinStyleSheetReference(PsiElement element, TextRange textRange, String path) {
        super(element, textRange);
        myPath = path;
    }

    @Override
    @RequiredReadAction
    public @Nullable PsiElement resolve() {
        ResolveResult[] results = multiResolve(false);
        return results.length == 1 ? results[0].getElement() : null;
    }

    @Override
    @RequiredReadAction
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        PsiElement element = getElement();
        Module module = element.getModule();
        if (module == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        // Normalize path - remove leading "./" if present
        String normalizedPath = myPath.startsWith("./") ? myPath.substring(2) : myPath;

        PsiManager psiManager = PsiManager.getInstance(element.getProject());
        List<ResolveResult> results = new ArrayList<>();
        for (VirtualFile contentRoot : ModuleRootManager.getInstance(module).getContentRoots()) {
            for (String resourcePath : RESOURCE_PATHS) {
                VirtualFile file = contentRoot.findFileByRelativePath(resourcePath + normalizedPath);
                if (file != null && file.exists()) {
                    PsiFile psiFile = psiManager.findFile(file);
                    if (psiFile != null) {
                        results.add(new PsiElementResolveResult(psiFile));
                    }
                }
            }
        }
        return results.toArray(ResolveResult.EMPTY_ARRAY);
    }
}
