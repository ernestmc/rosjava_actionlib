
package com.github.ekumen.rosjava_actionlib;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.Publisher;
import org.ros.message.MessageListener;
import org.ros.internal.message.Message;

public class ActionClient<T_ACTION_GOAL extends Message> {
  T_ACTION_GOAL actionGoal;
  String actionGoalType;
  Publisher<T_ACTION_GOAL> goalPublisher;
  //Publisher<actionlib_msgs.cancel> clientCancel;
  //Suscriber<actionlib_msgs.status> serverStatus;
  Subscriber<actionlib_tutorials.FibonacciActionResult> serverResult;
  //Suscriber<actionlib_tutorials.FibonacciActionFeedback> serverFeedback;
  ConnectedNode node;
  String actionName;

  ActionClient (ConnectedNode node, String actionName, String actionGoalType) {
    this.node = node;
    this.actionName = actionName;
    this.actionGoalType = actionGoalType;
    publishClient(node);
  }

  void sendGoal(T_ACTION_GOAL goal) {
    goalPublisher.publish(goal);
  }

  private void publishClient(ConnectedNode node) {
    goalPublisher = node.newPublisher(actionName + "/goal",
      actionGoalType);
    //clientCancel = connectedNode.newPublisher("fibonacci/cancel",
    //  actionlib_msgs.cancel._TYPE);
  }

  public T_ACTION_GOAL newGoalMessage() {
    return goalPublisher.newMessage();
  }

  private void suscribeServer(ConnectedNode node) {
    serverResult = node.newSubscriber("fibonacci/result",
      actionlib_tutorials.FibonacciActionResult._TYPE);

    serverResult.addMessageListener(new MessageListener<actionlib_tutorials.FibonacciActionResult>() {
      @Override
      public void onNewMessage(actionlib_tutorials.FibonacciActionResult message) {
        gotResult(message);
      }
    });

    /*serverStatus = node.newSubscriber("fibonacci/status",
      actionlib_msgs.status._TYPE);
    serverFeedback = node.newSubscriber("fibonacci/feedback",
      actionlib_tutorials.FibonacciActionFeedback._TYPE);*/
  }

  public void gotResult(actionlib_tutorials.FibonacciActionResult message) {
    actionlib_tutorials.FibonacciResult result = message.getResult();
    int[] sequence = result.getSequence();
    int i;

    System.out.print("Got Fibonacci result sequence! ");
    for (i=0; i<sequence.length; i++)
      System.out.print(Integer.toString(sequence[i]) + " ");
    System.out.print("\n");
  }

/**
 * Publishes the client's topics and suscribes to the server's topics.
 */
  public void connect(ConnectedNode node) {
    publishClient(node);
    //suscribeServer(node);
  }

}
