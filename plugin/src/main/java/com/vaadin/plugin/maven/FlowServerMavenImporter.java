package com.vaadin.plugin.maven;

import com.vaadin.plugin.module.extension.VaadinFlowModuleExtension;
import consulo.annotation.component.ExtensionImpl;
import consulo.maven.importing.MavenImporterFromDependency;
import consulo.module.Module;
import org.jetbrains.idea.maven.importing.MavenModifiableModelsProvider;
import org.jetbrains.idea.maven.importing.MavenRootModelAdapter;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectChanges;
import org.jetbrains.idea.maven.project.MavenProjectsProcessorTask;
import org.jetbrains.idea.maven.project.MavenProjectsTree;

import java.util.List;
import java.util.Map;

/**
 * Enables {@link VaadinFlowModuleExtension} on every Maven module depending on {@code com.vaadin:flow-server}, the
 * artifact that brings {@code com.vaadin.flow.server.VaadinService}, by which the IntelliJ plugin detects Vaadin.
 *
 * @author VISTALL
 * @since 17/06/2023
 */
@ExtensionImpl
public class FlowServerMavenImporter extends MavenImporterFromDependency {
    public FlowServerMavenImporter() {
        super("com.vaadin", "flow-server");
    }

    @Override
    public void preProcess(
        Module module,
        MavenProject mavenProject,
        MavenProjectChanges mavenProjectChanges,
        MavenModifiableModelsProvider mavenModifiableModelsProvider
    ) {
    }

    @Override
    public void process(
        MavenModifiableModelsProvider mavenModifiableModelsProvider,
        Module module,
        MavenRootModelAdapter mavenRootModelAdapter,
        MavenProjectsTree mavenProjectsTree,
        MavenProject mavenProject,
        MavenProjectChanges mavenProjectChanges,
        Map<MavenProject, String> map,
        List<MavenProjectsProcessorTask> list
    ) {
        enableModuleExtension(module, mavenModifiableModelsProvider, VaadinFlowModuleExtension.class);
    }
}
