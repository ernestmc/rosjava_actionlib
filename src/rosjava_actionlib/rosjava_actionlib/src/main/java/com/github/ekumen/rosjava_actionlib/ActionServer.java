package com.github.ekumen.rosjava_actionlib;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.Publisher;
import org.ros.message.MessageListener;
import org.ros.internal.message.Message;
import java.util.concurrent.TimeUnit;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.Vector;
import java.lang.reflect.Method;
import actionlib_msgs.GoalStatusArray;
import actionlib_msgs.GoalID;
import actionlib_msgs.GoalStatus;

public class ActionServer<T_ACTION_GOAL extends Message,
  T_ACTION_FEEDBACK extends Message,
  T_ACTION_RESULT extends Message> {

  private class ServerGoal {
    T_ACTION_GOAL goal;
    ServerStateMachine state = new ServerStateMachine();

    ServerGoal(T_ACTION_GOAL g) {
      goal = g;
    }
  }

  private T_ACTION_GOAL actionGoal;
  private String actionGoalType;
  private String actionResultType;
  private String actionFeedbackType;
  private Subscriber<T_ACTION_GOAL> goalSuscriber = null;
  private Subscriber<GoalID> cancelSuscriber = null;

  private Publisher<T_ACTION_RESULT> resultPublisher = null;
  private Publisher<T_ACTION_FEEDBACK> feedbackPublisher = null;
  private Publisher<GoalStatusArray> statusPublisher = null;
  private ConnectedNode node = null;
  private String actionName;
  private ActionServerListener callbackTarget = null;
  private Timer statusTick = new Timer();
  private HashMap<String, ServerGoal> goalTracker = new  HashMap<String,
    ServerGoal>(1);

  ActionServer (ConnectedNode node, String actionName, String actionGoalType,
    String actionFeedbackType, String actionResultType) {
    this.node = node;
    this.actionName = actionName;
    this.actionGoalType = actionGoalType;
    this.actionFeedbackType = actionFeedbackType;
    this.actionResultType = actionResultType;

    connect(node);
  }

  public void attachListener(ActionServerListener target) {
    callbackTarget = target;
  }

  public void sendStatus(GoalStatusArray status) {
    statusPublisher.publish(status);
  }

  public void sendFeedback(T_ACTION_FEEDBACK feedback) {
    feedbackPublisher.publish(feedback);
  }

  public void sendResult(T_ACTION_RESULT result) {
    resultPublisher.publish(result);
  }

  private void publishServer(ConnectedNode node) {
    statusPublisher = node.newPublisher(actionName + "/status", GoalStatusArray._TYPE);
    feedbackPublisher = node.newPublisher(actionName + "/feedback", actionFeedbackType);
    resultPublisher = node.newPublisher(actionName + "/result", actionResultType);
    statusTick.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        sendStatusTick();
      }
    }, 2000, 1000);
  }

  private void unpublishServer() {
    if (statusPublisher != null) {
      statusPublisher.shutdown(5, TimeUnit.SECONDS);
      statusPublisher = null;
    }
    if (feedbackPublisher != null) {
      feedbackPublisher.shutdown(5, TimeUnit.SECONDS);
      feedbackPublisher = null;
    }
    if (resultPublisher != null) {
      resultPublisher.shutdown(5, TimeUnit.SECONDS);
      resultPublisher = null;
    }
  }

  /**
   * Subscribe to the action client's topics: goal and cancel.
   * @param node The ROS node connected to the master.
   */
  private void subscribeToClient(ConnectedNode node) {
    goalSuscriber = node.newSubscriber(actionName + "/goal", actionGoalType);
    cancelSuscriber = node.newSubscriber(actionName + "/cancel", GoalID._TYPE);

    goalSuscriber.addMessageListener(new MessageListener<T_ACTION_GOAL>() {
      @Override
      public void onNewMessage(T_ACTION_GOAL message) {
        gotGoal(message);
      }
    });

    cancelSuscriber.addMessageListener(new MessageListener<GoalID>() {
      @Override
      public void onNewMessage(GoalID message) {
        gotCancel(message);
      }
    });
  }

  /**
   * Unsubscribe from the client's topics.
   */
  private void unsubscribeToClient() {
    if (goalSuscriber != null) {
      goalSuscriber.shutdown(5, TimeUnit.SECONDS);
      goalSuscriber = null;
    }
    if (cancelSuscriber != null) {
      cancelSuscriber.shutdown(5, TimeUnit.SECONDS);
      cancelSuscriber = null;
    }
  }

  /**
   * Called when we get a message on the subscribed goal topic.
   */
  public void gotGoal(T_ACTION_GOAL goal) {
    boolean accepted =  false;
    String goalIdString = getGoalId(goal).getId();

    // start tracking this newly received goal
    goalTracker.put(goalIdString, new ServerGoal(goal));
    // Propagate the callback
    if (callbackTarget != null) {
      // inform the user of a received message
      callbackTarget.goalReceived(goal);
      // ask the user to accept the goal
      accepted = callbackTarget.acceptGoal(goal);
      if (accepted) {
        // the user accepted the goal
        try {
          goalTracker.get(goalIdString).state.transition(ServerStateMachine.Events.ACCEPT);
        }
        catch (Exception e) {
          e.printStackTrace(System.out);
        }
      } else {
        // the user rejected the goal
        try {
          goalTracker.get(goalIdString).state.transition(ServerStateMachine.Events.REJECT);
        }
        catch (Exception e) {
          e.printStackTrace(System.out);
        }
      }
    }
  }

  /**
   * Called when we get a message on the subscribed cancel topic.
   */
  public void gotCancel(GoalID gid) {
    // Propagate the callback
    if (callbackTarget != null) {
      callbackTarget.cancelReceived(gid);
    }
  }

  /**
   * Publishes the current status on the server's status topic.
   * This is used like a heartbeat to update the status of every tracked goal.
   */
  public void sendStatusTick() {
    GoalStatusArray status = statusPublisher.newMessage();
    GoalStatus goalStatus;
    Vector<GoalStatus> goalStatusList = new Vector<GoalStatus>();

    for (ServerGoal sg : goalTracker.values()) {
      goalStatus = node.getTopicMessageFactory().newFromType(GoalStatus._TYPE);
      goalStatus.setGoalId(getGoalId(sg.goal));
      goalStatus.setStatus((byte)sg.state.getState());
      goalStatusList.add(goalStatus);
    }
    status.setStatusList(goalStatusList);
    sendStatus(status);
  }

  public T_ACTION_RESULT newResultMessage() {
    return resultPublisher.newMessage();
  }

  public T_ACTION_FEEDBACK newFeedbackMessage() {
    return feedbackPublisher.newMessage();
  }

  /**
   * Returns the goal ID object related to a given action goal.
   * @param goal An action goal message.
   * @return The goal ID object.
   */
  public GoalID getGoalId(T_ACTION_GOAL goal) {
    GoalID gid = null;
    try {
      Method m = goal.getClass().getMethod("getGoalId");
      m.setAccessible(true); // workaround for known bug http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6924232
      gid = (GoalID)m.invoke(goal);
    }
    catch (Exception e) {
      e.printStackTrace(System.out);
    }
    return gid;
  }

  /**
   * Get the current state of the referenced goal.
   * @param goalId String representing the ID of the goal.
   * @return The current state of the goal or -100 if the goal ID is not tracked.
   * @see actionlib_msgs.GoalStatus
   */
  public int getGoalState(String goalId) {
    int ret = 0;

    if (goalTracker.containsKey(goalId)) {
      ret = goalTracker.get(goalId).state.getState();
    } else {
      ret = -100;
    }
    return ret;
  }

  /**
   * Express a succeed event for this goal. The state of the goal will be updated.
   */
  public void setSucceed(String goalIdString) {
    try {
      goalTracker.get(goalIdString).state.transition(ServerStateMachine.Events.SUCCEED);
    }
    catch (Exception e) {
    }
  }

  /**
  * Publishes the server's topics and suscribes to the client's topics.
  */
  private void connect(ConnectedNode node) {
    publishServer(node);
    subscribeToClient(node);
  }

  /**
   * Finish the action server. Unregister publishers and listeners.
   */
  public void finish() {
    callbackTarget = null;
    unpublishServer();
    unsubscribeToClient();
  }

  protected void finalize() {
    finish();
  }
}
