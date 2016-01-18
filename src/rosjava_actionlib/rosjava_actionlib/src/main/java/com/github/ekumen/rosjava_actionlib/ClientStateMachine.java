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

import java.lang.Exception;
import actionlib_msgs.GoalStatus;
import java.util.Vector;
import java.util.Iterator;


/**
 * State machine for the action client.
 * @author Ernesto Corbellini ecorbellini@ekumenlabs.com
  */
public class ClientStateMachine {
  // Local class to hold the states
  public static class ClientStates {
    public final static int INVALID_TRANSITION = -2;
    public final static int NO_TRANSITION = -1;
    public final static int WAITING_FOR_GOAL_ACK = 0;
    public final static int PENDING = 1;
    public final static int ACTIVE = 2;
    public final static int WAITING_FOR_RESULT = 3;
    public final static int WAITING_FOR_CANCEL_ACK = 4;
    public final static int RECALLING = 5;
    public final static int PREEMPTING = 6;
    public final static int DONE = 7;
    public final static int LOST = 8;
  }

  int latestGoalStatus;
  int state;
  int nextState;


  /**
   * Constructor
   */
  public void ClientStateMachine()
  {
    // interface object for the callbacks?
    // store arguments locally in the object
    //this.goal = actionGoal;
  }

  public synchronized void setState(int state) {
    this.state = state;
  }

  public synchronized int getState() {
    return this.state;
  }

  /**
   * Update the state of the client based on the current state and the goal state.
   */
  public synchronized void updateStatus(int status)
  {
    if (this.state != ClientStates.DONE)
    {
      /*status = this.goal.findStatus(statusArray); //_find_status_by_goal_id(statusArray, self.action_goal.goal_id.id);

      // we haven't heard of the goal?
      if (status == 0)
      {
        if (this.state != ClientStates.WAITING_FOR_GOAL_ACK && this.state != ClientStates.WAITING_FOR_RESULT && this.state != ClientStates.DONE)
        {
          markAsLost();
        }
        return;
      }*/

      this.latestGoalStatus = status;

      // Determine the next state

      //if (this.state

    }

  }

  /**
   * Update the state of the client upon the received status of the goal.
   * @param goalStatus Status of the goal.
   */
  public synchronized void transition(int goalStatus)
  {
    Vector<Integer> nextStates;
    Iterator<Integer> iterStates;

    // transition to next states
    nextStates = getTransition(goalStatus);
    iterStates = nextStates.iterator();

    while (iterStates.hasNext()) {
      this.state = iterStates.next();
    }
  }

  /**
   * Get the next state transition depending on the current client state and the
   * goal state.
   * @param goalStatus The current status of the tracked goal.
   * @return A vector with the list of next states. The states should be
   * transitioned in order. This is necessary because if we loose a state update
   * we might still be able to infer the actual transition history that took us
   * to the final goal state.
   */
  public Vector<Integer> getTransition(int goalStatus)
  {
    Vector<Integer> stateList = new Vector<Integer>();

    switch (this.state)
    {
      case ClientStates.WAITING_FOR_GOAL_ACK:
        switch (goalStatus)
        {
          case actionlib_msgs.GoalStatus.PENDING:
            stateList.add(ClientStates.PENDING);
            break;
          case actionlib_msgs.GoalStatus.ACTIVE:
            stateList.add(ClientStates.ACTIVE);
            break;
          case actionlib_msgs.GoalStatus.REJECTED:
            stateList.add(ClientStates.PENDING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.RECALLING:
            stateList.add(ClientStates.PENDING);
            stateList.add(ClientStates.RECALLING);
            break;
          case actionlib_msgs.GoalStatus.RECALLED:
            stateList.add(ClientStates.PENDING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.SUCCEEDED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.ABORTED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTING:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.PENDING:
        switch (goalStatus)
        {
          case actionlib_msgs.GoalStatus.PENDING:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.ACTIVE:
            stateList.add(ClientStates.ACTIVE);
            break;
          case actionlib_msgs.GoalStatus.REJECTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.RECALLING:
            stateList.add(ClientStates.RECALLING);
            break;
          case actionlib_msgs.GoalStatus.RECALLED:
            stateList.add(ClientStates.RECALLING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.SUCCEEDED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.ABORTED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTING:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.ACTIVE:
        switch (goalStatus)
        {
          case actionlib_msgs.GoalStatus.PENDING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.ACTIVE:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.REJECTED:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.RECALLING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.RECALLED:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.SUCCEEDED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.ABORTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTING:
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.WAITING_FOR_RESULT:
        switch (goalStatus)
        {
          case actionlib_msgs.GoalStatus.PENDING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.ACTIVE:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.REJECTED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.RECALLING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.RECALLED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.PREEMPTED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.SUCCEEDED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.ABORTED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.PREEMPTING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
        }
        break;
      case ClientStates.WAITING_FOR_CANCEL_ACK:
        switch (goalStatus)
        {
          case actionlib_msgs.GoalStatus.PENDING:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.ACTIVE:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.REJECTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.RECALLING:
            stateList.add(ClientStates.RECALLING);
            break;
          case actionlib_msgs.GoalStatus.RECALLED:
            stateList.add(ClientStates.RECALLING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.SUCCEEDED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.ABORTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTING:
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.RECALLING:
        switch (goalStatus)
        {
          case actionlib_msgs.GoalStatus.PENDING:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.ACTIVE:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.REJECTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.RECALLING:
            stateList.add(ClientStates.RECALLING);
            break;
          case actionlib_msgs.GoalStatus.RECALLED:
            stateList.add(ClientStates.RECALLING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.SUCCEEDED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.ABORTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTING:
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.PREEMPTING:
        switch (goalStatus)
        {
          case actionlib_msgs.GoalStatus.PENDING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.ACTIVE:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.REJECTED:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.RECALLING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.RECALLED:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.SUCCEEDED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.ABORTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case actionlib_msgs.GoalStatus.PREEMPTING:
            // no transition
            break;
        }
        break;
      case ClientStates.DONE:
        switch (goalStatus)
        {
          case actionlib_msgs.GoalStatus.PENDING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.ACTIVE:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.REJECTED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.RECALLING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case actionlib_msgs.GoalStatus.RECALLED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.PREEMPTED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.SUCCEEDED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.ABORTED:
            // no transition
            break;
          case actionlib_msgs.GoalStatus.PREEMPTING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
        }
        break;
    }
    return stateList;
  }

  /**
   * Cancel action goal. The goal can only be cancelled if its in certain
   * states. If it can be cancelled the state will be changed to
   * WAITING_FOR_CANCEL_ACK.
   * @return True if the goal can be cancelled, false otherwise.
   */
  public boolean cancel() {
    bolean ret = false;
    switch (stateMachine.getState()) {
      case ClientStates.WAITING_FOR_GOAL_ACK:
      case ClientStates.PENDING:
      case ClientStates.ACTIVE:
        this.state = ClientStates.WAITING_FOR_CANCEL_ACK;
        ret = true;
        break;
    }
    return ret;
  }

  public void markAsLost()
  {
  }

  public void updateResult(int statusResult)
  {
  }

}
