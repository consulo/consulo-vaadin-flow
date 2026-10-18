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
package com.vaadin.plugin.module;

import com.vaadin.plugin.icon.VaadinIconGroup;
import com.vaadin.plugin.localize.VaadinLocalize;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.progress.ProgressBuilderFactory;
import consulo.http.HttpRequestBuilderFactory;
import consulo.module.content.layer.ContentEntry;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.module.creation.scratch.NewModuleBuilder;
import consulo.module.creation.scratch.NewModuleBuilderProcessor;
import consulo.module.creation.scratch.NewModuleContext;
import consulo.module.creation.scratch.NewModuleContextGroup;
import consulo.project.ui.notification.NotificationService;
import consulo.ui.ex.wizard.WizardStep;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.inject.Inject;

import java.util.function.Consumer;

/**
 * Vaadin in the JVM group of the new project wizard: a starter or Hello World project from start.vaadin.com.
 * <p>
 * The IntelliJ plugin offers it for new projects only; Consulo tells the wizard apart from the new module one only once the
 * entry is chosen, so it is offered for new modules as well, where the project lands in the module directory.
 *
 * @author VISTALL
 * @since 2026-10-18
 */
@ExtensionImpl(order = "after jvm")
public class VaadinNewModuleBuilder implements NewModuleBuilder {
    private final VaadinProjectCreator myProjectCreator;

    @Inject
    public VaadinNewModuleBuilder(
        ProgressBuilderFactory progressBuilderFactory,
        NotificationService notificationService,
        HttpRequestBuilderFactory httpRequestBuilderFactory
    ) {
        myProjectCreator = new VaadinProjectCreator(progressBuilderFactory, notificationService, httpRequestBuilderFactory);
    }

    @Override
    public void setupContext(NewModuleContext newModuleContext) {
        NewModuleContextGroup group = newModuleContext.findGroup("jvm");
        if (group == null) {
            return;
        }

        group.add(VaadinLocalize.wizardName(), VaadinIconGroup.vaadin(), new NewModuleBuilderProcessor<VaadinNewModuleWizardContext>() {
            @Override
            public VaadinNewModuleWizardContext createContext(boolean isNewProject) {
                return new VaadinNewModuleWizardContext(isNewProject);
            }

            @Override
            public void buildSteps(Consumer<WizardStep<VaadinNewModuleWizardContext>> consumer, VaadinNewModuleWizardContext context) {
                consumer.accept(new VaadinProjectOrModuleNameStep(context));
            }

            @RequiredReadAction
            @Override
            public void process(VaadinNewModuleWizardContext context, ContentEntry contentEntry, ModifiableRootModel modifiableRootModel) {
                VirtualFile projectDir = contentEntry.getFile();
                if (projectDir == null) {
                    return;
                }
                String downloadUrl = context.getProjectModel().getDownloadLink(context.getName());
                myProjectCreator.createAndImport(modifiableRootModel.getProject(), projectDir, downloadUrl);
            }
        });
    }
}
