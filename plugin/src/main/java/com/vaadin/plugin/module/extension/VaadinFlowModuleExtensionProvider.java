package com.vaadin.plugin.module.extension;

import com.vaadin.plugin.icon.VaadinIconGroup;
import com.vaadin.plugin.localize.VaadinLocalize;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.image.Image;
import org.jspecify.annotations.Nullable;

/**
 * @author VISTALL
 * @since 17/06/2023
 */
@ExtensionImpl
public class VaadinFlowModuleExtensionProvider implements ModuleExtensionProvider<VaadinFlowModuleExtension> {
    public static final String ID = "vaadin-flow";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public @Nullable String getParentId() {
        return "java";
    }

    @Override
    public LocalizeValue getName() {
        return VaadinLocalize.vaadinFlowModuleExtensionName();
    }

    @Override
    public Image getIcon() {
        return VaadinIconGroup.vaadin();
    }

    @Override
    public ModuleExtension<VaadinFlowModuleExtension> createImmutableExtension(ModuleRootLayer moduleRootLayer) {
        return new VaadinFlowModuleExtension(getId(), moduleRootLayer);
    }

    @Override
    public MutableModuleExtension<VaadinFlowModuleExtension> createMutableExtension(ModuleRootLayer moduleRootLayer) {
        return new VaadinFlowMutableModuleExtension(getId(), moduleRootLayer);
    }
}
