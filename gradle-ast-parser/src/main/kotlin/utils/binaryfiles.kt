package utils

/**
 * Copyright 2022 Square Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


const val IGNORE_BUILDS = "ignore-builds.txt" // Which build files to ignore in project
const val ALLOWLIST_CLOSURES = "allowlist-closures.txt" // Closures that are allowed
const val DISALLOWED_DEPENDENCIES = "disallowed-dependencies.txt" // Dependencies that are not allowed
const val PROJECT_CATALOG = "project-catalog.json" // Project configuration
const val BUILD_FILES = "build-files.txt" // Where list of build files are cached for each project
const val VIOLATIONS = "violations.txt" // Where all violations are output to