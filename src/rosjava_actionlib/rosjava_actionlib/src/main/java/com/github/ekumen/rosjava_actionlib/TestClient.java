package com.github.ekumen.rosjava_actionlib;

import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import actionlib_tutorials.FibonacciActionGoal;
import actionlib_tutorials.FibonacciGoal;

public class TestClient extends AbstractNodeMain {
  ActionClient ac;

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("fibonacci_test_client");
  }

  @Override
  public void onStart(ConnectedNode node) {
    ac = new ActionClient<FibonacciActionGoal>(node, "/fibonacci", FibonacciActionGoal._TYPE);

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
}
