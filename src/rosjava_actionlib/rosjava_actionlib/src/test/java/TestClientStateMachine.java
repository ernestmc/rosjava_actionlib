import com.github.ekumen.rosjava_actionlib.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.Vector;
import java.util.Arrays;
import actionlib_msgs.GoalStatus;

public class TestClientStateMachine {
    private ClientStateMachine subject;
   
   // Executes before each test. 
  @Before
  public void setUp() {
    subject = new ClientStateMachine();
  }
  
  @Test
  public void testGetState() {
      // Setup
      int expectedState = ClientStateMachine.ClientStates.WAITING_FOR_GOAL_ACK;
      int actualState;
      
      subject.setState(expectedState);
      
      // Optional: Precondition
      
      // Exercise
      actualState = subject.getState();
      
      // Assertion - Postcondition
      assertEquals(expectedState, actualState);
  }
  
  @Test
  public void testSetState() {
       // Setup
      int expectedState = ClientStateMachine.ClientStates.WAITING_FOR_GOAL_ACK;
     
      // Optional: Precondition 
      assertEquals(subject.getState(), 0);
      
      // Exercise
      subject.setState(expectedState);
      
      // Assertion - Postcondition
      assertEquals(expectedState, subject.getState());
  }
  
  @Test
  public void testUpdateStatusWhenStateIsNotDone() {
       // Setup
      int expectedStatus = 7;
      subject.setState(ClientStateMachine.ClientStates.WAITING_FOR_GOAL_ACK);
     
      // Optional: Precondition 
      assertEquals(0, subject.latestGoalStatus);
      
      // Exercise
      subject.updateStatus(expectedStatus);
      
      // Assertion - Postcondition
      assertEquals(expectedStatus, subject.latestGoalStatus);
  }
  
  @Test
  public void testUpdateStatusWhenStateIsDone() {
       // Setup
      subject.setState(ClientStateMachine.ClientStates.DONE);
     
      // Optional: Precondition 
      assertEquals(0, subject.latestGoalStatus);
      
      // Exercise
      subject.updateStatus(7);
      
      // Assertion - Postcondition
      assertEquals(0, subject.latestGoalStatus);
  }
  
  @Test
  public void testCancelOnCancellableStates() {
      checkCancelOnInitialCancellableState(ClientStateMachine.ClientStates.WAITING_FOR_GOAL_ACK);
      checkCancelOnInitialCancellableState(ClientStateMachine.ClientStates.PENDING);
      checkCancelOnInitialCancellableState(ClientStateMachine.ClientStates.ACTIVE);
  }
  
  @Test
  public void testCancelOnNonCancellableStates() {
      checkCancelOnInitialNonCancellableState(ClientStateMachine.ClientStates.INVALID_TRANSITION);
      checkCancelOnInitialNonCancellableState(ClientStateMachine.ClientStates.NO_TRANSITION);
      checkCancelOnInitialNonCancellableState(ClientStateMachine.ClientStates.WAITING_FOR_RESULT);
      checkCancelOnInitialNonCancellableState(ClientStateMachine.ClientStates.WAITING_FOR_CANCEL_ACK);
      checkCancelOnInitialNonCancellableState(ClientStateMachine.ClientStates.RECALLING);
      checkCancelOnInitialNonCancellableState(ClientStateMachine.ClientStates.PREEMPTING);
      checkCancelOnInitialNonCancellableState(ClientStateMachine.ClientStates.DONE);
      checkCancelOnInitialNonCancellableState(ClientStateMachine.ClientStates.LOST);
  }  
  
  private void checkCancelOnInitialCancellableState(int initialState) {
    subject.setState(initialState);
      
      assertTrue("Failed test on initial state " + initialState, subject.cancel());
      
      assertEquals("Failed test on initial state " + initialState, ClientStateMachine.ClientStates.WAITING_FOR_CANCEL_ACK, subject.getState());
  }


  private void checkCancelOnInitialNonCancellableState(int initialState) {
    subject.setState(initialState);
      
      assertFalse("Failed test on initial state " + initialState, subject.cancel());
      
      assertEquals("Failed test on initial state " + initialState, initialState, subject.getState());
  }
  
  @Test
  public void testResultReceivedWhileWaitingForResult() {
    subject.setState(ClientStateMachine.ClientStates.WAITING_FOR_RESULT);
    subject.resultReceived();
    assertEquals(ClientStateMachine.ClientStates.DONE, subject.getState());
  }
  
  @Test
  public void testResultReceivedWhileNotWaitingForResult() {
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.INVALID_TRANSITION);
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.NO_TRANSITION);
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.WAITING_FOR_GOAL_ACK);
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.PENDING);
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.ACTIVE);
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.WAITING_FOR_CANCEL_ACK);
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.RECALLING);
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.PREEMPTING);
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.DONE);
    checkResultReceivedWhileNotWaitingForResult(ClientStateMachine.ClientStates.LOST);
  }
  
  private void checkResultReceivedWhileNotWaitingForResult(int state) {
    subject.setState(state);
    subject.resultReceived();
    assertEquals("Failed test on initial state " + state, state, subject.getState());    
  }
  
  @Test
  public void testGetTrasition() {
      Vector<Integer> expected;
      expected = new Vector<>(Arrays.asList(ClientStateMachine.ClientStates.PENDING));
      checkGetTransition(ClientStateMachine.ClientStates.WAITING_FOR_GOAL_ACK,
        actionlib_msgs.GoalStatus.PENDING, expected);
        
      expected = new Vector<>(Arrays.asList(ClientStateMachine.ClientStates.PENDING,
        ClientStateMachine.ClientStates.WAITING_FOR_RESULT));
      checkGetTransition(ClientStateMachine.ClientStates.WAITING_FOR_GOAL_ACK,
        actionlib_msgs.GoalStatus.REJECTED, expected);
  }
  
  private void checkGetTransition(int initialState, int goalStatus, Vector<Integer> expected) {
    subject.setState(initialState);
    Vector<Integer> output = subject.getTransition(goalStatus);
    assertEquals(expected, output);
  }
}
