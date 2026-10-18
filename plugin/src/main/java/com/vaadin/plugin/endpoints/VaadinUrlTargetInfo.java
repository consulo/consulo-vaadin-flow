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

import com.vaadin.plugin.icon.VaadinIconGroup;
import consulo.annotation.access.RequiredReadAction;
import consulo.endpoint.url.Authority;
import consulo.endpoint.url.UrlConstants;
import consulo.endpoint.url.UrlPath;
import consulo.endpoint.url.UrlTargetInfo;
import consulo.language.psi.PsiAnchor;
import consulo.language.psi.PsiElement;
import consulo.ui.image.Image;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class VaadinUrlTargetInfo implements UrlTargetInfo {
    private final PsiAnchor myAnchor;
    private final UrlPath myPath;

    public VaadinUrlTargetInfo(VaadinRoute route) {
        myAnchor = route.getAnchor();
        myPath = VaadinUrlParser.parseVaadinUrlMapping(route.getUrlMapping());
    }

    @Override
    public List<String> getSchemes() {
        return UrlConstants.HTTP_SCHEMES;
    }

    @Override
    public List<Authority> getAuthorities() {
        return List.of();
    }

    @Override
    public UrlPath getPath() {
        return myPath;
    }

    @Override
    public Image getIcon() {
        return VaadinIconGroup.vaadin();
    }

    @Override
    @RequiredReadAction
    public @Nullable PsiElement resolveToPsiElement() {
        return myAnchor.retrieve();
    }
}
