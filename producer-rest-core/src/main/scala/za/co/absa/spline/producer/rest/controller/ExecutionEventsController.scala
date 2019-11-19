/*
 * Copyright 2019 ABSA Group Limited
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

package za.co.absa.spline.producer.rest.controller

import io.swagger.annotations.{Api, ApiOperation, ApiResponse, ApiResponses}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import za.co.absa.spline.producer.rest.model.ExecutionEvent
import za.co.absa.spline.producer.service.repo.ExecutionProducerRepository

import scala.concurrent.{ExecutionContext, Future}

@Controller
@Api(tags = Array("execution"))
class ExecutionEventsController @Autowired()(
  val repo: ExecutionProducerRepository) {

  import ExecutionContext.Implicits.global

  @PostMapping(Array("/execution-events"))
  @ApiOperation(
    value = "Save Execution Events",
    notes =
      """
        Saves a list of Execution Events.

        RequestBody format :

        Array[ExecutionEvent]
        [
          // Reference to the Executionplan Id that was triggered
          planId: UUID
          // Timestamp of the execution
          timestamp: Long
          // If an error occurred during the execution
          error: Option[Any]
          // Any other extra information related to the execution Event Like the application Id for instance
          extra: Map[String, Any]
        ]
      """)
  @ApiResponses(Array(
    new ApiResponse(code = 201, message = "All execution Events are successfully stored")
  ))
  @ResponseStatus(HttpStatus.CREATED)
  def executionEvent(@RequestBody execEvents: Array[ExecutionEvent]): Future[Unit] = {
    repo.insertExecutionEvents(execEvents)
  }

}
