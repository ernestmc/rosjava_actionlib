package com.github.ekumen.rosjava_actionlib;

import org.ros.internal.message.Message;
import actionlib_msgs.GoalID;


/**
 * Listener interface to receive the incoming messages from the ActionLib client.
 * A server should implement this interface if it wants to receive the callbacks
 * with information from the client.
 */
public interface ActionServerListener<T_ACTION_GOAL extends Message> {
  /**
   * This callback is called when a message is received on the goal topic.
   * Note: this method is called right after the server starts tracking this
   * goal and is intended for informative purposes.
   * @param goal the action goal received.
   */
  void goalReceived(T_ACTION_GOAL goal);

  /**
   * This callback is called when a message is received on the cancel topic.
   * @param id Goal ID object that was received.
   */
  void cancelReceived(GoalID id);

  /**
   * Callback method to accept a recently received action goal.
   * @param goal The action goal received.
   * @return The implementer must return true if he accepts the goal or false
   * otherwise.
   */
  boolean acceptGoal(T_ACTION_GOAL goal);
}
