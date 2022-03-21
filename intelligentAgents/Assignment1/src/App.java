
//import java.util.ArrayList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.Random;
import java.io.File; // Import the File class
import java.io.FileWriter;
import java.io.IOException; // Import the IOException class to handle errors
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedWriter;
import java.util.List;
import java.util.Scanner;

public class App {

    public static State[][] stateList;
    public static State[][] stateListOld;
    public static State[] policy;
    public static State[][] policyList;
    public static double SMALL_ENOUGH = 0.1;
    // public static double SMALL_ENOUGH = 0.366;
    public static double GAMMA = 0.99;
    public static double NOISE = 0.1;
    public static int row_n = 6;
    public static int col_n = 6;
    public static int wall_n = 5;
    public static int reward_n = 6;
    public static int neg_n = 5;
    public static int policyIterationN = 0;
    public static double beforeApply = 0;
    public static double afterApply = 0;
    public static String[] valueIterationFiles = { "ValueIteration.txt", "ValueIterationOP.txt",
            "ValueIterationOPPath.txt" ,"valueIterationUtility.txt"};
    public static String valueIterationFile = valueIterationFiles[0];
    public static String valueIterationOPFile = valueIterationFiles[1];
    public static String valueIterationOPPathFile = valueIterationFiles[2];
    public static String valueIterationUtility = valueIterationFiles[3];
    public static String[] policyIterationFiles = { "PolicyIteration.txt", "PolicyIterationOP.txt",
    "PolicyOPPath.txt","policyIterationUtility.txt","policyIterationChart.txt" };
    public static String policyIterationFile = policyIterationFiles[0];
    public static String policyIterationOPFile = policyIterationFiles[1];
    public static String policyIterationOPPathFile = policyIterationFiles[2];
    public static String policyIterationUtility = policyIterationFiles[3];
    public static String policyIterationChart = policyIterationFiles[4];
  
    public static ArrayList<ArrayList<Double>> DataList;

    public static void main(String[] args) throws Exception {

        //initialise all array 
        DataList = new ArrayList<ArrayList<Double>>();
        stateListOld = new State[row_n][col_n];
        stateList = new State[row_n][col_n];
        policyList = new State[row_n][col_n];

        // init world's states with transition model, reward and values.
        InitWorld();

        // header for textfile
        String header = ",";
        for (int c = 0; c < col_n; c++) {
            for (int r = 0; r < row_n; r++) {
                header += "(" + r + ":" + c + "),";
            }
        }

        //ask the user for input to see which to run
        Scanner scanner = new Scanner(System.in); // Create a Scanner object
        System.out.println("Enter 1 for value iteration, 2 for policy iteration:");
        String choice = scanner.nextLine(); // Read user input

        if (choice.equals("1")) {
            //delete existing files related to value iteration before running
            for (String filename : valueIterationFiles) {
                Path fileToDeletePath = Paths.get(filename);
                Files.deleteIfExists(fileToDeletePath);
            }
            //add heading to text file valueIteration.txt
            CreateFile(header, valueIterationFile);

            //run the value iteration algo
            ValueIteration();
            
            //constructing optimal policy from updated values
            GetOptimalPolicy();

            //generate sample path from start
            GetOptimalPolicyPath(valueIterationOPPathFile);
            
            //creating map of the policy to text file valueIterationOP.txt
            ConstructOptimalPolicy(valueIterationOPFile);

            //print optimal utility value of each state to text file valueIterationUtility.txt
            PrintOptimalValue(valueIterationUtility);
        } else {
             //delete existing files related to value iteration before running
            for (String filename : policyIterationFiles) {
                Path fileToDeletePath = Paths.get(filename);
                Files.deleteIfExists(fileToDeletePath);
            }

             //add heading to text file policyIteration.txt
            CreateFile(header, policyIterationFile);

            //get a random policy to improve on
            InitialPolicyRandom();
            //run the policy iteration
            PolicyIteration();
             //print optimal utility value of each state to text file policyIterationUtility.txt
            PrintOptimalValue(policyIterationUtility);
        }
        scanner.close();

    }

    public static void InitWorld() {

        // init everything to be a empty space at the start
        for (int r = 0; r < row_n; r++) {
            for (int c = 0; c < col_n; c++) {
                State s = new State(r, c);

                s.setReward(-0.04);
                // Define initial value 
                s.setValue(0);

                //set transition model for each box to the state.
                if (r == 0 && c == 0) {
                    s.setActions(new String[] { "D" });
                }
                if (r == 0 && c == 1) {
                    s.setActions(new String[] {});
                }
                if (r == 0 && c == 2) {
                    s.setActions(new String[] { "D", "R" });
                }
                if (r == 0 && c == 3) {
                    s.setActions(new String[] { "D", "L", "R" });
                }
                if (r == 0 && c == 4) {
                    s.setActions(new String[] { "L", "R" });
                }
                if (r == 0 && c == 5) {
                    s.setActions(new String[] { "D", "L" });
                }

                if (r == 1 && c == 0) {
                    s.setActions(new String[] { "U", "D", "R" });
                }
                if (r == 1 && c == 1) {
                    s.setActions(new String[] { "D", "L", "R" });
                }
                if (r == 1 && c == 2) {
                    s.setActions(new String[] { "U", "D", "L", "R" });
                }
                if (r == 1 && c == 3) {
                    s.setActions(new String[] { "U", "D", "L" });
                }
                if (r == 1 && c == 4) {
                    s.setActions(new String[] {});
                }
                if (r == 1 && c == 5) {
                    s.setActions(new String[] { "U", "D" });
                }

                if (r == 2 && c == 0) {
                    s.setActions(new String[] { "U", "D", "R" });
                }
                if (r == 2 && c == 1) {
                    s.setActions(new String[] { "U", "D", "L", "R" });
                }
                if (r == 2 && c == 2) {
                    s.setActions(new String[] { "U", "D", "L", "R" });
                }
                if (r == 2 && c == 3) {
                    s.setActions(new String[] { "U", "D", "L", "R" });
                }
                if (r == 2 && c == 4) {
                    s.setActions(new String[] { "D", "L", "R" });
                }
                if (r == 2 && c == 5) {
                    s.setActions(new String[] { "U", "D", "L" });
                }

                if (r == 3 && c == 0) {
                    s.setActions(new String[] { "U", "D", "R" });
                }
                if (r == 3 && c == 1) {
                    s.setActions(new String[] { "U", "L", "R" });
                }
                if (r == 3 && c == 2) {
                    s.setActions(new String[] { "U", "L", "R" });
                }
                if (r == 3 && c == 3) {
                    s.setActions(new String[] { "U", "L", "R" });
                }
                if (r == 3 && c == 4) {
                    s.setActions(new String[] { "U", "D", "L", "R" });
                }
                if (r == 3 && c == 5) {
                    s.setActions(new String[] { "U", "D", "L" });
                }

                if (r == 4 && c == 0) {
                    s.setActions(new String[] { "U", "D" });
                }
                if (r == 4 && c == 1) {
                    s.setActions(new String[] {});
                }
                if (r == 4 && c == 2) {
                    s.setActions(new String[] {});
                }
                if (r == 4 && c == 3) {
                    s.setActions(new String[] {});
                }
                if (r == 4 && c == 4) {
                    s.setActions(new String[] { "U", "D", "R" });
                }
                if (r == 4 && c == 5) {
                    s.setActions(new String[] { "U", "D", "L" });
                }

                if (r == 5 && c == 0) {
                    s.setActions(new String[] { "U", "R" });
                }
                if (r == 5 && c == 1) {
                    s.setActions(new String[] { "L", "R" });
                }
                if (r == 5 && c == 2) {
                    s.setActions(new String[] { "L", "R" });
                }
                if (r == 5 && c == 3) {
                    s.setActions(new String[] { "L", "R" });
                }
                if (r == 5 && c == 4) {
                    s.setActions(new String[] { "U", "L", "R" });
                }
                if (r == 5 && c == 5) {
                    s.setActions(new String[] { "U", "L" });
                }

                //add basic init values to lists
                stateList[r][c] = s;
                policyList[r][c] = s.clone();
            }
        }

        //set wall locations
        String wallStr = "0,1 1,4 4,1 4,2 4,3";
        String[] wallCoorStr = wallStr.split("\\s+");
        State[] wallList = new State[wall_n];
        for (int i = 0; i < wall_n; i++) {

            String[] colRow = wallCoorStr[i].split(",");
            int row = Integer.parseInt(colRow[0]);
            int col = Integer.parseInt(colRow[1]);
            State wall1 = stateList[row][col];

            wall1.setReward(0);
            wall1.setValue(0);
            wall1.setIsWall(true);
            wallList[i] = wall1;
            policyList[row][col] = wall1.clone();

        }

        //set reward locations
        String rewardStr = "0,0 0,2 0,5 1,3 2,4 3,5";
        String[] rewardCoorStr = rewardStr.split("\\s+");
        State[] rewardList = new State[reward_n];
        for (int i = 0; i < reward_n; i++) {

            String[] colRow = rewardCoorStr[i].split(",");
            int row = Integer.parseInt(colRow[0]);
            int col = Integer.parseInt(colRow[1]);
            State reward = stateList[row][col];

            reward.setReward(1.00);
            reward.setValue(0);
            rewardList[i] = reward;
            policyList[row][col] = reward.clone();

        }

        //set negative reward locations
        String negStr = "1,1 1,5 2,2 3,3 4,4";
        String[] negCoorStr = negStr.split("\\s+");
        State[] negList = new State[reward_n];
        for (int i = 0; i < neg_n; i++) {

            String[] colRow = negCoorStr[i].split(",");
            int row = Integer.parseInt(colRow[0]);
            int col = Integer.parseInt(colRow[1]);
            State neg = stateList[row][col];
            neg.setReward(-1.00);
            neg.setValue(0);

            negList[i] = neg;
            policyList[row][col] = neg.clone();

        }
        //print init map
        String map = "";
        for (int r = 0; r < row_n; r++) {
            for (int c = 0; c < col_n; c++) {

                double reward = stateList[r][c].getReward();
                map += reward + "|";

            }
            map += "\n";
        }

        System.out.println(map);

    }

    public static void ValueIteration() {

        int iteration = 0;
        while (true) {
            //biggest_change use to keep track of the maximum change different of a state 
            //in a iteration before and after execution
            double biggest_change = 0;

            //loop through all boxes
            for (int r = 0; r < stateList.length; r++) {
                for (int c = 0; c < stateList.length; c++) {

                    if (stateList[r][c].getIsWall() == false) {

                        // getting out current state
                        State current = stateList[r][c];
                        double old_value = current.getValue();//set before for comparrison
                        double new_value = 0;

                        if (current.getActions().length > 0) {// not wall

                            //getting the sum of all the utility that current node can access
                            double upUtil = 0;
                            double downUtil = 0;
                            double leftUtil = 0;
                            double rightUtil = 0;

                            
                            if (r - 1 >= 0) {// means there is a path up
                                State intendedState = stateList[r - 1][c];
                                if (intendedState.getIsWall()) {// check if up is a wall
                                    upUtil += 0.8 * current.getValue();// if wall stay current
                                } else {// not wall add value of next
                                    upUtil += 0.8 * intendedState.getValue();
                                }
                                // left
                                if (c - 1 >= 0) {
                                    State left = stateList[r][c - 1];
                                    if (left.getIsWall()) {// check if up is a wall
                                        upUtil += 0.1 * current.getValue();// if wall stay current
                                    } else {// not wall add value of next
                                        upUtil += 0.1 * left.getValue();
                                    }
                                } else {// turn left hit wall
                                    upUtil += 0.1 * current.getValue();
                                }
                                // right
                                if (c + 1 < col_n) {
                                    State right = stateList[r][c + 1];
                                    if (right.getIsWall()) {// check if up is a wall
                                        upUtil += 0.1 * current.getValue();// if wall stay current
                                    } else {// not wall add value of next
                                        upUtil += 0.1 * right.getValue();
                                    }

                                } else {// rturn right hit wall
                                    upUtil += 0.1 * current.getValue();
                                }
                            } else {// turn up hit wall that is boundary line
                                upUtil += 0.8 * current.getValue();//intended action remains in same box
                                // left
                                if (c - 1 >= 0) {
                                    State left = stateList[r][c - 1];
                                    if (left.getIsWall()) {// check if up is a wall
                                        upUtil += 0.1 * current.getValue();// if wall stay current
                                    } else {// not wall add value of next
                                        upUtil += 0.1 * left.getValue();
                                    }
                                } else {// turn left hit wall
                                    upUtil += 0.1 * current.getValue();
                                }
                                // right
                                if (c + 1 < col_n) {
                                    State right = stateList[r][c + 1];

                                    if (right.getIsWall()) {// check if up is a wall
                                        upUtil += 0.1 * current.getValue();// if wall stay current
                                    } else {// not wall add value of next
                                        upUtil += 0.1 * right.getValue();
                                    }
                                } else {// rturn right hit wall
                                    upUtil += 0.1 * current.getValue();
                                }
                            }
                            // end of up
                            // if Down---------------------------------
                            if (r + 1 < row_n) {// check if can go down
                                State intendedState = stateList[r + 1][c];
                                if (intendedState.getIsWall()) {// check if up is a wall
                                    downUtil += 0.8 * current.getValue();// if wall stay current
                                } else {// not wall add value of next
                                    downUtil += 0.8 * intendedState.getValue();
                                }
                                // go left
                                if (c + 1 < col_n) {
                                    State left = stateList[r][c + 1];
                                    if (left.getIsWall()) {// check if up is a wall
                                        downUtil += 0.1 * current.getValue();// if wall stay current
                                    } else {// not wall add value of next
                                        downUtil += 0.1 * left.getValue();
                                    }
                                } else {
                                    // hit wall
                                    downUtil += 0.1 * current.getValue();
                                }

                                // go right
                                if (c - 1 >= 0) {
                                    State right = stateList[r][c - 1];
                                    if (right.getIsWall()) {// check if up is a wall
                                        downUtil += 0.1 * current.getValue();// if wall stay current
                                    } else {// not wall add value of next
                                        downUtil += 0.1 * right.getValue();
                                    }
                                } else {
                                    // wall
                                    downUtil += 0.1 * current.getValue();
                                }
                            } else {// hit wall that is boundary line
                                downUtil += 0.8 * current.getValue();
                                if (c + 1 < col_n) {
                                    State left = stateList[r][c + 1];
                                    if (left.getIsWall()) {// check if up is a wall
                                        downUtil += 0.1 * current.getValue();// if wall stay current
                                    } else {// not wall add value of next
                                        downUtil += 0.1 * left.getValue();
                                    }
                                } else {
                                    // hit wall
                                    downUtil += 0.1 * current.getValue();
                                }

                                // go right
                                if (c - 1 >= 0) {
                                    State right = stateList[r][c - 1];
                                    if (right.getIsWall()) {// check if up is a wall
                                        downUtil += 0.1 * current.getValue();// if wall stay current
                                    } else {// not wall add value of next
                                        downUtil += 0.1 * right.getValue();
                                    }
                                } else {
                                    // wall
                                    downUtil += 0.1 * current.getValue();
                                }

                            }
                            // end of down
                            // If Go LEFT------------------------
                            if (c - 1 >= 0) {
                                State intendedState = stateList[r][c - 1];
                                leftUtil += 0.8 * intendedState.getValue();
                                if (r - 1 > row_n) {// go right
                                    State right = stateList[r - 1][c];
                                    leftUtil += 0.1 * right.getValue();
                                } else {
                                    // hit wall
                                    leftUtil += 0.1 * current.getValue();
                                }

                                if (r + 1 < row_n) {// left
                                    State left = stateList[r + 1][c];
                                    leftUtil += 0.1 * left.getValue();
                                } else {
                                    // hit wall
                                    leftUtil += 0.1 * current.getValue();
                                }
                            } else {
                                // hit wall that is boundary line
                                leftUtil += 0.8 * current.getValue();
                                if (r - 1 > row_n) {// go right
                                    State right = stateList[r - 1][c];
                                    leftUtil += 0.1 * right.getValue();
                                } else {
                                    // hit wall
                                    leftUtil += 0.1 * current.getValue();
                                }

                                if (r + 1 < row_n) {// left
                                    State left = stateList[r + 1][c];
                                    leftUtil += 0.1 * left.getValue();
                                } else {
                                    // hit wall
                                    leftUtil += 0.1 * current.getValue();
                                }

                            }
                            // end of left-
                            // IF GO RIGHT ------------
                            if (c + 1 < col_n) {
                                State intendedState = stateList[r][c + 1];
                                rightUtil += 0.8 * intendedState.getValue();
                                // if right
                                if (r + 1 < row_n) {
                                    State right = stateList[r + 1][c];
                                    rightUtil += 0.1 * right.getValue();
                                } else {
                                    // hitwall
                                    rightUtil += 0.1 * current.getValue();
                                }

                                // if left
                                if (r - 1 >= 0) {
                                    State left = stateList[r - 1][c];
                                    rightUtil += 0.1 * left.getValue();
                                } else {
                                    // outside or hitwall
                                    rightUtil += 0.1 * current.getValue();
                                }
                            } else {
                                // hit wall that is boundary line
                                rightUtil += 0.8 * current.getValue();
                                // if right
                                if (r + 1 < row_n) {
                                    State right = stateList[r + 1][c];
                                    rightUtil += 0.1 * right.getValue();
                                } else {
                                    // hitwall
                                    rightUtil += 0.1 * current.getValue();
                                }

                                // if left
                                if (r - 1 >= 0) {
                                    State left = stateList[r - 1][c];
                                    rightUtil += 0.1 * left.getValue();
                                } else {
                                    // outside or hitwall
                                    rightUtil += 0.1 * current.getValue();
                                }
                            }
                            // end of right ---

                            // getting the max value out of every sum of expected utility
                            List<Double> list = new ArrayList<Double>();
                            list.add(upUtil);
                            list.add(downUtil);
                            list.add(leftUtil);
                            list.add(rightUtil);
                            double maxExpect = Collections.max(list, null);

                            //calculate current state value
                            double stateUtil = current.getReward() + GAMMA * maxExpect;

                            
                            double value = stateUtil;
                            //check if it is better
                            if (value > old_value || old_value == 0) {
                                new_value = value;
                            } else {
                                new_value = old_value;
                            }

                        } // end of if
                         

                        stateList[r][c].setValue(new_value);

                        //check if the the abs of the change of value is bigger than the current iteration biggest_change
                        //this is use for calculating the torrlance for exiting the function
                        if (Math.abs(old_value - current.getValue()) > biggest_change) {
                            biggest_change = Math.abs(old_value - current.getValue());
                        }

                    } // end if contain
                } // end for c
            } // end for r

            // write to text to keep track of every loop;
            CreateFile(ConstructValue(iteration), valueIterationFile);
            ArrayList<Double> data = new ArrayList<Double>();
            for (int w = 0; w < row_n; w++) {
                for (int x = 0; x < col_n; x++) {
                    data.add(stateList[w][x].getValue());
                }
            }
            DataList.add(data);


            //check if the difference of the biggest_change in a loop satifiy the require torrance 
            double error = (SMALL_ENOUGH * (1 - GAMMA)) / GAMMA;
            if (biggest_change < error && biggest_change != 0) {
                break;
            }

            iteration += 1;
        } // end of while true

    }// end of VI

    public static void GetOptimalPolicy() {

        // generate policy
        for (int r = 0; r < row_n; r++) {
            for (int c = 0; c < col_n; c++) {
                //values use to compare which is the optimal value 
                double downValue = -100.0;
                double upValue = -100.0;
                double leftValue = -100.0;
                double rightValue = -100.0;

                //get current state
                State current = stateList[r][c];
                if (current.iswall == false) {
                    //use the transition model to know where this state can go
                    String[] actions = current.getActions();

                    //check if current action is available in the state
                    boolean containsU = Arrays.stream(actions).anyMatch("U"::equals);
                    boolean containsD = Arrays.stream(actions).anyMatch("D"::equals);
                    boolean containsL = Arrays.stream(actions).anyMatch("L"::equals);
                    boolean containsR = Arrays.stream(actions).anyMatch("R"::equals);

                    if (containsD) {// default if not overide means that it hits a wall and turns opposite direction
                        downValue = stateList[r + 1][c].getValue();
                    }
                    if (containsU) {
                        upValue = stateList[r - 1][c].getValue();
                    }
                    if (containsL) {
                        leftValue = stateList[r][c - 1].getValue();
                    }
                    if (containsR) {
                        rightValue = stateList[r][c + 1].getValue();
                    }

                    //getting the best action base on all the values to decide which 
                    //state to go next
                    List<Double> list = new ArrayList<Double>();
                    list.add(upValue);
                    list.add(downValue);
                    list.add(leftValue);
                    list.add(rightValue);
                    double best = Collections.max(list, null);

                    //assign the next state to the current box of the policyList
                    if (best == leftValue) {
                        policyList[r][c] = stateList[r][c - 1];
                    } else if (best == upValue) {
                        policyList[r][c] = stateList[r - 1][c];
                    } else if (best == downValue) {
                        policyList[r][c] = stateList[r + 1][c];
                    } else if (best == rightValue) {
                        policyList[r][c] = stateList[r][c + 1];
                    }
                }
            }
        }
        // ---

        // end of generate policy

        
        // ----

    }// OP2
    
    public static void GetOptimalPolicyPath(String filename) {

        //this function is use to generate a path to see where the agent can go using the updated values
        //from the starting point of 3 , 2
        int nonemptybox = row_n * col_n - wall_n;//agent will walk the length of row* length of col - the number of walls steps.
        policy = new State[nonemptybox];// this policy is just a array storing the path of the agent.
        int startingRow = 3;
        int startingCol = 2;
        String facing = "U";
        State init = stateList[startingRow][startingCol];
        String[] actions = init.getActions();
        policy[0] = init;
        for (int i = 1; i < nonemptybox; i++) {

            // ---start
            if (actions.length > 0) {// not a wall

                double biggestValue = -1000.00;
                State actionTake = new State();

                //checking if action available to take
                boolean containsU = Arrays.stream(actions).anyMatch("U"::equals);
                boolean containsD = Arrays.stream(actions).anyMatch("D"::equals);
                boolean containsL = Arrays.stream(actions).anyMatch("L"::equals);
                boolean containsR = Arrays.stream(actions).anyMatch("R"::equals);
                //checking where the agent is facing to get 3 actions to move to
                //and taking the best value action as the choice
                
                if (facing == "U") {// when facing up it cannot choose down.
                    // default if all ULR cannot go
                    if (containsD) {// default if not overide means that it hits a wall and turns opposite direction
                        actionTake = stateList[startingRow + 1][startingCol];
                        facing = "D";
                    }
                    // if exist , overide down
                    if (containsU) {
                        State up = stateList[startingRow - 1][startingCol];
                        if (up.getValue() > biggestValue) {
                            biggestValue = up.getValue();
                            actionTake = up;
                            facing = "U";
                        }
                    }
                    if (containsL) {
                        State left = stateList[startingRow][startingCol - 1];
                        if (left.getValue() > biggestValue) {
                            biggestValue = left.getValue();
                            actionTake = left;
                            facing = "L";
                        }
                    }
                    if (containsR) {
                        State right = stateList[startingRow][startingCol + 1];
                        if (right.getValue() > biggestValue) {
                            biggestValue = right.getValue();
                            actionTake = right;
                            facing = "R";
                        }
                    }
                    policy[i] = actionTake;
                    actions = actionTake.getActions();
                    startingCol = actionTake.getCol();
                    startingRow = actionTake.getRow();

                } else if (facing == "D") {
                    // default if all ULR cannot go
                    if (containsU) {// default if not overide means that it hits a wall and turns opposite direction
                        actionTake = stateList[startingRow - 1][startingCol];
                        facing = "U";
                    }

                    // if exist , overide
                    if (containsD) {
                        State down = stateList[startingRow + 1][startingCol];
                        if (down.getValue() > biggestValue) {
                            biggestValue = down.getValue();
                            actionTake = down;
                            facing = "D";
                        }
                    }

                    if (containsL) {
                        State left = stateList[startingRow][startingCol - 1];
                        if (left.getValue() > biggestValue) {
                            biggestValue = left.getValue();
                            actionTake = left;
                            facing = "L";
                        }
                    }

                    if (containsR) {
                        State right = stateList[startingRow][startingCol + 1];
                        if (right.getValue() > biggestValue) {
                            biggestValue = right.getValue();
                            actionTake = right;
                            facing = "R";
                        }
                    }

                    policy[i] = actionTake;
                    actions = actionTake.getActions();
                    startingCol = actionTake.getCol();
                    startingRow = actionTake.getRow();

                } else if (facing == "L") {
                    if (containsR) {// default if not overide means that it hits a wall and turns opposite direction
                        actionTake = stateList[startingRow][startingCol + 1];
                        facing = "R";
                    }

                    // if exist , overide
                    if (containsL) {
                        State left = stateList[startingRow][startingCol - 1];
                        if (left.getValue() > biggestValue) {
                            biggestValue = left.getValue();
                            actionTake = left;
                            facing = "L";
                        }
                    }

                    if (containsD) {
                        State down = stateList[startingRow + 1][startingCol];
                        if (down.getValue() > biggestValue) {
                            biggestValue = down.getValue();
                            actionTake = down;
                            facing = "D";
                        }
                    }

                    if (containsU) {
                        State up = stateList[startingRow - 1][startingCol];
                        if (up.getValue() > biggestValue) {
                            biggestValue = up.getValue();
                            actionTake = up;
                            facing = "U";
                        }
                    }

                    policy[i] = actionTake;
                    actions = actionTake.getActions();
                    startingCol = actionTake.getCol();
                    startingRow = actionTake.getRow();

                } else if (facing == "R") {
                    if (containsL) {// default if not overide means that it hits a wall and turns opposite direction
                        actionTake = stateList[startingRow][startingCol - 1];
                        facing = "L";
                    }

                    if (containsR) {
                        State right = stateList[startingRow][startingCol + 1];
                        if (right.getValue() > biggestValue) {
                            biggestValue = right.getValue();
                            actionTake = right;
                            facing = "R";
                        }
                    }
                    if (containsU) {
                        State up = stateList[startingRow - 1][startingCol];
                        if (up.getValue() > biggestValue) {
                            biggestValue = up.getValue();
                            actionTake = up;
                            facing = "U";
                        }
                    }
                    if (containsD) {
                        State down = stateList[startingRow + 1][startingCol];
                        if (down.getValue() > biggestValue) {
                            biggestValue = down.getValue();
                            actionTake = down;
                            facing = "D";
                        }
                    }
                    policy[i] = actionTake;
                    actions = actionTake.getActions();
                    startingCol = actionTake.getCol();
                    startingRow = actionTake.getRow();

                }
            }
        }//end of forloop

        // --printing the policy path of agent to textfile
        String msg = "optimal policy path = ";
        double totalReward = 0;

        for (State s : policy) {

            msg += s.getRow() + "," + s.getCol() + "  -  ";
            totalReward += s.getReward();
        }
        msg += "\n" + "reward = " + totalReward;
        System.out.println(msg);

        CreateFile(msg, filename);
        // ----

    }

    public static void ConstructOptimalPolicy(String filename){
        // construct policy
        String policyStr = "";
        for (int r = 0; r < row_n; r++) {
            for (int c = 0; c < col_n; c++) {
                State move = policyList[r][c];
                if (move.iswall == false) {
                    int nextRow = move.getRow();
                    int nextCol = move.getCol();
                    if (r + 1 == nextRow && c == nextCol) {
                        policyStr += "v|";
                    } else if (r - 1 == nextRow && c == nextCol) {
                        policyStr += "^|";
                    } else if (r == nextRow && c + 1 == nextCol) {
                        policyStr += ">|";
                    } else {
                        policyStr += "<|";
                    }
                } else {
                    policyStr += "W|";
                }

            }
            policyStr += "\n";
        }

        System.out.println(policyStr);
        CreateFile(policyStr, filename);
        
    }
   
    public static void PrintOptimalValue(String filename) {
        // use to print all the best expected utility of all the state
        String in = "";
        for (int r = 0; r < row_n; r++) {
            for (int c = 0; c < col_n; c++) {
                in += "(" + r + "," + c + ")";
                State current = stateList[r][c];
                double factor = 1e4; // = 1 * 10^5 = 100000.
                double result = Math.round(current.getValue() * factor) / factor;
                if (result == 0) {
                    in += "0" + result + "000";
                } else {
                    in += result + "";
                }
                in += "\n";
            }

        }
        try {
            FileWriter myWriter = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(myWriter);
            bw.write(in);
            bw.close();
            // System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static void PolicyIteration(){
        PolicyEvaluation();
        //PolicyImprovement();
    }
    
    public static void PolicyEvaluation() {
        //this function calculate new value for each state base on formula
        //this is use for policy iteration

           // double maxChange = 0;
            for (int r = 0; r < row_n; r++) {
                for (int c = 0; c < col_n; c++) {

                    State current = stateList[r][c];
                    //double oldValue = current.getValue();
                    String[] actions = current.getActions();
                    int nextRow = current.getRow();
                    int nextCol = current.getCol();
                    String facing = "U";
                    double value = 0;
                    boolean containsU = Arrays.stream(actions).anyMatch("U"::equals);
                    boolean containsD = Arrays.stream(actions).anyMatch("D"::equals);
                    boolean containsL = Arrays.stream(actions).anyMatch("L"::equals);
                    boolean containsR = Arrays.stream(actions).anyMatch("R"::equals);
                    if (r + 1 == nextRow && c == nextCol) {
                        facing = "D";
                        // D is optimal path, R and L is sub
                        // for D
                        if (containsD) {
                            value += 0.8 * (stateList[r + 1][c].getReward() + GAMMA * stateList[r + 1][c].getValue());
                        } else {
                            // no up, set itself
                            value += 0.8 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }
                        // for L
                        if (containsL) {
                            value += 0.1 * (stateList[r][c - 1].getReward() + GAMMA * stateList[r][c - 1].getValue());
                        } else {
                            // else hit wall remains itself
                            value += 0.1 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }

                        // for R
                        if (containsR) {
                            value += 0.1 * (stateList[r][c + 1].getReward() + GAMMA * stateList[r][c + 1].getValue());
                        } else {
                            // else hit wall remains itself
                            value += 0.1 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }

                    } else if (r - 1 == nextRow && c == nextCol) {
                        facing = "U";
                        // U is optimal path, R and L is sub
                        if (containsU) {
                            value += 0.8 * (stateList[r - 1][c].getReward() + GAMMA * stateList[r - 1][c].getValue());
                        } else {
                            value += 0.8 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }
                        // for L
                        if (containsL) {
                            value += 0.1 * (stateList[r][c - 1].getReward() + GAMMA * stateList[r][c - 1].getValue());
                        } else {
                            // else hit wall remains itself
                            value += 0.1 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }

                        // for R
                        if (containsR) {
                            value += 0.1 * (stateList[r][c + 1].getReward() + GAMMA * stateList[r][c + 1].getValue());
                        } else {
                            // else hit wall remains itself
                            value += 0.1 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }

                    } else if (r == nextRow && c + 1 == nextCol) {
                        facing = "R";
                        // R is optimal path, U and D is sub

                        if (containsR) {
                            value += 0.8 * (stateList[r][c + 1].getReward() + GAMMA * stateList[r][c + 1].getValue());
                        } else {
                            // else hit wall remains itself
                            value += 0.8 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }
                        if (containsU) {
                            value += 0.1 * (stateList[r - 1][c].getReward() + GAMMA * stateList[r - 1][c].getValue());
                        } else {
                            value += 0.1 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }
                        if (containsD) {
                            value += 0.1 * (stateList[r + 1][c].getReward() + GAMMA * stateList[r + 1][c].getValue());
                        } else {
                            // no up, set itself
                            value += 0.1 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }

                    } else {
                        facing = "L";
                        // L is optimal path, U and D is sub

                        if (containsL) {
                            value += 0.8 * (stateList[r][c - 1].getReward() + GAMMA * stateList[r][c - 1].getValue());
                        } else {
                            // else hit wall remains itself
                            value += 0.8 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }
                        if (containsU) {
                            value += 0.1 * (stateList[r - 1][c].getReward() + GAMMA * stateList[r - 1][c].getValue());
                        } else {
                            value += 0.1 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }
                        if (containsD) {
                            value += 0.1 * (stateList[r + 1][c].getReward() + GAMMA * stateList[r + 1][c].getValue());
                        } else {
                            // no up, set itself
                            value += 0.1 * (stateList[r][c].getReward() + GAMMA * stateList[r][c].getValue());
                        }
                    }

                    stateList[r][c].setValue(value);
                    //maxChange = Math.max(maxChange, Math.abs(oldValue - value));
                }
            }


            PolicyImprovement();
  

    }// end of PE()
    
    public static void PolicyImprovement() {
       //check if the policy error is close enought to be a optimal solution
        double maxChange = 0;
       
        // generate policy
        for (int r = 0; r < row_n; r++) {
            for (int c = 0; c < col_n; c++) {
                double downValue = -100.0;
                double upValue = -100.0;
                double leftValue = -100.0;
                double rightValue = -100.0;
                State temp = policyList[r][c];
                State current = stateList[r][c];
                double old_v = temp.getValue();
                double new_v = 0;
                if (current.iswall == false) {
                    String[] actions = current.getActions();
                    boolean containsU = Arrays.stream(actions).anyMatch("U"::equals);
                    boolean containsD = Arrays.stream(actions).anyMatch("D"::equals);
                    boolean containsL = Arrays.stream(actions).anyMatch("L"::equals);
                    boolean containsR = Arrays.stream(actions).anyMatch("R"::equals);
                    
                    //get all available action values to compare
                    if (containsD) {// default if not overide means that it hits a wall and turns opposite direction
                        downValue = stateList[r + 1][c].getValue();
                    }
                    if (containsU) {
                        upValue = stateList[r - 1][c].getValue();
                    }
                    if (containsL) {
                        leftValue = stateList[r][c - 1].getValue();
                    }
                    if (containsR) {
                        rightValue = stateList[r][c + 1].getValue();
                    }
                    //get the best value to determin best action
                    List<Double> list = new ArrayList<Double>();
                    list.add(upValue);
                    list.add(downValue);
                    list.add(leftValue);
                    list.add(rightValue);
                    double best = Collections.max(list, null);
                    new_v= best;
                    if (best == leftValue) {
                        policyList[r][c] = stateList[r][c - 1];
                    } else if (best == upValue) {
                        policyList[r][c] = stateList[r - 1][c];
                    } else if (best == downValue) {
                        policyList[r][c] = stateList[r + 1][c];
                    } else if (best == rightValue) {
                        policyList[r][c] = stateList[r][c + 1];
                    }
                    //max change is use to monitor the torrance rate to exist function
                    maxChange = Math.max(maxChange, Math.abs(old_v - new_v));
                }
            }
        } // end of gen policy
        beforeApply = maxChange;
        
        //check if torrance rate is small enought with maxCHange, else do policyEvaluation again
        double error = (SMALL_ENOUGH * (1 - GAMMA)) / GAMMA;
        CreateFile("\n iteration "+policyIterationN+","+maxChange,policyIterationChart);
        if (maxChange > error && maxChange != 0) {
            
            // write values to text for the iteration tracking;
            CreateFile(ConstructValue(policyIterationN), policyIterationFile);
            ArrayList<Double> data = new ArrayList<Double>();
            for (int w = 0; w < row_n; w++) {
                for (int x = 0; x < col_n; x++) {
                    data.add(stateList[w][x].getValue());
                }
            }
            DataList.add(data);
            policyIterationN++;

            //rerun evaluation
            PolicyEvaluation();
        }else{
            //torrance met
            System.out.println("num of iteration="+policyIterationN);
            ConstructOptimalPolicy(policyIterationOPFile);
            GetOptimalPolicyPath(policyIterationOPPathFile);
        }
        
    }

    public static void InitialPolicyRandom() {
        //generate a random policy base on available transition model actions
        //not the best/optimal policy

        Random rand = new Random();
        for (int r = 0; r < row_n; r++) {
            for (int c = 0; c < col_n; c++) {
                State current = policyList[r][c];
                if (current.getIsWall() == false) {
                    // choose a random action and state for the policy
                    String[] action = current.getActions();
                    State next = null;

                    while (next == null) {
                        int randomaction = rand.nextInt((action.length - 1 - 0) + 1) + 0;

                        String strAction = action[randomaction];
                        if (strAction == "U") {
                            next = stateList[r - 1][c];
                        } else if (strAction == "D") {
                            next = stateList[r + 1][c];
                        } else if (strAction == "L") {
                            next = stateList[r][c - 1];
                        } else if (strAction == "R") {
                            next = stateList[r][c + 1];
                        }
                    }
                    policyList[r][c] = next;
                } // end if
            } // end for c
        } // end for r

    }

    public static String ConstructValue(int iteration) {
        //use to construct text file for every state utility
        String map = "\n iteration " + iteration + ",";

        for (int r = 0; r < row_n; r++) {
            for (int c = 0; c < col_n; c++) {

                // map += "(" + r + "," + c + "):";
                double reward = stateList[r][c].getValue();
                map += reward + ",";
            }
            // map += "\n";
        }
        // System.out.println(map);

        return map;

    }

    public static void CreateFile(String in, String filename) {
        //write to textfile
        try {
            FileWriter myWriter = new FileWriter(filename, true);
            BufferedWriter bw = new BufferedWriter(myWriter);
            bw.write(in);
            bw.close();
            // System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    
}
