# Actionlib for Rosjava
A pure java implementation of [actionlib](http://wiki.ros.org/actionlib) for [rosjava](http://wiki.ros.org/rosjava).
Features implemented:

### Basic client:
* methods for publishing goal and cancel messages.
* callback interface for status, feedback and result messages.
* goal state tracking is not implemented yet

### Basic server:
* methods for publishing result, feedback and status messages.
* periodic goal status publishing as heartbeat
* callback interface for accepting goals, and receiving cancel messages.
* multi-goal state tracking.


## Requirements:
* ROS Indigo http://wiki.ros.org/
* Rosjava ```$ sudo apt-get install ros-indigo-rosjava``` http://wiki.ros.org/rosjava/Tutorials/indigo/Installation
* Java 1.7 or greater (OpenJDK should work)
* Also make sure you have the following packages: ```ros-indigo-actionlib``` ```ros-indigo-actionlib-tutorials``` ```ros-indigo-genjava```
* Download the project: ```$ git clone https://github.com/ernestmc/rosjava_actionlib.git```

You can find a video tutorial showing how to install and test the library following the instructions below:
https://youtu.be/FmmsMdEbYFs

## Compiling:
1. Go to the package folder: ```$ cd rosjava_actionlib```
2. Compile the code: ```$ catkin_make```

## Running a test client:
1. Open a new terminal and get a ros master running: ```$ roscore```
2. In another terminal run the actionlib sample server: ```$ rosrun actionlib_tutorials fibonacci_server```
3. Run our client:
  * Source the project environment: ```$ source devel/setup.bash```
  * Run the client: ```$ rosrun rosjava_actionlib execute com.github.ekumen.rosjava_actionlib.TestClient```

## Output from the test client
The test client will connect to the fibonacci server and send it a goal. It
should then receive feedback from the server and a final response. The output
should look something like this:
```
Sending goal #0...
Goal sent.
Got Fibonacci result sequence!0 1
Sending goal #1...
Goal sent.
Feedback from Fibonacci server: 0 1 1
Got Fibonacci result sequence!0 1 1
Sending goal #2...
Goal sent.
Sending goal ID: fibonacci_test_3...
Goal sent.
Feedback from Fibonacci server: 0 1 1
Got Fibonacci result sequence!Feedback from Fibonacci server:
0 1 1
Cancelling goal ID: fibonacci_test_3
Got Fibonacci result sequence!

```

## Running a test server:
1. Run our server:
  * Source the project environment: ```$ source devel/setup.bash```
  * Run the server: ```$ rosrun rosjava_actionlib execute com.github.ekumen.rosjava_actionlib.TestServer```
2. If its not already running, open a new terminal and get a ros master running: ```$ roscore```
3. In another terminal run the actionlib sample client: ```$ rosrun actionlib_tutorials fibonacci_client```
4. Once finished, use Ctrl+C to close the server.

## Output from the test server
The test server will start running and wait for clients to connect and send goal messages.
Once the fibonacci client sends a goal, the server accepts it and sends a result. The output
should look something like this:
```
Goal received.
Goal accepted.
Sending result...
```

## Running demos for the server and the client
You can launch a demo client and a fibonacci action server all at once using:
```
roslaunch rosjava_actionlib client_demo.launch --screen
```


You can also launch a demo server and a fibonacci action client all at once using:
```
roslaunch rosjava_actionlib server_demo.launch --screen
```

Use Ctrl+C to stop the execution once it's finished.
