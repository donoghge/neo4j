/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.cypher.internal.ast.factory.neo4j

import org.opencypher.tools.tck.api.CypherTCK
import org.opencypher.tools.tck.api.Execute
import org.opencypher.tools.tck.api.Scenario
import org.scalatest.FunSpecLike

class Neo4jASTFactoryTCKTest extends ParsingTestBase with FunSpecLike {

  val scenariosPerFeature: Map[String, Seq[Scenario]] =
    CypherTCK.allTckScenarios.foldLeft(Map.empty[String, Seq[Scenario]]) {
      case (acc, scenario: Scenario) =>
        val soFar: Seq[Scenario] = acc.getOrElse(scenario.featureName, Seq.empty[Scenario])
        acc + (scenario.featureName -> (soFar :+ scenario))
    }
  var x = 0

  val BLACKLIST = Set(
      "Fail when asterisk operator is missing",
      "Fail on negative bound",
      "In-query call to procedure that takes no arguments and yields no results",
      "In-query call to procedure with explicit arguments that drops all result fields",
      "Failing on incorrect unicode literal",
      "Fail for invalid Unicode hyphen in subtraction",
      "Failing when creating relationship with more than one type",
      "Failing when merging relationship with more than one type",
      "Supplying invalid hexadecimal literal 1",
      "Supplying invalid hexadecimal literal 2",
      "Using ON CREATE and ON MATCH",
      "Matching using relationship predicate with multiples of the same type")

  scenariosPerFeature foreach {
    case (featureName, scenarios) =>
      describe(featureName) {
        scenarios
          .filter(scenarioObj => !BLACKLIST(scenarioObj.name))
          .foreach {
            scenarioObj =>
              describe(scenarioObj.name) {
                scenarioObj.steps foreach {
                  case Execute(query, _, _) =>
                    x = x + 1
                    it(query + x) {
                      assertSameAST(query)
                    }
                  case _ =>
                }
              }
        }
      }
    case _ =>
  }
}

