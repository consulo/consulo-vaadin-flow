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
import consulo.language.psi.PsiAnchor;

/**
 * A Flow route view or a Hilla browser callable service, with the class declaring it.
 */
public class VaadinRoute {
    private final String myUrlMapping;
    private final String myLocationString;
    private final PsiAnchor myAnchor;

    public VaadinRoute(String urlMapping, String locationString, PsiAnchor anchor) {
        myUrlMapping = urlMapping;
        myLocationString = locationString;
        myAnchor = anchor;
    }

    public String getUrlMapping() {
        return myUrlMapping;
    }

    public String getLocationString() {
        return myLocationString;
    }

    public PsiAnchor getAnchor() {
        return myAnchor;
    }

    @RequiredReadAction
    public boolean isValid() {
        return myAnchor.retrieve() != null;
    }
}
