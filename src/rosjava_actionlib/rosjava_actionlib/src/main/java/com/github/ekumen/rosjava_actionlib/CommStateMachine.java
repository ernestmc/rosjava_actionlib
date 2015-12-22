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


import java.util.Vector;
import java.util.Iterator;


/**
 * State machine for the action client.
 * @author Ernesto Corbellini <ecorbellini@ekumenlabs.com>
 * Comments:
 *   - The state returned on a transition is actually a vector of states that should be transitioned in sequence.
 *  TODO: change class name to ClientStateMachine
 */
public class CommStateMachine {
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

  ActionGoal goal;
  int latestGoalStatus;
  int state;
  int nextState;


  /**
   * Constructor
   */
  //public void CommStateMachine(ActionGoal actionGoal, transition_callback?, feedback_callback?, send_goal_function, send_cancel_function)
  //public void CommStateMachine(ActionGoal actionGoal, transition_callback?, feedback_callback?, ActionGoalTrigger)
  public void CommStateMachine(ActionGoal actionGoal)
  {
    // interface object for the callbacks?
    // store arguments locally in the object
    this.goal = actionGoal;
  }

  /*
   * Compare two objects.
   */
  public boolean equals(CommStateMachine obj) {
    //return actionGoal.goalId.id == obj.actionGoal.goalId.id;
    return true;
  }

  public void setState(int state) {
    this.state = state;
  }

  public int getState() {
    return this.state;
  }

  /**
   * Update the state of the client based on the current state and the goal state.
   * Note: This method uses a mutex in the original implementation so we make it synchronized.
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

  public void transitionTo(int toState)
  {
    Vector nextStates;
    Iterator iterStates;
    int state;

    // transition to next states
    nextStates = getTransition(ActionGoal.GoalStates.ACTIVE);
    iterStates = nextStates.iterator();

    while (iterStates.hasNext()) {
      state = (int)iterStates.next();
    }
  }

  /**
   * Get the next state transition depending on the current client state and the goal state.
   * Note: this replaces the transition lookup table from the original implementation.
   */
  public Vector getTransition(int goalStatus)
  {
    Vector stateList = new Vector();

    switch (this.state)
    {
      case ClientStates.WAITING_FOR_GOAL_ACK:
        switch (goalStatus)
        {
          case ActionGoal.GoalStates.PENDING:
            stateList.add(ClientStates.PENDING);
            break;
          case ActionGoal.GoalStates.ACTIVE:
            stateList.add(ClientStates.ACTIVE);
            break;
          case ActionGoal.GoalStates.REJECTED:
            stateList.add(ClientStates.PENDING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.RECALLING:
            stateList.add(ClientStates.PENDING);
            stateList.add(ClientStates.RECALLING);
            break;
          case ActionGoal.GoalStates.RECALLED:
            stateList.add(ClientStates.PENDING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.SUCCEEDED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.ABORTED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTING:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.PENDING:
        switch (goalStatus)
        {
          case ActionGoal.GoalStates.PENDING:
            // no transition
            break;
          case ActionGoal.GoalStates.ACTIVE:
            stateList.add(ClientStates.ACTIVE);
            break;
          case ActionGoal.GoalStates.REJECTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.RECALLING:
            stateList.add(ClientStates.RECALLING);
            break;
          case ActionGoal.GoalStates.RECALLED:
            stateList.add(ClientStates.RECALLING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.SUCCEEDED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.ABORTED:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTING:
            stateList.add(ClientStates.ACTIVE);
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.ACTIVE:
        switch (goalStatus)
        {
          case ActionGoal.GoalStates.PENDING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.ACTIVE:
            // no transition
            break;
          case ActionGoal.GoalStates.REJECTED:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.RECALLING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.RECALLED:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.PREEMPTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.SUCCEEDED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.ABORTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTING:
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.WAITING_FOR_RESULT:
        switch (goalStatus)
        {
          case ActionGoal.GoalStates.PENDING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.ACTIVE:
            // no transition
            break;
          case ActionGoal.GoalStates.REJECTED:
            // no transition
            break;
          case ActionGoal.GoalStates.RECALLING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.RECALLED:
            // no transition
            break;
          case ActionGoal.GoalStates.PREEMPTED:
            // no transition
            break;
          case ActionGoal.GoalStates.SUCCEEDED:
            // no transition
            break;
          case ActionGoal.GoalStates.ABORTED:
            // no transition
            break;
          case ActionGoal.GoalStates.PREEMPTING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
        }
        break;
      case ClientStates.WAITING_FOR_CANCEL_ACK:
        switch (goalStatus)
        {
          case ActionGoal.GoalStates.PENDING:
            // no transition
            break;
          case ActionGoal.GoalStates.ACTIVE:
            // no transition
            break;
          case ActionGoal.GoalStates.REJECTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.RECALLING:
            stateList.add(ClientStates.RECALLING);
            break;
          case ActionGoal.GoalStates.RECALLED:
            stateList.add(ClientStates.RECALLING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.SUCCEEDED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.ABORTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTING:
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.RECALLING:
        switch (goalStatus)
        {
          case ActionGoal.GoalStates.PENDING:
            // no transition
            break;
          case ActionGoal.GoalStates.ACTIVE:
            // no transition
            break;
          case ActionGoal.GoalStates.REJECTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.RECALLING:
            stateList.add(ClientStates.RECALLING);
            break;
          case ActionGoal.GoalStates.RECALLED:
            stateList.add(ClientStates.RECALLING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.SUCCEEDED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.ABORTED:
            stateList.add(ClientStates.PREEMPTING);
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTING:
            stateList.add(ClientStates.PREEMPTING);
            break;
        }
        break;
      case ClientStates.PREEMPTING:
        switch (goalStatus)
        {
          case ActionGoal.GoalStates.PENDING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.ACTIVE:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.REJECTED:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.RECALLING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.RECALLED:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.PREEMPTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.SUCCEEDED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.ABORTED:
            stateList.add(ClientStates.WAITING_FOR_RESULT);
            break;
          case ActionGoal.GoalStates.PREEMPTING:
            // no transition
            break;
        }
        break;
      case ClientStates.DONE:
        switch (goalStatus)
        {
          case ActionGoal.GoalStates.PENDING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.ACTIVE:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.REJECTED:
            // no transition
            break;
          case ActionGoal.GoalStates.RECALLING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
          case ActionGoal.GoalStates.RECALLED:
            // no transition
            break;
          case ActionGoal.GoalStates.PREEMPTED:
            // no transition
            break;
          case ActionGoal.GoalStates.SUCCEEDED:
            // no transition
            break;
          case ActionGoal.GoalStates.ABORTED:
            // no transition
            break;
          case ActionGoal.GoalStates.PREEMPTING:
            stateList.add(ClientStates.INVALID_TRANSITION);
            break;
        }
        break;
    }
    return stateList;
  }

  public void markAsLost()
  {
  }

  public void updateResult(int statusResult)
  {
  }

}
