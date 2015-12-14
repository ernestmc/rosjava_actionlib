
package com.github.ekumen.rosjava_actionlib;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.Publisher;
import org.ros.message.MessageListener;
import org.ros.internal.message.Message;
import java.util.concurrent.TimeUnit;
import actionlib_msgs.GoalStatusArray;
import actionlib_msgs.GoalID;

public class ActionClient<T_ACTION_GOAL extends Message,
  T_ACTION_FEEDBACK extends Message,
  T_ACTION_RESULT extends Message> {

  T_ACTION_GOAL actionGoal;
  String actionGoalType;
  String actionResultType;
  String actionFeedbackType;
  Publisher<T_ACTION_GOAL> goalPublisher = null;
  Publisher<GoalID> cancelPublisher = null;
  Subscriber<T_ACTION_RESULT> serverResult = null;
  Subscriber<T_ACTION_FEEDBACK> serverFeedback = null;
  Subscriber<GoalStatusArray> serverStatus = null;
  ConnectedNode node = null;
  String actionName;
  ActionClientListener callbackTarget = null;

  ActionClient (ConnectedNode node, String actionName, String actionGoalType,
    String actionFeedbackType, String actionResultType) {
    this.node = node;
    this.actionName = actionName;
    this.actionGoalType = actionGoalType;
    this.actionFeedbackType = actionFeedbackType;
    this.actionResultType = actionResultType;

    connect(node);
  }

  public void attachListener(ActionClientListener target) {
    callbackTarget = target;
  }

  public void sendGoal(T_ACTION_GOAL goal) {
    goalPublisher.publish(goal);
  }

  public void sendCancel(GoalID id) {
    cancelPublisher.publish(id);
  }

  private void publishClient(ConnectedNode node) {
    goalPublisher = node.newPublisher(actionName + "/goal", actionGoalType);
    cancelPublisher = node.newPublisher(actionName + "/cancel", GoalID._TYPE);
  }

  private void unpublishClient() {
    if (goalPublisher != null) {
      goalPublisher.shutdown(5, TimeUnit.SECONDS);
      goalPublisher = null;
    }
    if (cancelPublisher != null) {
      cancelPublisher.shutdown(5, TimeUnit.SECONDS);
      cancelPublisher = null;
    }
  }

  public T_ACTION_GOAL newGoalMessage() {
    return goalPublisher.newMessage();
  }

  private void subscribeToServer(ConnectedNode node) {
    serverResult = node.newSubscriber(actionName + "/result", actionResultType);
    serverFeedback = node.newSubscriber(actionName + "/feedback", actionFeedbackType);
    serverStatus = node.newSubscriber(actionName + "/status", GoalStatusArray._TYPE);

    serverFeedback.addMessageListener(new MessageListener<T_ACTION_FEEDBACK>() {
      @Override
      public void onNewMessage(T_ACTION_FEEDBACK message) {
        gotFeedback(message);
      }
    });

    serverResult.addMessageListener(new MessageListener<T_ACTION_RESULT>() {
      @Override
      public void onNewMessage(T_ACTION_RESULT message) {
        gotResult(message);
      }
    });

    serverStatus.addMessageListener(new MessageListener<GoalStatusArray>() {
      @Override
      public void onNewMessage(GoalStatusArray message) {
        gotStatus(message);
      }
    });
  }

  private void unsubscribeToServer() {
    if (serverFeedback != null) {
      serverFeedback.shutdown(5, TimeUnit.SECONDS);
      serverFeedback = null;
    }
    if (serverResult != null) {
      serverResult.shutdown(5, TimeUnit.SECONDS);
      serverResult = null;
    }
    if (serverStatus != null) {
      serverStatus.shutdown(5, TimeUnit.SECONDS);
      serverStatus = null;
    }
  }

  public void gotResult(T_ACTION_RESULT message) {
    // Propagate the callback
    if (callbackTarget != null) {
      callbackTarget.resultReceived(message);
    }
  }

  public void gotFeedback(T_ACTION_FEEDBACK message) {
    // Propagate the callback
    if (callbackTarget != null) {
      callbackTarget.feedbackReceived(message);
    }
  }

  public void gotStatus(GoalStatusArray message) {
    // Propagate the callback
    if (callbackTarget != null) {
      callbackTarget.statusReceived(message);
    }
  }

  /**
  * Publishes the client's topics and suscribes to the server's topics.
  */
  private void connect(ConnectedNode node) {
    publishClient(node);
    subscribeToServer(node);
  }

  /**
   * Finish the action client. Unregister publishers and listeners.
   */
  public void finish() {
    callbackTarget = null;
    unpublishClient();
    unsubscribeToServer();
  }

  protected void finalize() {
    finish();
  }
}
