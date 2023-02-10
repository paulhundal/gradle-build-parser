# Gradle AST Traverser (GAT)

## Table of Contents
- [What is an *AST*?](#what-is-an-ast)
- [What are Gradle Build Files?](#what-are-gradle-build-files)
- [What is the relationship between an AST and a Build file?](#what-is-the-relationship-between-an-ast-and-a-build-file)
- [Introduction](#introduction)
- [Usage](#usage)
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

**GAT** provides the capabilities to enforce rules on your build files without
relying on regex. The AST is built by the Groovy compiler (*this app does not support kotlin build files*)
and is parsed based on a project configuration you define. 

Here is a small example of a project configuration for running the parser on this repository.

```json
  "projects": {
    "ast-parser": {
      "name": "ast-parser",
      "path": "Development/gradle-build-parser/gradle-ast-parser"
    },
```

### Usage

#### CLI

```shell
./gradlew run --args="setup -p=Development/<your-repo>" // Setup config for the project

./gradlew run --args="violations" // Find ALL violations

./gradlew run --args="violations <NAME_OF_VIOLATION>" // Find a certain violation

./gradlew run --args="scan" // Output a scan of where to find important files
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
      "path": "Development/gradle-build-parser/gradle-ast-parser" // path to the repo
    },
    "register": {
      "name": "android-register",
      "path": "Development/android-register"
    }
  }
}
```

This configuration defines two different projects this app can support. 
Note that all paths must be the absolute file path to the locations.
The attributes in the configuration are **required**. 

- name: The name of the project. This is self defined
- path: The full file path to the repository for this project


## Setup

The setup command will bootstrap the environment in which the violation files, amongst other configuration, is stored.
This command should be run each time you update the project configuration file OR when switching to work on another project.

```shell
./gradlew run --args="setup -p=<full-path-to-project>"
```

This will create a directory at `~HOME/.ast/<project-name>` in which all configurations are stored.
You will need this directory to see output of violations and other output from this tool.

The logger for this tool should direct you to this, but in case it gets lost refer to this as the source of truth.

## Violations

Violations are rules that build.gradle files do not adhere to, but are expected to.

At the time of writing, this tool supports the following Rules:

- Unsupported Closure Rule: To allow list closures, update the allowlist-closures.txt file. Any closure that isn't listed will create a violation.
- Duplicate Closure Rule: Any closure block declared more than once per build.gradle file will be considered a violation.
- Duplicate Dependency Rules: Any dependency appearing more than once in a dependency block will be considered a violation.


**Output of Violations**

As mentioned in the Setup section, `~HOME/.ast/<project-name>` should contain a directory for your project.
This directory will contain a file named `violations.txt` once the violations are found. If no violations are found, you can expect nothing to be written.

Example output of violation for an unsupported dependency:

```shell
You declared implementation(org.slf4j:slf4j-simple:1.7.10) at line 22 in build file /Users/phundal/Development/gradle-build-parser/gradle-ast-parser/build.gradle. This dependency is not supported. Please replace or remove.
```

To find all violations for your project:

```shell
./gradlew run --args="violations" // check all violations
./gradlew run --args"violations <TYPE>" // check for specific violation
```

### Rules

Rules are set and defined within the binary of this project. To modify the rules for your own project, update the following files as needed:

```shell
/binary/allowlist-closures.txt
/binary/ignore-builds.txt
/binary/project-catalog.json
```

1. allowlist-closures.txt -> Defines the set of all closures that ARE allowed.
2. ignore-builds.txt -> Defines the set of all build files to ignore
3. project-catalog.json -> Defines the configuration of your project and where to read source from

Every rule that is broken becomes a [Violation]("https://github.com/paulhundal/gradle-build-parser/blob/master/gradle-ast-parser/src/main/kotlin/ast/violation/Violation.kt)

### Contributing

To add to this list of violations take the following steps:

1. Define a new violation type and place it in [IncludedViolation](https://github.com/paulhundal/gradle-build-parser/blob/master/gradle-ast-parser/src/main/kotlin/ast/violation/IncludedViolation.kt)
2. Create a [Rule](https://github.com/paulhundal/gradle-build-parser/blob/master/gradle-ast-parser/src/main/kotlin/ast/rule/Rule.kt) that enforces this violation.
3. Modify [ViolationsCommand](https://github.com/paulhundal/gradle-build-parser/blob/master/gradle-ast-parser/src/main/kotlin/commands/ViolationsCommand.kt) to include the new violation
4. **IF** the node data you need isn't readily available, create a [Visitor](https://github.com/paulhundal/gradle-build-parser/blob/master/gradle-ast-parser/src/main/kotlin/ast/visitor/Visitor.kt) and add it to the [VisitorFactory]("https://github.com/paulhundal/gradle-build-parser/blob/master/gradle-ast-parser/src/main/kotlin/ast/visitor/VisitorFactory.kt")
5. Create a PR/Issue with the suggested changes

### Limitations

As mentioned, this project is only meant for build.gradle files that are written in groovy.
At this time, the Kotlin compiler does not generate an AST that has the same structure
as the one produced by Groovy. 


### Future Work

This is just the start of what AST parsing can do!
As use cases arise this tool can help speed up build file investigations, modifications and analysis.


Please contribute! :)


### License

```shell
Copyright 2023 Square, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```