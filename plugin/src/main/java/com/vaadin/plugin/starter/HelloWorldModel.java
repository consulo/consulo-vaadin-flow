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

import com.vaadin.plugin.utils.ArtifactIdUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * A Flow Hello World in Java or Kotlin, built with Maven, on one of the supported stacks.
 */
public class HelloWorldModel implements DownloadableModel {
    private final Supplier<String> myGroupId;

    private String myLanguage = StarterSupport.LANGUAGES.keySet().iterator().next();
    private String myArchitecture = StarterSupport.ARCHITECTURES.keySet().iterator().next();

    public HelloWorldModel(Supplier<String> groupId) {
        myGroupId = groupId;
    }

    public String getLanguage() {
        return myLanguage;
    }

    public void setLanguage(String language) {
        myLanguage = language;
    }

    public String getArchitecture() {
        return myArchitecture;
    }

    public void setArchitecture(String architecture) {
        myArchitecture = architecture;
    }

    @Override
    public String getDownloadLink(String projectName) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("framework", "flow");
        params.put("language", myLanguage);
        params.put("buildtool", "maven");
        params.put("stack", myArchitecture);
        params.put("artifactId", ArtifactIdUtil.toArtifactId(projectName));
        params.put("groupId", myGroupId.get());
        params.put("ref", REF);
        return "https://start.vaadin.com/helloworld?" + DownloadableModel.query(params);
    }
}
