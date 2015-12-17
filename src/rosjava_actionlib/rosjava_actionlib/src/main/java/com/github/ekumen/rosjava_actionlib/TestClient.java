package com.github.ekumen.rosjava_actionlib;

import java.util.List;
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
import actionlib_msgs.GoalStatusArray;
import actionlib_msgs.GoalID;
import actionlib_msgs.GoalStatus;

public class TestClient extends AbstractNodeMain implements ActionClientListener<FibonacciActionFeedback, FibonacciActionResult> {
  private ActionClient ac = null;
  private volatile boolean resultReceived = false;

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("fibonacci_test_client");
  }

  @Override
  public void onStart(ConnectedNode node) {
    ac = new ActionClient<FibonacciActionGoal, FibonacciActionFeedback, FibonacciActionResult>(node, "/fibonacci", FibonacciActionGoal._TYPE, FibonacciActionFeedback._TYPE, FibonacciActionResult._TYPE);
    int repeat = 3;

    // Attach listener for the callbacks
    ac.attachListener(this);

    // Create Fibonacci goal message
    FibonacciActionGoal goalMessage = (FibonacciActionGoal)ac.newGoalMessage();
    FibonacciGoal fibonacciGoal = goalMessage.getGoal();

    // set Fibonacci parameter
    fibonacciGoal.setOrder(6);

    while (repeat > 0) {
      sleep(10000);
      System.out.println("Sending goal #" + repeat + "...");
      ac.sendGoal(goalMessage);
      System.out.println("Goal sent.");
      //while(!resultReceived) sleep(100);
      resultReceived = false;
      repeat--;
    }

    System.exit(0);
  }

  @Override
  public void resultReceived(FibonacciActionResult message) {
    FibonacciResult result = message.getResult();
    int[] sequence = result.getSequence();
    int i;

    resultReceived = true;

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
  public void statusReceived(GoalStatusArray status) {
    List<GoalStatus> statusList = status.getStatusList();
    for(GoalStatus gs:statusList) {
      //System.out.println("GoalID: " + gs.getGoalId().getId() + " -- GoalStatus: " + gs.getStatus() + " -- " + gs.getText());
    }
  }

  void sleep(long msec) {
    try {
      Thread.sleep(msec);
    }
    catch (InterruptedException ex) {
    }
  }
}
