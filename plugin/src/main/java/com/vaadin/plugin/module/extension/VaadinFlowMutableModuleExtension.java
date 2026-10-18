package com.vaadin.plugin.module.extension;

import consulo.content.bundle.Sdk;
import consulo.disposer.Disposable;
import consulo.javascript.module.extension.JavaScriptMutableModuleExtension;
import consulo.language.version.LanguageVersion;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.MutableModuleInheritableNamedPointer;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 17/06/2023
 */
public class VaadinFlowMutableModuleExtension extends VaadinFlowModuleExtension implements JavaScriptMutableModuleExtension<VaadinFlowModuleExtension> {
    public VaadinFlowMutableModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
        super(id, moduleRootLayer);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk() {
        return (MutableModuleInheritableNamedPointer<Sdk>) super.getInheritableSdk();
    }

    @Override
    public void setLanguageVersion(LanguageVersion languageVersion) {
    }

    @RequiredUIAccess
    @Override
    public @Nullable Component createConfigurationComponent(Disposable disposable, Runnable runnable) {
        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        myIsEnabled = enabled;
    }

    @Override
    public boolean isModified(VaadinFlowModuleExtension extension) {
        return myIsEnabled != extension.isEnabled();
    }
}
