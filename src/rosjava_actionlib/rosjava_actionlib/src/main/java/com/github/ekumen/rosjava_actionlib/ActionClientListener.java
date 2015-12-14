package com.github.ekumen.rosjava_actionlib;

import org.ros.internal.message.Message;
import actionlib_msgs.GoalStatusArray;


/**
 * Listener interface to receive the incoming messages from the ActionLib server.
 * A client should implement this interface if it wants to receive the callbacks
 * with information from the server.
 */
public interface ActionClientListener<T_ACTION_FEEDBACK extends Message, T_ACTION_RESULT extends Message> {
  void resultReceived(T_ACTION_RESULT result);
  void feedbackReceived(T_ACTION_FEEDBACK feedback);
  void statusReceived(GoalStatusArray status);
}
