package com.github.ekumen.rosjava_actionlib;

import org.ros.internal.message.Message;

/**
 * Listener interface to receive the incoming messages from the ActionLib server.
 */
public interface ActionClientListener {
  void resultReceived(Message result);
  void feedbackReceived(Message feedback);
  void statusReceived(Message status);
}
