# Vaadin plugin for Consulo

A [Consulo](https://consulo.io) fork of the official [Vaadin IntelliJ plugin](https://github.com/vaadin/intellij-plugin).

Improves developer experience while working with the [Vaadin](https://vaadin.com) framework:

- Creates new Vaadin projects from the Starter and Hello World projects of [start.vaadin.com](https://start.vaadin.com).
- Lists [Flow](https://vaadin.com/flow) `@Route` views and Hilla `@BrowserCallable` services in the Endpoints tool window, and
  resolves URLs to them.
- Completes CSS class names in `addClassName(...)` and the other `HasStyle` methods.
- Resolves `getTranslation("key")` to properties, `@StyleSheet` paths to stylesheets and `@Route` templates to URL paths.
- Injects JavaScript into `Element.executeJs`, HTML into `Html` and regular expressions into `TextField.setPattern`.
- Navigates from the TypeScript client of a Hilla service to its Java class and methods, and finds their usages in it.

Not ported from the IntelliJ plugin: the Vaadin Copilot integration, usage statistics and debugging with HotSwap Agent.