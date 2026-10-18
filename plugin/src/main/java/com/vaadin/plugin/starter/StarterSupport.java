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
package com.vaadin.plugin.starter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The options of a Hello World project, with the combinations start.vaadin.com supports.
 * <p>
 * start.vaadin.com also builds Hello World projects with Gradle; the Consulo wizard generates and imports Maven projects
 * only, so the build tool is not an option here.
 */
public final class StarterSupport {
    public static final Map<String, String> LANGUAGES = linkedMap("java", "Java", "kotlin", "Kotlin");

    public static final Map<String, String> ARCHITECTURES =
        linkedMap("springboot", "Spring Boot", "quarkus", "Quarkus", "jakartaee", "Jakarta EE", "servlet", "Servlet");

    private StarterSupport() {
    }

    public static boolean isSupportedArchitecture(HelloWorldModel model, String architecture) {
        if (model.getLanguage().equals("kotlin")) {
            return architecture.equals("springboot");
        }
        return true;
    }

    private static Map<String, String> linkedMap(String... keysAndValues) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length; i += 2) {
            map.put(keysAndValues[i], keysAndValues[i + 1]);
        }
        return map;
    }
}
