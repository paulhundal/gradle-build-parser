# Gradle AST Parser

### What is an *AST*?

An Abstract Syntax Tree (AST) is a data structure used to represent the structure of source
code in a tree-like format. It provides a way to analyze, transform and generate code by 
breaking down the source code into its constituent elements, such as statements, expressions
and operations, and organizing them in a tree-like hierarchy. 
An AST allows for easier processing of source code, as the structure of the code is 
represented in a convenient, organized and easily traversable form.

### What are Gradle Build Files?

Gradle build file, also known as build.gradle, is a script file written in Groovy or Kotlin
that defines how a project is built in the Gradle build system. 
The build file specifies the project's dependencies, configuration settings, 
tasks and other information required to build the project. 
The build file is executed by Gradle to produce a desired output, such as a compiled 
library or application. The Gradle build system is widely used in Java-based projects and 
is known for its flexibility, extensibility, and performance. 
The build.gradle file plays a critical role in defining the build process for a project, 
making it a fundamental component of any Gradle-based project.

### What is the relationship between an AST and a Build file?

The relationship between a build file and its Abstract Syntax Tree (AST) is that the build 
file is the source code, and the AST is the representation of that source code in a 
structured format. The AST is generated from the build file by parsing the source code and
breaking it down into its constituent elements, such as statements, expressions, and 
operations. The elements are then organized in a tree-like hierarchy, representing the 
structure of the code. The resulting AST provides a convenient and organized representation 
of the build file that can be easily processed and transformed, making it a useful tool 
for analyzing and manipulating the build file. In short, the build file is the source code,
and the AST is its structured representation.


## Introduction

Gradle AST Parser provides the capabilities to enforce rules on your build files without
relying on regex. The AST is built by the Groovy compiler (*this app does not support kotlin build files*)
and is parsed based on a project configuration you define. 

Here is a small example of a project configuration for running the parser on this repository.

```json
  "projects": {
    "ast-parser": {
      "name": "ast-parser",
      "path": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser",
      "allowlistClosuresPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/allowlist-closures.txt",
      "ignoreBuildsPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/ignore-builds.txt"
    },
```

**Read the section on getting started for how to use this configuration**

## Getting Started

Gradle AST Parser is a Kotlin application that can be run by invoking gradle. 
This app uses PicoCli to make it easy to interact with the app from the command line.
Once you follow these initial set up instruction, see the section on **common commands**
on how to use the CLI.

* Clone Repository (https://github.com/paulhundal/gradle-build-parser) 
* Go to gradle-ast-parser root (https://github.com/paulhundal/gradle-build-parser/gradle-ast-parser)
* Open the file `project-catalog.json`

This file is the source of truth for how this app works. To support more than the project(s)
initially listed, change them, or modify them, please make appropriate changes here for your
individual needs.

For example:

```json
{
  "projects": {
    "ast-parser": {
      "name": "ast-parser", // name of the project
      "path": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser", // path to the repo
      "allowlistClosuresPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/allowlist-closures.txt", // path to list of allowed closures. by default all are allowed. you can leave this empty if desired.
      "ignoreBuildsPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/ignore-builds.txt" // path to list of build files to ignore. if you wish to scan all build files in your repo keep this empty.
    },
    "register": {
      "name": "android-register",
      "path": "/Users/phundal/Development/android-register",
      "allowlistClosuresPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/allowlist-closures.txt",
      "ignoreBuildsPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/ignore-builds.txt"
    }
  }
}
```

This configuration defines two different projects this app can support. 
Note that all paths must be the absolute file path to the locations.
The attributes in the configuration are **required**. 

- name: The name of the project. This is self defined
- path: The full file path to the repository for this project
- allowlistClosuresPathAsString: File path to the allow list of closures. This will find violations against all closures that are not "allowed". If none are allowed, leave this emtpy.
- ignoreBuildsPathAsString: File path to the list of all build files you want to ignore. If you want to scan all build files for this project leave this empty.