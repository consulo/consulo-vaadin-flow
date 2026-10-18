/*
 * Copyright 2013-2026 consulo.io
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
package com.vaadin.plugin;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicImpl;
import consulo.module.content.layer.event.ModuleRootEvent;
import consulo.module.content.layer.event.ModuleRootListener;
import consulo.project.Project;
import consulo.ui.UIAccess;
import jakarta.inject.Inject;

/**
 * Runs the detection once the root change, which happens under a write action, is over.
 *
 * @author VISTALL
 * @since 2026-10-18
 */
@TopicImpl(ComponentScope.PROJECT)
public final class VaadinProjectRootListener implements ModuleRootListener {
    private final Project myProject;

    @Inject
    public VaadinProjectRootListener(Project project) {
        myProject = project;
    }

    @Override
    public void rootsChanged(ModuleRootEvent event) {
        UIAccess uiAccess = myProject.getUIAccess();
        if (myProject.isOpen() && uiAccess.isValid()) {
            uiAccess.give(() -> {
                if (!myProject.isDisposed()) {
                    VaadinProjectDetector.detectAndNotifyAboutVaadinProject(myProject);
                }
            });
        }
    }
}
