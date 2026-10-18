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

import com.intellij.java.language.JavaLanguage;
import com.intellij.java.language.patterns.PsiJavaPatterns;
import com.intellij.java.language.psi.PsiLiteralExpression;
import com.intellij.lang.properties.references.PropertyReference;
import com.vaadin.plugin.endpoints.VaadinUrlParser;
import com.vaadin.plugin.utils.VaadinProjectUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.endpoint.url.UrlConstants;
import consulo.endpoint.url.reference.UrlPathReferenceInjector;
import consulo.language.Language;
import consulo.language.editor.highlight.HighlightedReference;
import consulo.language.psi.ElementManipulators;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceContributor;
import consulo.language.psi.PsiReferenceProvider;
import consulo.language.psi.PsiReferenceRegistrar;
import consulo.language.psi.util.PartiallyKnownString;
import consulo.language.util.ProcessingContext;
import org.jspecify.annotations.Nullable;

import static com.vaadin.plugin.endpoints.VaadinModel.VAADIN_COMPONENT;
import static com.vaadin.plugin.endpoints.VaadinModel.VAADIN_ROUTE;
import static com.vaadin.plugin.endpoints.VaadinModel.VAADIN_STYLESHEET;

/**
 * References from Java string literals: the segments of the {@code @Route} template resolve as URL paths, the key passed to
 * {@code Component.getTranslation} to a property, and the path in {@code @StyleSheet} to a stylesheet in the resource folders.
 * <p>
 * The IntelliJ plugin registers these on UAST and so covers Kotlin as well; this port reads Java PSI only.
 */
@ExtensionImpl
public class VaadinReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(
            PsiJavaPatterns.literalExpression().insideAnnotationParam(VAADIN_ROUTE),
            new RouteUrlReferenceProvider()
        );

        registrar.registerReferenceProvider(
            PsiJavaPatterns.literalExpression()
                .methodCallParameter(0, PsiJavaPatterns.psiMethod().withName("getTranslation").definedInClass(VAADIN_COMPONENT)),
            new TranslationKeyReferenceProvider()
        );

        registrar.registerReferenceProvider(
            PsiJavaPatterns.literalExpression().insideAnnotationParam(VAADIN_STYLESHEET),
            new StyleSheetReferenceProvider()
        );
    }

    @Override
    public Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }

    private static final class RouteUrlReferenceProvider extends PsiReferenceProvider {
        private final UrlPathReferenceInjector<PsiLiteralExpression> myInjector =
            UrlPathReferenceInjector.<PsiLiteralExpression>forPartialStringFrom(
                VaadinUrlParser.VAADIN_URL_PKS_PARSER,
                RouteUrlReferenceProvider::toPartiallyKnownString
            ).withSchemesSupport(UrlConstants.HTTP_SCHEMES);

        @Override
        @RequiredReadAction
        public PsiReference[] getReferencesByElement(PsiElement element, ProcessingContext context) {
            if (!(element instanceof PsiLiteralExpression literal)
                || !(literal.getValue() instanceof String)
                || !VaadinProjectUtil.isInVaadinModule(literal)) {
                return PsiReference.EMPTY_ARRAY;
            }
            return myInjector.buildAbsoluteOrRelativeReferences(literal, literal);
        }

        @RequiredReadAction
        private static @Nullable PartiallyKnownString toPartiallyKnownString(PsiLiteralExpression literal) {
            if (!(literal.getValue() instanceof String value)) {
                return null;
            }
            return new PartiallyKnownString(value, literal, ElementManipulators.getValueTextRange(literal));
        }
    }

    private static final class TranslationKeyReferenceProvider extends PsiReferenceProvider {
        @Override
        @RequiredReadAction
        public PsiReference[] getReferencesByElement(PsiElement element, ProcessingContext context) {
            if (!(element instanceof PsiLiteralExpression literal)
                || !(literal.getValue() instanceof String key)
                || !VaadinProjectUtil.isInVaadinModule(literal)) {
                return PsiReference.EMPTY_ARRAY;
            }
            return new PsiReference[]{new TranslationKeyReference(key, literal)};
        }
    }

    /**
     * Highlighted like a property key; the key is looked up in every properties file, as Vaadin's I18N provider is free to
     * choose its bundle.
     */
    private static final class TranslationKeyReference extends PropertyReference implements HighlightedReference {
        private TranslationKeyReference(String key, PsiElement element) {
            super(key, element, null, false);
        }
    }

    private static final class StyleSheetReferenceProvider extends PsiReferenceProvider {
        @Override
        @RequiredReadAction
        public PsiReference[] getReferencesByElement(PsiElement element, ProcessingContext context) {
            if (!(element instanceof PsiLiteralExpression literal)
                || !(literal.getValue() instanceof String path)
                || !VaadinProjectUtil.isInVaadinModule(literal)) {
                return PsiReference.EMPTY_ARRAY;
            }
            return new PsiReference[]{new VaadinStyleSheetReference(literal, ElementManipulators.getValueTextRange(literal), path)};
        }
    }
}
