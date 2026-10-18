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
import com.vaadin.plugin.utils.DownloadUtil;
import com.vaadin.plugin.utils.VaadinNotificationGroupContributor;
import consulo.application.concurrent.coroutine.ReadLock;
import consulo.application.progress.ProgressBuilderFactory;
import consulo.http.HttpRequestBuilderFactory;
import consulo.localize.LocalizeValue;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.project.ui.notification.NotificationService;
import consulo.ui.UIAction;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.concurrent.coroutine.Coroutine;
import consulo.util.concurrent.coroutine.step.CodeExecution;
import consulo.util.concurrent.coroutine.step.CompletableFutureStep;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Downloads the project start.vaadin.com generates into the directory of the new project and imports it as a Maven project.
 */
public class VaadinProjectCreator {
    private static final Logger LOG = Logger.getInstance(VaadinProjectCreator.class);

    private final ProgressBuilderFactory myProgressBuilderFactory;
    private final NotificationService myNotificationService;
    private final HttpRequestBuilderFactory myHttpRequestBuilderFactory;

    public VaadinProjectCreator(
        ProgressBuilderFactory progressBuilderFactory,
        NotificationService notificationService,
        HttpRequestBuilderFactory httpRequestBuilderFactory
    ) {
        myProgressBuilderFactory = progressBuilderFactory;
        myNotificationService = notificationService;
        myHttpRequestBuilderFactory = httpRequestBuilderFactory;
    }

    @RequiredUIAccess
    public void createAndImport(Project project, VirtualFile projectDir, String downloadUrl) {
        Path outputDir = projectDir.toNioPath();
        myProgressBuilderFactory.newProgressBuilder(project, VaadinLocalize.wizardProgressDownloading())
            .execute(project.getUIAccess(), () -> Coroutine
                .first(CodeExecution.<Object, Path>apply(input -> DownloadUtil.download(myHttpRequestBuilderFactory, downloadUrl, outputDir)))
                // the files were written behind the VFS, the import looks for them in it
                .then(CompletableFutureStep.<Path, Path>await(path -> refresh(projectDir, path)))
                // looking the pom up may touch the disk, which the UI thread must not
                .then(ReadLock.<Path, Optional<VirtualFile>>apply(path -> Optional.ofNullable(projectDir.findChild("pom.xml"))))
                // not from the refresh callback - it runs in a write action, where the import must not start
                .then(UIAction.<Optional<VirtualFile>, Optional<VirtualFile>>apply(pom -> {
                    pom.ifPresent(file -> importMavenProject(project, file));
                    return pom;
                })))
            .whenComplete((result, error) -> {
                if (error != null) {
                    LOG.warn("Vaadin project download failed: " + downloadUrl, error);
                    myNotificationService.newError(VaadinNotificationGroupContributor.GROUP)
                        .title(VaadinLocalize.wizardDownloadFailedTitle())
                        .content(LocalizeValue.of(String.valueOf(error.getMessage())))
                        .notify(project);
                }
            });
    }

    private static CompletableFuture<Path> refresh(VirtualFile projectDir, Path path) {
        CompletableFuture<Path> future = new CompletableFuture<>();
        projectDir.refresh(true, true, () -> future.complete(path));
        return future;
    }

    /**
     * The upstream plugin also opens the README; Consulo opens it by itself when the Markdown plugin is installed.
     */
    @RequiredUIAccess
    private static void importMavenProject(Project project, VirtualFile pom) {
        if (project.isDisposed()) {
            return;
        }
        // the same as the Maven new module wizard: an incremental update right after the first registration finds nothing
        // changed and leaves the module unconfigured, a forced one imports it
        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
        manager.addManagedFilesOrUnignoreNoUpdate(List.of(pom));
        manager.forceUpdateAllProjectsOrFindAllAvailablePomFiles();
    }
}
