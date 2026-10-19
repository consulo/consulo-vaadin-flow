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
import com.vaadin.plugin.localize.VaadinLocalize;
import com.vaadin.plugin.utils.VaadinNotificationGroupContributor;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicImpl;
import consulo.project.Project;
import consulo.project.ProjectPropertiesComponent;
import consulo.project.ui.notification.NotificationService;
import consulo.util.dataholder.Key;
import jakarta.inject.Inject;

import java.util.List;

/**
 * Offers to exclude java.awt from auto-import and completion in Vaadin projects, so that they stop suggesting AWT widgets.
 * <p>
 * The IntelliJ plugin excludes it without asking; here the user decides once per project, since a project may use AWT
 * as well, and the exclusion would break its imports.
 */
@TopicImpl(ComponentScope.PROJECT)
public final class VaadinImportExclusionListener implements VaadinProjectListener {
    private static final String JAVA_AWT_PACKAGE = "java.awt";

    // set by either answer, so that the question is not asked again
    private static final String ANSWERED_PROPERTY = "vaadin.import.exclusion.awt.answered";

    // the detection runs again on every root change, a question left open is not repeated until the project is reopened
    private static final Key<Boolean> ASKED = Key.create("vaadin.import.exclusion.awt.asked");

    private final NotificationService myNotificationService;

    @Inject
    public VaadinImportExclusionListener(NotificationService notificationService) {
        myNotificationService = notificationService;
    }

    @Override
    public void vaadinProjectDetected(Project project) {
        if (project.isDisposed() || isExcluded(project) || project.getUserData(ASKED) != null) {
            return;
        }

        ProjectPropertiesComponent properties = ProjectPropertiesComponent.getInstance(project);
        if (properties.getBoolean(ANSWERED_PROPERTY)) {
            return;
        }

        project.putUserData(ASKED, Boolean.TRUE);

        myNotificationService.newInfo(VaadinNotificationGroupContributor.SUGGESTIONS)
            .title(VaadinLocalize.importExclusionAwtTitle())
            .content(VaadinLocalize.importExclusionAwtContent())
            .suggestion()
            .removeWhenExpired()
            .addClosingAction(VaadinLocalize.importExclusionAwtYes(), () -> {
                properties.setValue(ANSWERED_PROPERTY, true);
                if (!isExcluded(project)) {
                    getExcludedNames(project).add(JAVA_AWT_PACKAGE);
                }
            })
            .addClosingAction(VaadinLocalize.importExclusionAwtNo(), () -> properties.setValue(ANSWERED_PROPERTY, true))
            .notify(project);
    }

    private static boolean isExcluded(Project project) {
        List<String> excluded = getExcludedNames(project);
        return excluded.contains(JAVA_AWT_PACKAGE) || excluded.contains(JAVA_AWT_PACKAGE + ".*");
    }

    private static List<String> getExcludedNames(Project project) {
        return JavaProjectCodeInsightSettings.getSettings(project).excludedNames;
    }
}
