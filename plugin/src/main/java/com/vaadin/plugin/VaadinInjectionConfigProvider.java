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

import consulo.annotation.component.ExtensionImpl;
import consulo.language.inject.advanced.InjectionConfigProvider;

import java.io.InputStream;

/**
 * JavaScript in {@code Element.executeJs}, HTML in {@code Html} and a regular expression in {@code TextField.setPattern}.
 * The configuration is the one of the IntelliJ plugin, under the root element Consulo expects.
 *
 * @author VISTALL
 * @since 2026-10-18
 */
@ExtensionImpl
public class VaadinInjectionConfigProvider implements InjectionConfigProvider {
    @Override
    public InputStream openConfigFileStream() throws Exception {
        return getClass().getResourceAsStream("/vaadin/flow-language-injections.xml");
    }
}
