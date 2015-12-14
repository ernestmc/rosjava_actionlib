package com.github.ekumen.rosjava_actionlib;

import org.ros.internal.message.Message;
import actionlib_msgs.GoalID;


/**
 * Listener interface to receive the incoming messages from the ActionLib client.
 * A server should implement this interface if it wants to receive the callbacks
 * with information from the client.
 */
public interface ActionServerListener<T_ACTION_GOAL extends Message> {
  void goalReceived(T_ACTION_GOAL goal);
  void cancelReceived(GoalID id);
}
