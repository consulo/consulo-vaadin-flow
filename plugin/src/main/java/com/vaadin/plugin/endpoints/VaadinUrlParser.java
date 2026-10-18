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

import consulo.endpoint.url.UrlPath;
import consulo.endpoint.url.reference.UrlPksParser;
import consulo.language.psi.util.PartiallyKnownString;
import consulo.language.psi.util.SplitEscaper;

/**
 * Route templates of Vaadin Flow: a segment starting with {@code :} is a parameter, as in {@code user/:id?} or
 * {@code item/:id(\d+)}.
 */
public final class VaadinUrlParser {
    public static final UrlPksParser VAADIN_URL_PKS_PARSER = new UrlPksParser(
        (charSequence, delimiter) -> SplitEscaper.ACCEPT_ALL,
        part -> {
            if (part.startsWith(":")) {
                String varName = substringBefore(substringBefore(part.substring(1), "?"), "(");
                return new UrlPath.PathSegment.Variable(varName);
            }
            return new UrlPath.PathSegment.Exact(part);
        }
    );

    private VaadinUrlParser() {
    }

    public static UrlPath parseVaadinUrlMapping(String urlMapping) {
        return VAADIN_URL_PKS_PARSER.parseUrlPath(new PartiallyKnownString(urlMapping)).getUrlPath();
    }

    private static String substringBefore(String value, String delimiter) {
        int index = value.indexOf(delimiter);
        return index < 0 ? value : value.substring(0, index);
    }
}
