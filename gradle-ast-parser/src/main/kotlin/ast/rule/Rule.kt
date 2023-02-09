package ast.rule

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

import ast.violation.Violation
import ast.visitor.Visitor
import java.nio.file.Path

internal interface Rule {
  fun enforce(buildFile: Path, visitor: Visitor): Set<Violation>
}

/** Marker interface specific to closures, to future-proof this multiple rules need to be applied
 * to closures
 */
internal interface ClosureRule : Rule

/** Marker interface specific to dependencies, to future-proof this multiple rules need to be applied
 * to dependencies
 */
internal interface DependencyRule : Rule
