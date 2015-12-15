# Actionlib for Rosjava
A pure java implementation of [actionlib](http://wiki.ros.org/actionlib) for rosjava.
For now there is a basic client implemented with the following features:
* send goal
* send cancel
* callback interface for status, feedback and result messages.


## Requirements:
* ROS Indigo http://wiki.ros.org/
* Rosjava ```$ sudo apt-get install ros-indigo-rosjava``` http://wiki.ros.org/rosjava/Tutorials/indigo/Installation
* Java 1.7 or greater (OpenJDK should work)
* Also make sure you have the following packages: ```ros-indigo-actionlib``` ```ros-indigo-actionlib-tutorials``` ```ros-indigo-genjava```
* Download the project: ```$ git clone https://github.com/ernestmc/rosjava_actionlib.git```

## Compiling:
1. Go to the package folder: ```$ cd rosjava_actionlib```
2. Compile the code: ```$ catkin_make```
3. Move to the project folder: ```$ cd src/rosjava_actionlib```
4. Build the execution target: ```$ ./gradlew deployApp```

## Running:
1. Open a new terminal and get a ros server running: ```$ roscore```
2. In another terminal run the actionlib sample server: ```$ rosrun actionlib_tutorials fibonacci_server```
3. Run our test client:
  * Go back to the package folder: ```cd ../..```
  * Source the project environment: ```$ source devel/setup.bash```
  * Run the client: ```$ rosrun rosjava_actionlib execute com.github.ekumen.rosjava_actionlib.TestClient```

## Output
The test client will connect to the fibonacci server and send it a goal. It
should then receive feedback from the server and a final response. The output
should look something like this:
```
Sending goal #3...
Goal sent.
Feedback from Fibonacci server: 0 1 1
Feedback from Fibonacci server: 0 1 1 2
Feedback from Fibonacci server: 0 1 1 2 3
Feedback from Fibonacci server: 0 1 1 2 3 5
Feedback from Fibonacci server: 0 1 1 2 3 5 8
Feedback from Fibonacci server: 0 1 1 2 3 5 8 13
Got Fibonacci result sequence!0 1 1 2 3 5 8 13
Sending goal #2...
Goal sent.
Feedback from Fibonacci server: 0 1 1
Feedback from Fibonacci server: 0 1 1 2
Feedback from Fibonacci server: 0 1 1 2 3
Feedback from Fibonacci server: 0 1 1 2 3 5
Feedback from Fibonacci server: 0 1 1 2 3 5 8
Feedback from Fibonacci server: 0 1 1 2 3 5 8 13
Got Fibonacci result sequence!0 1 1 2 3 5 8 13
Sending goal #1...
Goal sent.
Feedback from Fibonacci server: 0 1 1
Feedback from Fibonacci server: 0 1 1 2
Feedback from Fibonacci server: 0 1 1 2 3
Feedback from Fibonacci server: 0 1 1 2 3 5
Feedback from Fibonacci server: 0 1 1 2 3 5 8
Feedback from Fibonacci server: 0 1 1 2 3 5 8 13
```
