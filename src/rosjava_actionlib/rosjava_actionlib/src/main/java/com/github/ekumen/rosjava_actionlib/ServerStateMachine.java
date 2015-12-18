/**
 * Copyright (c) 2015, Ernesto Corbellini, Ekumen
 *
 * Pending license.
 *
 */


package com.github.ekumen.rosjava_actionlib;

import java.lang.Exception;
import actionlib_msgs.GoalStatus;

/*
 * Class to manage the server state machine transitions.
 * @author Ernesto Corbellini
 */
public class ServerStateMachine {
  public static class Events {
    public final static int CANCEL_REQUEST = 1;
    public final static int CANCEL = 2;
    public final static int REJECT = 3;
    public final static int ACCEPT = 4;
    public final static int SUCCEED = 5;
    public final static int ABORT = 6;
  }

  private int state;

  ServerStateMachine() {
    // Initial state
    state = GoalStatus.PENDING;
  }

  public synchronized int getState() {
    return state;
  }

  public synchronized void setState(int s) {
    state = s;
  }

  public synchronized int transition(int event) throws Exception {
    int nextState = state;

    switch (state) {
      case GoalStatus.PENDING:
        switch (event) {
          case Events.REJECT:
            nextState = GoalStatus.REJECTED;
            break;
          case Events.CANCEL_REQUEST:
            nextState = GoalStatus.RECALLING;
            break;
          case Events.ACCEPT:
            nextState = GoalStatus.ACTIVE;
            break;
          default:
            throw new Exception("Actionlib server exception: Invalid transition event!");
        }
        break;
      case GoalStatus.RECALLING:
        switch (event) {
          case Events.REJECT:
            nextState = GoalStatus.REJECTED;
            break;
          case Events.CANCEL:
            nextState = GoalStatus.RECALLED;
            break;
          case Events.ACCEPT:
            nextState = GoalStatus.PREEMPTING;
            break;
          default:
            throw new Exception("Actionlib server exception: Invalid transition event!");
        }
        break;
      case GoalStatus.ACTIVE:
        switch (event) {
          case Events.SUCCEED:
            nextState = GoalStatus.SUCCEEDED;
            break;
          case Events.CANCEL_REQUEST:
            nextState = GoalStatus.PREEMPTING;
            break;
          case Events.ABORT:
            nextState = GoalStatus.ABORTED;
            break;
          default:
            throw new Exception("Actionlib server exception: Invalid transition event!");
        }
        break;
      case GoalStatus.PREEMPTING:
        switch (event) {
          case Events.SUCCEED:
            nextState = GoalStatus.SUCCEEDED;
            break;
          case Events.CANCEL:
            nextState = GoalStatus.PREEMPTED;
            break;
          case Events.ABORT:
            nextState = GoalStatus.ABORTED;
            break;
          default:
            throw new Exception("Actionlib server exception: Invalid transition event!");
        }
        break;
      case GoalStatus.REJECTED:
        break;
      case GoalStatus.RECALLED:
        break;
      case GoalStatus.PREEMPTED:
        break;
      case GoalStatus.SUCCEEDED:
        break;
      case GoalStatus.ABORTED:
        break;
      default:
        throw new Exception("Actionlib server exception: Invalid state!");
    }
    // transition to the next state
    state = nextState;
    return nextState;
  }
}
