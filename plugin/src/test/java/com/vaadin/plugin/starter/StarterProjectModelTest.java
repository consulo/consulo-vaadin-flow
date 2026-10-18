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

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StarterProjectModelTest {
    @Test
    public void groupIdCanBeChanged() {
        AtomicReference<String> groupId = new AtomicReference<>("com.vaadin.application");
        StarterProjectModel model = new StarterProjectModel(groupId::get);
        groupId.set("org.example");
        assertTrue(model.getDownloadLink("demo").contains("groupId=org.example"));
    }

    @Test
    public void groupIdIsIncludedInDownloadUrl() {
        AtomicReference<String> groupId = new AtomicReference<>("com.vaadin.application");
        StarterProjectModel model = new StarterProjectModel(groupId::get);
        groupId.set("org.test");
        String url = model.getDownloadLink("demo");
        assertTrue(url.contains("groupId=org.test"));
    }
}
