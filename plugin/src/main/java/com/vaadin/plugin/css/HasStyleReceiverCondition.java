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

import com.intellij.java.language.psi.PsiClass;
import com.intellij.java.language.psi.PsiLiteralExpression;
import com.intellij.java.language.psi.PsiMethod;
import com.intellij.java.language.psi.PsiMethodCallExpression;
import com.intellij.java.language.psi.util.InheritanceUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.pattern.PatternCondition;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.util.ProcessingContext;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * Condition to check if the given PsiElement is inside a string literal argument of a HasStyle method call.
 */
public class HasStyleReceiverCondition extends PatternCondition<PsiElement> {
    private static final String HAS_STYLE_FQN = "com.vaadin.flow.component.HasStyle";

    private static final Set<String> TARGET_METHODS =
        Set.of("addClassName", "addClassNames", "setClassName", "removeClassName", "removeClassNames");

    public HasStyleReceiverCondition() {
        super("hasStyleReceiver");
    }

    @Override
    @RequiredReadAction
    public boolean accepts(PsiElement element, @Nullable ProcessingContext context) {
        PsiLiteralExpression literal = PsiTreeUtil.getParentOfType(element, PsiLiteralExpression.class);
        if (literal == null) {
            return false;
        }
        PsiMethodCallExpression methodCall = PsiTreeUtil.getParentOfType(literal, PsiMethodCallExpression.class);
        if (methodCall == null) {
            return false;
        }
        String methodName = methodCall.getMethodExpression().getReferenceName();
        if (methodName == null || !TARGET_METHODS.contains(methodName)) {
            return false;
        }
        PsiMethod method = methodCall.resolveMethod();
        if (method == null) {
            return false;
        }
        PsiClass containingClass = method.getContainingClass();
        // HasStyle itself or any of its subtypes
        return InheritanceUtil.isInheritor(containingClass, HAS_STYLE_FQN);
    }
}
