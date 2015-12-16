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

public class TestServer extends AbstractNodeMain implements ActionServerListener<FibonacciActionGoal> {
  private ActionServer<FibonacciActionGoal, FibonacciActionFeedback, FibonacciActionResult> as = null;

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("fibonacci_test_server");
  }

  @Override
  public void onStart(ConnectedNode node) {
    as = new ActionServer<FibonacciActionGoal, FibonacciActionFeedback,
      FibonacciActionResult>(node, "/fibonacci", FibonacciActionGoal._TYPE,
      FibonacciActionFeedback._TYPE, FibonacciActionResult._TYPE);

    as.attachListener(this);
  }

  @Override
  public void goalReceived(FibonacciActionGoal goal) {
    System.out.println("Goal received.");
    as.sendResult(as.newResultMessage());
  }

  @Override
  public void cancelReceived(GoalID id) {
    System.out.println("Cancel received.");
  }
}
