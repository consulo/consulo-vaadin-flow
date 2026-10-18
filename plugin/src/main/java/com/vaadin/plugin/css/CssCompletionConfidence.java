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
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionConfidence;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.util.lang.ThreeState;

/**
 * Ensures that auto-completion popup appears automatically when typing inside Vaadin's addClassName() and related
 * method string arguments.
 */
@ExtensionImpl(id = "vaadinCssConfidence", order = "before javaSkipAutopopupInStrings")
public class CssCompletionConfidence extends CompletionConfidence {
    @Override
    @RequiredReadAction
    public ThreeState shouldSkipAutopopup(PsiElement contextElement, PsiFile psiFile, int offset) {
        if (new HasStyleReceiverCondition().accepts(contextElement, null)) {
            // Don't skip - we want the popup to open automatically when typing
            return ThreeState.NO;
        }
        return ThreeState.UNSURE;
    }

    @Override
    public Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }
}
