/**
 * Copyright 2015 Ekumen www.ekumenlabs.com
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

package com.github.ekumen.rosjava_actionlib;

import actionlib_msgs.GoalID;
import actionlib_msgs.GoalStatus;

/**
 * Class that binds and action goal with a state machine to track its state.
 * @author Ernesto Corbellini ecorbellini@ekumenlabs.com
 */
public class ClientGoalManager<T_ACTION_GOAL extends Message> {
  public ActionGoal<T_ACTION_GOAL> actionGoal = null;
  public ClientStateMachine stateMachine = null;

  public void sendGoal(ActionGoal<T_ACTION_GOAL> ag) {
    actionGoal = ag;
    stateMachine = new ClientStateMachine();
    stateMachine.setState(ClientStateMachine.ClientStates.WAITING_FOR_GOAL_ACK);
  }

  public boolean cancelGoal() {
    return stateMachine.cancelGoal();
  }
}
