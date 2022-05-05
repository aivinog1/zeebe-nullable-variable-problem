/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
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
package io.zeebe;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.test.ZeebeTestRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.Duration;
import java.util.Map;

public class ProcessTest {

  @Rule public ZeebeTestRule testRule = new ZeebeTestRule();

  private ZeebeClient client;

  @Before
  public void deploy() {
    client = testRule.getClient();

    client.newDeployCommand().addResourceFromClasspath("process.bpmn").requestTimeout(Duration.ofSeconds(20)).send().join();
  }

  @Test
  public void shouldCompleteWithNonnullVariable() {
    final ProcessInstanceEvent processInstance =
            client
                    .newCreateInstanceCommand()
                    .bpmnProcessId("process-with-nullable-variable")
                    .latestVersion()
                    .variables(Map.of("var", "NONNULL"))
                    .requestTimeout(Duration.ofSeconds(20))
                    .send().join();

    ZeebeTestRule.assertThat(processInstance).isEnded().hasPassed("start", "variable_gateway", "end_nonnull");
  }

  @Test
  public void shouldCompleteWithNullableVariable() {
    final ProcessInstanceEvent processInstance =
            client
                    .newCreateInstanceCommand()
                    .bpmnProcessId("process-with-nullable-variable")
                    .latestVersion()
                    .variables(Map.of("var", "NULLABLE"))
                    .requestTimeout(Duration.ofSeconds(20))
                    .send().join();

    ZeebeTestRule.assertThat(processInstance).isEnded().hasPassed("start", "variable_gateway", "end_nullable");
  }

  @Test
  public void shouldCompleteWithNullVariable() {
    final ProcessInstanceEvent processInstance =
            client
                    .newCreateInstanceCommand()
                    .bpmnProcessId("process-with-nullable-variable")
                    .latestVersion()
                    .requestTimeout(Duration.ofSeconds(20))
                    .send().join();

    ZeebeTestRule.assertThat(processInstance).isEnded().hasPassed("start", "variable_gateway", "end_nullable");
  }

}
