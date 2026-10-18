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
package com.vaadin.plugin.css;

import com.intellij.java.language.JavaLanguage;
import com.vaadin.plugin.utils.VaadinProjectUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.css.lang.index.CssClassIndex;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.CompletionType;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.pattern.PlatformPatterns;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.util.ProcessingContext;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.xstylesheet.icon.XStyleSheetIconGroup;

import java.util.Collection;

/**
 * Provides exclusive CSS class name completion for Vaadin Flow's HasStyle methods. Only shows CSS classes, no other
 * suggestions.
 */
@ExtensionImpl(order = "before propertiesCompletion")
public class CssClassCompletionContributor extends CompletionContributor {
    public CssClassCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement().with(new HasStyleReceiverCondition()), new CssClassCompletionProvider());
    }

    @Override
    public Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }

    private static final class CssClassCompletionProvider implements CompletionProvider {
        @Override
        @RequiredReadAction
        public void addCompletions(CompletionParameters parameters, ProcessingContext context, CompletionResultSet result) {
            if (!VaadinProjectUtil.isInVaadinModule(parameters.getOriginalFile())) {
                return;
            }

            // Add all CSS class names from the CSS plugin's index
            Project project = parameters.getOriginalFile().getProject();
            GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
            for (String className : CssClassIndex.getAllClassNames(project, scope)) {
                Collection<VirtualFile> files = CssClassIndex.getFilesWithClass(className, scope);
                String sourceFile = files.isEmpty() ? "CSS" : files.iterator().next().getName();
                result.addElement(
                    LookupElementBuilder.create(className)
                        .withIcon(XStyleSheetIconGroup.css_class())
                        .withTypeText(sourceFile)
                        .withCaseSensitivity(false)
                );
            }

            // Stop other contributors from adding suggestions, we only want class names; the contributor runs before the
            // properties one, so that the property keys it completes in string literals do not come in before it stops them
            result.stopHere();
        }
    }
}
