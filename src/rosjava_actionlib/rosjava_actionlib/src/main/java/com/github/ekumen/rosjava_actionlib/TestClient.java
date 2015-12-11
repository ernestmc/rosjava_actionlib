package com.github.ekumen.rosjava_actionlib;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.internal.message.Message;
import actionlib_tutorials.FibonacciActionGoal;
import actionlib_tutorials.FibonacciActionFeedback;
import actionlib_tutorials.FibonacciActionResult;
import actionlib_tutorials.FibonacciGoal;
import actionlib_tutorials.FibonacciFeedback;
import actionlib_tutorials.FibonacciResult;

public class TestClient extends AbstractNodeMain implements ActionClientListener<FibonacciActionFeedback, FibonacciActionResult> {
  ActionClient ac;

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("fibonacci_test_client");
  }

  @Override
  public void onStart(ConnectedNode node) {
    ac = new ActionClient<FibonacciActionGoal, FibonacciActionFeedback, FibonacciActionResult>(node, "/fibonacci", FibonacciActionGoal._TYPE, FibonacciActionFeedback._TYPE, FibonacciActionResult._TYPE);

    // Attach listener for the callbacks
    ac.attachListener(this);

    // publish a goal message
    FibonacciActionGoal goalMessage = (FibonacciActionGoal)ac.newGoalMessage();
    FibonacciGoal fibonacciGoal = goalMessage.getGoal();

    // set Fibonacci parameter
    fibonacciGoal.setOrder(6);
    goalMessage.setGoal(fibonacciGoal);

    while (true) {
      ac.sendGoal(goalMessage);
      try {
        Thread.sleep(10000);
      }
      catch (InterruptedException ex) {
        ;
      }
    }
  }

  @Override
  public void resultReceived(FibonacciActionResult message) {
    FibonacciResult result = message.getResult();
    int[] sequence = result.getSequence();
    int i;

    System.out.print("Got Fibonacci result sequence!");
    for (i=0; i<sequence.length; i++)
      System.out.print(Integer.toString(sequence[i]) + " ");
    System.out.print("\n");
  }

  @Override
  public void feedbackReceived(FibonacciActionFeedback message) {
    FibonacciFeedback result = message.getFeedback();
    int[] sequence = result.getSequence();
    int i;

    System.out.print("Feedback from Fibonacci server: ");
    for (i=0; i<sequence.length; i++)
      System.out.print(Integer.toString(sequence[i]) + " ");
    System.out.print("\n");
  }

  @Override
  public void statusReceived(Message status) {

  }
}
