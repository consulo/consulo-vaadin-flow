# Development Guide

This repository is a Consulo fork of the [Vaadin IntelliJ plugin](https://github.com/vaadin/intellij-plugin). The plugin
is written in Java and built with Maven like every other Consulo plugin.

## Prerequisites

| Requirement | Version |
|---|---|
| JDK | 21 |
| Maven | 3.9 or newer |
| Consulo | a recent build, to develop and run the plugin |

## Build

```bash
mvn package -T 1C
```

This compiles the plugin, runs the unit tests and writes the plugin to `plugin/target/`. The Consulo platform and the
plugins this one depends on come from the snapshot repository `https://maven.consulo.dev/repository/snapshots/`.

## Run the plugin in a sandbox

Open the repository in Consulo and import it as a Maven project. The run configuration
`consulo:run-desktop-awt-fork` (in `.consulo/runConfigurations`) starts a sandbox Consulo with the plugin installed.

## Testing

- Plain logic, such as the download links of the starter projects, has JUnit 5 tests in `plugin/src/test`.
- Everything that needs the IDE is tested by hand in the sandbox, on a real Vaadin project: create one with
  File | New | Project | Vaadin, or open an existing Maven project depending on `com.vaadin:flow-server`.

## How the code is organized

- Packages mirror the upstream plugin: every Java class sits where its Kotlin original is (`com.vaadin.plugin.endpoints`,
  `.css`, `.module`, `.starter` ...), and keeps its name. When upstream changes a file, port the change into the class at
  the same path. Where Consulo has no counterpart of an IntelliJ API, the Java class says which upstream classes it
  replaces.
- Every feature is gated on the Vaadin module extension (`VaadinFlowModuleExtension`, checked through
  `VaadinProjectUtil`) rather than on finding Vaadin on the classpath. The Maven import enables it on modules depending on
  `com.vaadin:flow-server`.
- Code insight reads Java PSI; upstream reads UAST, which Consulo does not have, so Kotlin sources get none of it.
- User-visible text lives in `LOCALIZE-LIB` (`VaadinLocalize`), icons in `ICON-LIB` (`VaadinIconGroup`).
