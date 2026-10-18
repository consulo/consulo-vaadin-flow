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
package com.vaadin.plugin;

import com.vaadin.plugin.listeners.VaadinProjectListener;
import com.vaadin.plugin.utils.VaadinProjectUtil;
import consulo.annotation.component.ExtensionImpl;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.project.startup.PostStartupActivity;
import consulo.ui.UIAccess;

/**
 * Tells {@link VaadinProjectListener}s about a Vaadin project when it is opened, and again whenever its module roots change
 * (see {@link VaadinProjectRootListener}), which covers a Maven import enabling the Vaadin module extension.
 * <p>
 * The IntelliJ plugin looks for {@code VaadinService} on the classpath in a cancellable read action; asking for the module
 * extension needs no index, so there is nothing to wait for here.
 */
@ExtensionImpl
public final class VaadinProjectDetector implements PostStartupActivity {
    private static final Logger LOG = Logger.getInstance(VaadinProjectDetector.class);

    @Override
    public void runActivity(Project project, UIAccess uiAccess) {
        detectAndNotifyAboutVaadinProject(project);
    }

    static void detectAndNotifyAboutVaadinProject(Project project) {
        if (project.isDisposed() || !VaadinProjectUtil.hasVaadinModules(project)) {
            return;
        }
        project.getMessageBus().syncPublisher(VaadinProjectListener.class).vaadinProjectDetected(project);
        LOG.info("Vaadin project detected " + project.getName());
    }
}
