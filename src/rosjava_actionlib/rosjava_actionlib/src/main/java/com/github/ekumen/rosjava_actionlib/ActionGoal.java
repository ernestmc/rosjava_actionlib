
package com.github.ekumen.rosjava_actionlib;

public class ActionGoal
{
  public static class GoalStates
  {
    public final static int PENDING = 0;  // The goal has yet to be processed by the action server
    public final static int ACTIVE = 1;  // The goal is currently being processed by the action server
    public final static int REJECTED = 2;  // The goal was rejected by the action server without being processed and without a request from the action client to cancel
    public final static int SUCCEEDED = 3;  // The goal was achieved successfully by the action server
    public final static int ABORTED = 4;  // The goal was aborted by the action server
    public final static int PREEMPTING = 5;  // Processing of the goal was canceled by either another goal, or a cancel request sent to the action server
    public final static int PREEMPTED = 6;  // The goal was preempted by either another goal, or a preempt message being sent to the action server
    public final static int RECALLING = 7;  // The goal has not been processed and a cancel request has been received from the action client, but the action server has not confirmed the goal is canceled
    public final static int RECALLED = 8; // The goal was canceled by either another goal, or a cancel request before the action server began processing the goal
    public final static int LOST = 9;  // The goal was sent
  }

  int findStatus(int statusArray)
  {
    return 0;
  }
}
