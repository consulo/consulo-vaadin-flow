# Working in this repository

This is the Consulo fork of the Vaadin IntelliJ plugin. Read [DEVELOPMENT.md](DEVELOPMENT.md) first; the points below
are the ones most easily got wrong.

## Code

- Java, Maven, JPMS. The module is `@NullMarked` and NullAway runs in the build: mark what can be null with
  `org.jspecify.annotations.Nullable`, nothing else.
- Keep upstream's package layout and class names, so an upstream change can be found and ported file by file. Upstream is
  Kotlin at https://github.com/vaadin/intellij-plugin.
- Gate features on the Vaadin module extension through `VaadinProjectUtil`, never on the classpath.
- Read Java PSI, not UAST.
- When the Consulo platform or a plugin lacks an API, add it in the owning repository instead of working around it.
- Out of scope for now: the Vaadin Copilot integration, usage statistics, the status bar widget and debugging with
  HotSwap Agent. Do not port them back without being asked.
- The new project wizard generates and imports Maven projects only.

## Before committing

```bash
mvn package -T 1C
```

Unit tests cover plain logic only; IDE behaviour is checked by hand in the sandbox on a real Vaadin project.
