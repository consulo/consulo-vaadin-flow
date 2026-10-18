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
package com.vaadin.plugin.module;

import com.vaadin.plugin.localize.VaadinLocalize;
import com.vaadin.plugin.starter.HelloWorldModel;
import com.vaadin.plugin.starter.StarterProjectModel;
import com.vaadin.plugin.starter.StarterSupport;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.module.creation.ui.UnifiedProjectOrModuleNameStep;
import consulo.platform.Platform;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.Hyperlink;
import consulo.ui.Label;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.wizard.WizardStepValidationException;
import consulo.ui.layout.FoldoutLayout;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * The name, location and group of the project, and either a starter project or a Hello World project; opening one of the
 * two sections closes the other, as in the IntelliJ wizard.
 * <p>
 * The IntelliJ wizard shows the Hello World options as segmented buttons with the unsupported combinations disabled; combo
 * boxes cannot disable an item, so an unsupported choice falls back to the first supported one.
 */
public class VaadinProjectOrModuleNameStep extends UnifiedProjectOrModuleNameStep<VaadinNewModuleWizardContext> {
    private static final String GETTING_STARTED_URL = "https://vaadin.com/docs/latest/getting-started";

    private static final Pattern GROUP_PATTERN = Pattern.compile("^(?!\\.)(?!.*\\.\\.)(?=.*\\.)([A-Za-z0-9_.]+)(?<!\\.)$");

    private final VaadinNewModuleWizardContext myContext;

    private @Nullable TextBox myGroupIdBox;
    private boolean myHelloWorldSelected;
    private @Nullable ComboBox<Boolean> myVersionBox;
    private @Nullable CheckBox myIncludeFlowBox;
    private @Nullable ComboBox<String> myArchitectureBox;

    public VaadinProjectOrModuleNameStep(VaadinNewModuleWizardContext context) {
        super(context);
        myContext = context;
    }

    @Override
    @RequiredUIAccess
    protected void extend(FormBuilder builder, Disposable uiDisposable) {
        TextBox groupIdBox = TextBox.create(myContext.getGroupId());
        myGroupIdBox = groupIdBox;
        builder.addLabeled(VaadinLocalize.wizardGroupLabel(), groupIdBox);

        FoldoutLayout starterFoldout = FoldoutLayout.create(VaadinLocalize.wizardStarterProject(), buildStarterProjectPanel(), !myContext.isHelloWorld());
        FoldoutLayout helloWorldFoldout =
            FoldoutLayout.create(VaadinLocalize.wizardHelloWorldProjects(), buildHelloWorldPanel(), myContext.isHelloWorld());
        myHelloWorldSelected = myContext.isHelloWorld();
        starterFoldout.addOpenedListener(event -> {
            if (event.isOpened()) {
                myHelloWorldSelected = false;
                helloWorldFoldout.setState(false);
            }
        });
        helloWorldFoldout.addOpenedListener(event -> {
            if (event.isOpened()) {
                myHelloWorldSelected = true;
                starterFoldout.setState(false);
            }
        });

        VerticalLayout bottom = VerticalLayout.create();
        bottom.add(starterFoldout);
        bottom.add(helloWorldFoldout);
        // the IntelliJ wizard describes the guide in a paragraph wrapped at 60 characters; a Consulo label does not wrap, and a
        // one-line paragraph would stretch the wizard past the screen, so only the link is kept
        bottom.add(Hyperlink.create(
            VaadinLocalize.wizardGettingStartedTitle(),
            event -> Platform.current().openInBrowser(GETTING_STARTED_URL)
        ));
        builder.addBottom(bottom);
    }

    @RequiredUIAccess
    private Component buildStarterProjectPanel() {
        StarterProjectModel model = myContext.getStarterProjectModel();

        ComboBox<Boolean> versionBox = ComboBox.create(false, true);
        versionBox.setTextRenderer(prerelease -> Boolean.TRUE.equals(prerelease)
            ? VaadinLocalize.wizardVaadinVersionPrerelease()
            : VaadinLocalize.wizardVaadinVersionStable());
        versionBox.setValue(model.isUsePrerelease());
        myVersionBox = versionBox;

        CheckBox includeFlowBox = CheckBox.create(VaadinLocalize.wizardSampleViewTitle());
        includeFlowBox.setValue(model.isIncludeFlow());
        myIncludeFlowBox = includeFlowBox;

        FormBuilder builder = FormBuilder.create();
        builder.addLabeled(VaadinLocalize.wizardVaadinVersionLabel(), versionBox);
        builder.addBottom(includeFlowBox);
        builder.addBottom(Label.create(VaadinLocalize.wizardSampleViewDescription()));
        return builder.build();
    }

    @RequiredUIAccess
    private Component buildHelloWorldPanel() {
        HelloWorldModel model = myContext.getHelloWorldModel();

        ComboBox<String> languageBox = createComboBox(StarterSupport.LANGUAGES, model.getLanguage());
        ComboBox<String> architectureBox = createComboBox(StarterSupport.ARCHITECTURES, model.getArchitecture());
        myArchitectureBox = architectureBox;

        Label kotlinInfo = Label.create(VaadinLocalize.wizardKotlinInfo());
        kotlinInfo.setVisible(model.getLanguage().equals("kotlin"));

        languageBox.addValueListener(event -> {
            model.setLanguage(StringUtil.notNullize(event.getValue()));
            kotlinInfo.setVisible(model.getLanguage().equals("kotlin"));
            refreshSegments();
        });
        architectureBox.addValueListener(event -> {
            model.setArchitecture(StringUtil.notNullize(event.getValue()));
            refreshSegments();
        });

        FormBuilder builder = FormBuilder.create();
        builder.addLabeled(VaadinLocalize.wizardLanguageLabel(), languageBox);
        builder.addLabeled(VaadinLocalize.wizardArchitectureLabel(), architectureBox);
        // FormBuilder takes no labeled row after a bottom one
        builder.addBottom(kotlinInfo);
        return builder.build();
    }

    /**
     * Falls back to the first supported value of every option whose current value the other options do not support.
     */
    @RequiredUIAccess
    private void refreshSegments() {
        HelloWorldModel model = myContext.getHelloWorldModel();
        fallbackToFirstSupported(myArchitectureBox, StarterSupport.ARCHITECTURES, model.getArchitecture(), StarterSupport::isSupportedArchitecture);
    }

    @RequiredUIAccess
    private void fallbackToFirstSupported(
        @Nullable ComboBox<String> comboBox,
        Map<String, String> values,
        String value,
        BiPredicate<HelloWorldModel, String> supported
    ) {
        HelloWorldModel model = myContext.getHelloWorldModel();
        if (comboBox == null || supported.test(model, value)) {
            return;
        }
        for (String candidate : values.keySet()) {
            if (supported.test(model, candidate)) {
                // the value listener stores it in the model
                comboBox.setValue(candidate);
                return;
            }
        }
    }

    @RequiredUIAccess
    private static ComboBox<String> createComboBox(Map<String, String> values, String selected) {
        List<String> keys = new ArrayList<>(values.keySet());
        ComboBox<String> comboBox = ComboBox.create(keys);
        comboBox.setTextRenderer(key -> key == null ? LocalizeValue.empty() : LocalizeValue.of(values.getOrDefault(key, key)));
        comboBox.setValue(selected);
        return comboBox;
    }

    @Override
    @RequiredUIAccess
    public CompletableFuture<?> validateStep(VaadinNewModuleWizardContext context) {
        TextBox groupIdBox = myGroupIdBox;
        if (groupIdBox != null && !GROUP_PATTERN.matcher(StringUtil.notNullize(groupIdBox.getValue()).trim()).matches()) {
            return CompletableFuture.failedFuture(new WizardStepValidationException(groupIdBox, VaadinLocalize.wizardGroupInvalid()));
        }
        return super.validateStep(context);
    }

    @Override
    @RequiredUIAccess
    public void onStepLeave(VaadinNewModuleWizardContext context) {
        // stores the name and the location
        super.onStepLeave(context);

        if (myGroupIdBox != null) {
            context.setGroupId(StringUtil.notNullize(myGroupIdBox.getValue()).trim());
        }
        context.setHelloWorld(myHelloWorldSelected);

        StarterProjectModel starter = context.getStarterProjectModel();
        if (myVersionBox != null) {
            starter.setUsePrerelease(Boolean.TRUE.equals(myVersionBox.getValue()));
        }
        if (myIncludeFlowBox != null) {
            starter.setIncludeFlow(Boolean.TRUE.equals(myIncludeFlowBox.getValue()));
        }
    }
}
