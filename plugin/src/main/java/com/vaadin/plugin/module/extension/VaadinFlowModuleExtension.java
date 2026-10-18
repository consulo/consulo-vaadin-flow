package com.vaadin.plugin.module.extension;

import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkType;
import consulo.javascript.ecmascript.lang.EcmaScript12JavaScriptVersion;
import consulo.javascript.module.extension.JavaScriptModuleExtension;
import consulo.language.version.LanguageVersion;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionBase;
import consulo.module.content.layer.extension.SdkModuleInheritableNamedPointerImpl;
import consulo.module.extension.ModuleInheritableNamedPointer;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 17/06/2023
 */
public class VaadinFlowModuleExtension extends ModuleExtensionBase<VaadinFlowModuleExtension> implements JavaScriptModuleExtension<VaadinFlowModuleExtension> {
    /**
     * A Vaadin module has no SDK of its own. The pointer is never set, so it stays null and the platform adds no SDK order
     * entry for the extension; the module settings ask it whenever the extension is switched on.
     */
    private final SdkModuleInheritableNamedPointerImpl mySdkPointer;

    public VaadinFlowModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
        super(id, moduleRootLayer);
        mySdkPointer = new SdkModuleInheritableNamedPointerImpl(moduleRootLayer, id);
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return EcmaScript12JavaScriptVersion.getInstance();
    }

    @Override
    public ModuleInheritableNamedPointer<Sdk> getInheritableSdk() {
        return mySdkPointer;
    }

    @Override
    public @Nullable Sdk getSdk() {
        return mySdkPointer.get();
    }

    @Override
    public @Nullable String getSdkName() {
        return mySdkPointer.getName();
    }

    /**
     * Only an SDK chooser asks for the type, and the extension never shows one.
     */
    @Override
    public Class<? extends SdkType> getSdkTypeClass() {
        throw new UnsupportedOperationException("Vaadin Flow module extension has no SDK");
    }
}
