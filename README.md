# Gradle AST Parser

## Table of Contents
- [What is an *AST*?](#what-is-an-ast)
- [What are Gradle Build Files?](#what-are-gradle-build-files)
- [What is the relationship between an AST and a Build file?](#what-is-the-relationship-between-an-ast-and-a-build-file)
- [Introduction](#introduction)
- [Getting Started](#getting-started)
- [Violations](#violations)
- [Limitations](#limitations)
- [Future Work](#future-work)

### What is an *AST*?

An [Abstract Syntax Tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree) is a data structure used to represent the structure of source
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
      "disallowedDependenciesPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/disallowed-dependencies.txt",
      "allowlistClosuresPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/allowlist-closures.txt",
      "ignoreBuildsPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/ignore-builds.txt"
    },
```

**Read the section on getting started for how to use this configuration**

## Getting Started

Gradle AST Parser is a Kotlin application that can be run by invoking gradle. 
This app uses PicoCli to make it easy to interact with the app from the command line.

* [Clone Repository](https://github.com/paulhundal/gradle-build-parser) 
* [Go to gradle-ast-parser root](https://github.com/paulhundal/gradle-build-parser/gradle-ast-parser)
* Open the file [project-catalog.json]("https://github.com/paulhundal/gradle-build-parser/blob/master/gradle-ast-parser/binary/project-catalog.json")

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
      "disallowedDependenciesPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/disallowed-dependencies.txt",
      "allowlistClosuresPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/allowlist-closures.txt", // path to list of allowed closures. by default all are allowed. you can leave this empty if desired.
      "ignoreBuildsPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/ignore-builds.txt" // path to list of build files to ignore. if you wish to scan all build files in your repo keep this empty.
    },
    "register": {
      "name": "android-register",
      "path": "/Users/phundal/Development/android-register",
      "disallowedDependenciesPathAsString": "/Users/phundal/Development/gradle-build-parser/gradle-ast-parser/binary/disallowed-dependencies.txt",
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
- disallowedDependenciesPathAsString: File path to the dependencies that are not allowed. This will find violation against any declared dependency in the list. If all dependencies are allowed, leave this empty.
- allowlistClosuresPathAsString: File path to the allow list of closures. This will find violations against all closures that are not "allowed". If none are allowed, leave this emtpy.
- ignoreBuildsPathAsString: File path to the list of all build files you want to ignore. If you want to scan all build files for this project leave this empty.


## Setup

The setup command will bootstrap the environment in which the violation files, amongst other configuration, is stored.
This command should be run each time you update the project configuration file OR when switching to work on another project.

```shell
./gradlew run --args="setup -p<full-path-to-project>"
```

This will create a directory at `~HOME/.ast/<project-name>` in which all configurations are stored.
You will need this directory to see output of violations and other output from this tool.

The logger for this tool should direct you to this, but in case it gets lost refer to this as the source of truth.

## Violations

Violations are rules that build.gradle files do not adhere to, but are expected to.

At the time of writing, this tool supports the following Rules:

- Unsupported Closure Rule: To allow list closures, update the allowlist-closures.txt file. Any closure that isn't listed will create a violation.
- Unsupported Dependency Rule: To deny certain dependency declarations, update disallowed-dependencies.txt. Any dependency not declared here will be considered acceptable.


**Output of Violations**

As mentioned in the Setup section, `~HOME/.ast/<project-name>` should contain a directory for your project.
This directory will contain a file named `violations.txt` once the violations are found. If no violations are found, you can expect nothing to be written.

Example output of violation for an unsupported dependency:

```shell
You declared implementation(org.slf4j:slf4j-simple:1.7.10) at line 22 in build file /Users/phundal/Development/gradle-build-parser/gradle-ast-parser/build.gradle. This dependency is not supported. Please replace or remove.
```

To find all violations for your project:

```shell
./gradlew run --args="violations"
```

### Limitations

As mentioned, this project is only meant for build.gradle files that are written in groovy.
At this time, the Kotlin compiler does not generate an AST that has the same structure
as the one produced by Groovy. 


### Future Work

This is just the start of what AST parsing can do. I expect this project to include

- Showing test vs prod dependencies
- Finding conflicting versions of dependency declarations
- Be able to append/transform the AST

And so much more! Please contribute! :)