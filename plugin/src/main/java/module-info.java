import org.jspecify.annotations.NullMarked;

/**
 * @author VISTALL
 * @since 24/01/2023
 */
@NullMarked
module consulo.vaadin.flow {
    requires consulo.application.api;
    requires consulo.application.content.api;
    requires consulo.code.editor.api;
    requires consulo.component.api;
    requires consulo.disposer.api;
    requires consulo.document.api;
    requires consulo.endpoint.api;
    requires consulo.file.editor.api;
    requires consulo.http.api;
    requires consulo.language.api;
    requires consulo.language.editor.api;
    requires consulo.language.inject.advanced.api;
    requires consulo.localize.api;
    requires consulo.logging.api;
    requires consulo.module.api;
    requires consulo.module.content.api;
    requires consulo.module.creation.api;
    requires consulo.platform.api;
    requires consulo.project.api;
    requires consulo.project.ui.api;
    requires consulo.ui.api;
    requires consulo.ui.ex.api;
    requires consulo.util.concurrent.coroutine;
    requires consulo.util.lang;
    requires consulo.virtual.file.system.api;

    requires consulo.java;
    requires consulo.javascript;
    requires consulo.javascript.ecmascript.impl;
    requires consulo.javascript.typescript.impl;
    requires org.jetbrains.idea.maven;
    requires com.intellij.properties;
    requires consulo.css.language.impl;
    requires consulo.css.xstylesheet.api;
}
