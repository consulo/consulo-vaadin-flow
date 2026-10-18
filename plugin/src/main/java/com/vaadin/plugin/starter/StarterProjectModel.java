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
 * A walking skeleton with Flow and/or Hilla, the stable or the prerelease platform.
 */
public class StarterProjectModel implements DownloadableModel {
    private final Supplier<String> myGroupId;

    private boolean myUsePrerelease;
    private boolean myIncludeFlow = true;
    private boolean myIncludeHilla;

    public StarterProjectModel(Supplier<String> groupId) {
        myGroupId = groupId;
    }

    public boolean isUsePrerelease() {
        return myUsePrerelease;
    }

    public void setUsePrerelease(boolean usePrerelease) {
        myUsePrerelease = usePrerelease;
    }

    public boolean isIncludeFlow() {
        return myIncludeFlow;
    }

    public void setIncludeFlow(boolean includeFlow) {
        myIncludeFlow = includeFlow;
    }

    public boolean isIncludeHilla() {
        return myIncludeHilla;
    }

    public void setIncludeHilla(boolean includeHilla) {
        myIncludeHilla = includeHilla;
    }

    @Override
    public String getDownloadLink(String projectName) {
        String frameworks;
        if (myIncludeFlow && myIncludeHilla) {
            frameworks = "flow,hilla";
        }
        else if (myIncludeFlow) {
            frameworks = "flow";
        }
        else if (myIncludeHilla) {
            frameworks = "hilla";
        }
        else {
            frameworks = "empty";
        }
        String platformVersion = myUsePrerelease ? "pre" : "latest";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("frameworks", frameworks);
        params.put("platformVersion", platformVersion);
        params.put("artifactId", ArtifactIdUtil.toArtifactId(projectName));
        params.put("groupId", myGroupId.get());
        params.put("ref", REF);
        return "https://start.vaadin.com/skeleton?" + DownloadableModel.query(params);
    }
}
