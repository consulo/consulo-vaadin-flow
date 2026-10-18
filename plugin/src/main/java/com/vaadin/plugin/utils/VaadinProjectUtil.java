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
package com.vaadin.plugin.utils;

import com.vaadin.plugin.module.extension.VaadinFlowModuleExtension;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.PsiElement;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.extension.ModuleExtensionHelper;
import consulo.project.Project;
import org.jspecify.annotations.Nullable;

/**
 * Where the IntelliJ plugin looks for {@code VaadinService} on the classpath, the Consulo port asks for
 * {@link VaadinFlowModuleExtension}: the Maven import enables it, and the user can switch it on by hand.
 *
 * @author VISTALL
 * @since 2026-10-18
 */
public final class VaadinProjectUtil {
    private VaadinProjectUtil() {
    }

    public static boolean isVaadinModule(@Nullable Module module) {
        return module != null && ModuleUtilCore.getExtension(module, VaadinFlowModuleExtension.class) != null;
    }

    @RequiredReadAction
    public static boolean isInVaadinModule(PsiElement element) {
        return isVaadinModule(element.getModule());
    }

    public static boolean hasVaadinModules(Project project) {
        return ModuleExtensionHelper.getInstance(project).hasModuleExtension(VaadinFlowModuleExtension.class);
    }
}
