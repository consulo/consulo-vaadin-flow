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

import com.vaadin.plugin.starter.DownloadableModel;
import com.vaadin.plugin.starter.HelloWorldModel;
import com.vaadin.plugin.starter.StarterProjectModel;
import consulo.module.creation.NewModuleWizardContextBase;

/**
 * The choices of the Vaadin wizard: a starter project or a Hello World project, both in the same group.
 *
 * @author VISTALL
 * @since 2026-10-18
 */
public class VaadinNewModuleWizardContext extends NewModuleWizardContextBase {
    private String myGroupId = "com.example.application";
    private boolean myHelloWorld;

    private final StarterProjectModel myStarterProjectModel = new StarterProjectModel(this::getGroupId);
    private final HelloWorldModel myHelloWorldModel = new HelloWorldModel(this::getGroupId);

    public VaadinNewModuleWizardContext(boolean isNewProject) {
        super(isNewProject);
    }

    public String getGroupId() {
        return myGroupId;
    }

    public void setGroupId(String groupId) {
        myGroupId = groupId;
    }

    public boolean isHelloWorld() {
        return myHelloWorld;
    }

    public void setHelloWorld(boolean helloWorld) {
        myHelloWorld = helloWorld;
    }

    public StarterProjectModel getStarterProjectModel() {
        return myStarterProjectModel;
    }

    public HelloWorldModel getHelloWorldModel() {
        return myHelloWorldModel;
    }

    public DownloadableModel getProjectModel() {
        return myHelloWorld ? myHelloWorldModel : myStarterProjectModel;
    }
}
