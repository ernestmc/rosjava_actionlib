
package com.github.ekumen.rosjava_actionlib;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import org.ros.node.topic.Publisher;
import org.ros.message.MessageListener;

public class ActionClient extends AbstractNodeMain {
  //actionlib_tutorials.FibonacciActionGoal actionGoal;
  Publisher<actionlib_tutorials.FibonacciActionGoal> clientGoal;
  //Publisher<actionlib_msgs.cancel> clientCancel;
  //Suscriber<actionlib_msgs.status> serverStatus;
  Subscriber<actionlib_tutorials.FibonacciActionResult> serverResult;
  //Suscriber<actionlib_tutorials.FibonacciActionFeedback> serverFeedback;

  private void publishClient(ConnectedNode node) {
    clientGoal = node.newPublisher("fibonacci/goal",
      actionlib_tutorials.FibonacciActionGoal._TYPE);
    //clientCancel = connectedNode.newPublisher("fibonacci/cancel",
    //  actionlib_msgs.cancel._TYPE);
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

  @Override
  public GraphName getDefaultNodeName() {
      return GraphName.of("fibonacci_client");
    }

    @Override
    public void onStart(ConnectedNode node) {
      connect(node);

      suscribeServer(node);

      // publish a goal message
      actionlib_tutorials.FibonacciActionGoal goalMessage = clientGoal.newMessage();
      actionlib_tutorials.FibonacciGoal fibonacciGoal = goalMessage.getGoal();
      // set Fibonacci parameter
      fibonacciGoal.setOrder(6);
      goalMessage.setGoal(fibonacciGoal);

      while (true) {
        clientGoal.publish(goalMessage);
        try {
          Thread.sleep(10000);
        }
        catch (InterruptedException ex) {
          ;
        }
      }
    }
}
