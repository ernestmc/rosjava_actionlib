import com.github.ekumen.rosjava_actionlib.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.Vector;
import actionlib_msgs.GoalStatus;

public class TestClientStateMachine {
  @Test
  public void test() {
    int state;
    ClientStateMachine stateMachine = new ClientStateMachine();

    //
    // set initial state
    stateMachine.setState(ClientStateMachine.ClientStates.WAITING_FOR_GOAL_ACK);
    // transition to next states
    stateMachine.transition(actionlib_msgs.GoalStatus.ACTIVE);
    assertEquals(ClientStateMachine.ClientStates.ACTIVE, stateMachine.getState());
    stateMachine.transition(actionlib_msgs.GoalStatus.PREEMPTING);
    assertEquals(ClientStateMachine.ClientStates.PREEMPTING, stateMachine.getState());
    stateMachine.transition(actionlib_msgs.GoalStatus.SUCCEEDED);
    assertEquals(ClientStateMachine.ClientStates.WAITING_FOR_RESULT, stateMachine.getState());
  }
}
