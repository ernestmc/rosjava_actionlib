/**
 * Copyright 2015 Ekumen www.ekumenlabs.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Class to test the actionlib client.
 * @author Ernesto Corbellini ecorbellini@ekumenlabs.com
 */
public class TestClient extends AbstractNodeMain implements ActionClientListener<FibonacciActionFeedback, FibonacciActionResult> {
  private ActionClient ac = null;
  private volatile boolean resultReceived = false;
  private Log log = LogFactory.getLog(ActionClient.class);

  @Override
  public GraphName getDefaultNodeName() {
    return GraphName.of("fibonacci_test_client");
  }

  @Override
  public void onStart(ConnectedNode node) {
    ac = new ActionClient<FibonacciActionGoal, FibonacciActionFeedback, FibonacciActionResult>(node, "/fibonacci", FibonacciActionGoal._TYPE, FibonacciActionFeedback._TYPE, FibonacciActionResult._TYPE);
    FibonacciActionGoal goalMessage;
    int repeat = 3;
    int i = 0;
    String goalId = "fibonacci_test_";

    // Attach listener for the callbacks
    ac.attachListener(this);

    System.out.println("Waiting for actionlib server to start...");
    ac.waitForActionServerToStart();
    System.out.println("actionlib server started.");

    // Create Fibonacci goal message
    //goalMessage = (FibonacciActionGoal)ac.newGoalMessage();
    //FibonacciGoal fibonacciGoal = goalMessage.getGoal();

    // set Fibonacci parameter
    //fibonacciGoal.setOrder(6);

    /*for (i = 0; i < repeat; i++) {
      //sleep(10000);
      System.out.println("Sending goal #" + i + "...");
      goalMessage = (FibonacciActionGoal)ac.newGoalMessage();
      goalMessage.getGoal().setOrder(i*3);
      ac.sendGoal(goalMessage, goalId + i);
      System.out.println("Goal sent.");
      resultReceived = false;
    }*/

    // send another message and cancel it
    goalId += i;
    goalMessage = (FibonacciActionGoal)ac.newGoalMessage();
    goalMessage.getGoal().setOrder(3);
    //System.out.println("Sending goal ID: " + goalId + "...");
    //ac.sendGoal(goalMessage, goalId);
    ac.sendGoal(goalMessage);
    GoalID gid = ac.getGoalId(goalMessage);
    System.out.println("Goal sent. Goal ID: " + gid);
    //sleep(1000);
    //System.out.println("Cancelling goal ID: " + goalId);
    //ac.sendCancel(gid);
    sleep(5000);

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
      log.info("GoalID: " + gs.getGoalId().getId() + " -- GoalStatus: " + gs.getStatus() + " -- " + gs.getText());
    }
    log.info("Current state of our goal: " + ClientStateMachine.ClientStates.translateState(ac.getGoalState()));
  }

  void sleep(long msec) {
    try {
      Thread.sleep(msec);
    }
    catch (InterruptedException ex) {
    }
  }
}
