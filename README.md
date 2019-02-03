# Extensible File Processor

Command-line tool for batch-processing files of arbitrary type, that includes its own plugin-framework, provides an API for plugin-developers and supports specification of runtime-dependencies among plugins (usefull if some plugin depends on the output of other plugins).


### Motivation

- includes plugin-framework to make FileProcessor extensible

- provides Extension-API for plugin-developers

- extensible by providing a Plug-in interface API for plugin-developers

- runtime-dependencies among plugins may be list-like, tree-like or graph-like (directed and acyclic)

- software architecture and design-patterns


### Features

- inspired by the concept of command-pipelines on the linux shell (namely bash)

- resolves runtime-dependencies in a Makefile/ant-like style

- each plugin is a file-converter, that is able to consume and produce files


### Example :: FileProcessorTest.testFileProcessor1()

This example demonstrates usage of different FileProcessor built-in plugins. It takes a textfile contaning a list of java-files and injects some static import statements by modifying the AbstractSyntaxTree (AST) of the corresponding Java-Code. Then it compares the original java-file with the rewritten one and outputs the diff to stderr.

- this JUnit.TestCase sets-up the following dependency graph …

  ![DependencyGraph](https://raw.githubusercontent.com/anblt/extensible-file-processor/master/docs/FileProcessorTest.png)

- … and produces the following output

    ```
    > import static java.lang.System.out;
    > import static java.lang.System.err;
    ```


### Annotations for FileProcessor-Plugins

- @Unary only

  plugin produces as many files as it consumes

  plugin processes exactly 1 file at every single call

  Example: JavaImportDeclarationInjector.java

- @Demuxer + @Unary

  plugin produces more files, than it consumes

  plugin processes exactly 1 file at every single call

  Example: FileListReader

- @Muxer + @Binary

  plugin produces less files, than it consumes

  plugin processes exactly 2 file at every single call



