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
package com.vaadin.plugin.listeners;

import com.intellij.java.impl.codeInsight.JavaProjectCodeInsightSettings;
import com.vaadin.plugin.listeners.VaadinProjectListener;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicImpl;
import consulo.project.Project;

import java.util.List;

/**
 * Adds java.awt to the excluded import/completion list for Vaadin projects to avoid suggesting AWT widgets.
 */
@TopicImpl(ComponentScope.PROJECT)
public final class VaadinImportExclusionListener implements VaadinProjectListener {
    private static final String JAVA_AWT_PACKAGE = "java.awt";

    @Override
    public void vaadinProjectDetected(Project project) {
        if (project.isDisposed()) {
            return;
        }

        List<String> excluded = JavaProjectCodeInsightSettings.getSettings(project).excludedNames;
        if (!excluded.contains(JAVA_AWT_PACKAGE) && !excluded.contains(JAVA_AWT_PACKAGE + ".*")) {
            excluded.add(JAVA_AWT_PACKAGE);
        }
    }
}
