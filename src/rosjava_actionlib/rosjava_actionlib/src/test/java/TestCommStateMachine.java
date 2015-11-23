import com.github.ekumen.rosjava_actionlib.*;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.Vector;

public class TestCommStateMachine {
  @Test
  public void test() {
    CommStateMachine stateMachine = new CommStateMachine();
    Vector nextStates;

    // set initial state
    stateMachine.setState(CommStateMachine.ClientStates.WAITING_FOR_GOAL_ACK);

    // transition to next states
    nextStates = stateMachine.getTransition(ActionGoal.GoalStates.ACTIVE);

    assertEquals(nextStates.firstElement(), CommStateMachine.ClientStates.WAITING_FOR_GOAL_ACK);
  }
}
