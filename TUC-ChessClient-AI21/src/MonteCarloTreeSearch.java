import java.util.ArrayList;
import java.util.Arrays;

public class MonteCarloTreeSearch {


    public String findNextMove(String[][] board, int playerColor,World world) {
        UCT uct = new UCT();
        // define an end time which will act as a terminating condition
        Node rootNode;
        if (playerColor==1){
            rootNode = new Node(board, null, 0, null);
        }else{
            rootNode = new Node(board, null, 1, null);
        }
        long time = System.currentTimeMillis();
//        for (int i = 0; i < 10000; i++) {
        while(System.currentTimeMillis()-time <5700){//57000

            /* selectPromissingNode */
            Node promisingNode = rootNode;//.clone(rootNode);
            int count=0;
            while (promisingNode.getChildren().size() != 0) {
                count++;
			/*
			If opponent is playing, select a random node (child)
			Else we find the best node with UCT
			 */
//                System.out.println("player Color is: " + promisingNode.getPlayerColor() + "World color is: "+ world.myColor);
                if (promisingNode.getPlayerColor() != world.myColor) {
                    Boolean flag=true;
                    ArrayList<String> availabM = null;
                    String move ;
                    String[][] tmpboard = null;
                    int [] moveInt = new int [4];
//                    System.out.println("bravailabM.size() "+ availabM.size()  + "   childern size   "+ promisingNode.getChildren().size());
                    for (int m=0;m<promisingNode.getChildren().size();m++){
                        move=promisingNode.getChildren().get(m).getMove();
                        for (int k = 0; k < move.length(); k++) {
                            moveInt[k] = Integer.parseInt(Character.toString(move.charAt(k)));
                        }
                        tmpboard =promisingNode.getBoard();
                        if (tmpboard[moveInt[2]][moveInt[3]].equals("WP") || tmpboard[moveInt[2]][moveInt[3]]=="BP" ||tmpboard[moveInt[2]][moveInt[3]]=="WR"||tmpboard[moveInt[2]][moveInt[3]]=="WK"||tmpboard[moveInt[2]][moveInt[3]]=="BR"||tmpboard[moveInt[2]][moveInt[3]]=="BK"){
                            promisingNode = promisingNode.getChildren().get(m);
//                            System.out.println("break break");
                            flag=false;
                            break;
                        }
                    }
                    if (flag){
                        promisingNode = promisingNode.getRandomChild();
                    }
                } else {

                    promisingNode = uct.findBestNodeWithUCT(promisingNode);
                }
            }

            if (!world.terminalState(promisingNode.getBoard())) {
                /* expandNode */
                ArrayList<String> availableMoves;
                int newColor;

                if (promisingNode.getPlayerColor() == 1) {    // I am the white player
                    availableMoves = world.whiteMoves2(promisingNode.getBoard());
                    newColor = 1;
                } else {            // I am the black player
                    availableMoves = world.blackMoves2(promisingNode.getBoard());
                    newColor = 0;
                }
                for (int j = 0; j < availableMoves.size(); j++) {
                    Node newNode = new Node(moveOnBoard(promisingNode.getBoard(), availableMoves.get(j),world), promisingNode, newColor, availableMoves.get(j));
                    promisingNode.addChild(newNode);
                }
            }
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildren().size() > 0) {
                nodeToExplore = promisingNode.getRandomChild();
            }
            double playoutResult = simulateRandomPlayout(nodeToExplore,world);

            Node tempNode = nodeToExplore;
            while (tempNode != null) {
                tempNode.incVisitCount();
                if (tempNode.getPlayerColor() != world.myColor) {
                    tempNode.updateNodeValue(playoutResult);
                }

                tempNode = tempNode.getParent();
            }

        }

        Node bestNode = rootNode.getBestChild();//.clone(rootNode.getBestChild());

        return bestNode.getMove();
    }



    public String findMoveFromBoard(String[][] oldBoard, String[][] newBoard, int playerColor) {
        String oldpos = "";
        String newpos = "";
        String move;
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 5; col++) {
                if (oldBoard[row][col] != newBoard[row][col]) {
                    if (newBoard[row][col] == " ") {
                        oldpos = Integer.toString(row) + Integer.toString(col);
                    }
                    else {
                        newpos = Integer.toString(row) + Integer.toString(col);
                    }
                }
            }
        }
        move = oldpos + newpos;
        return move;
    }


    public double simulateRandomPlayout(Node node,World world) {
        String[][] rndboard;
        rndboard = node.getBoard();
        ArrayList<String> availableMoves = null;
        int player = node.getPlayerColor();
        int counter=0;
        while (!world.terminalState(rndboard) || counter <20) {
            counter=counter +1;
            if (player == 0) {    // I am the white player
                availableMoves = world.whiteMoves2(rndboard);
                player = 1;
            } else {            // I am the black player
                availableMoves = world.blackMoves2(rndboard);
                player = 0;
            }
            if (availableMoves.size()==0){
                break;
            }
            int index = (int) (Math.random() * availableMoves.size());
            rndboard = moveOnBoard(rndboard, availableMoves.get(index),world);
        }
        return world.evaluate(rndboard);
    }


    public String[][] moveOnBoard(String[][] tmpboard, String move ,World world) {
//        World world = new World();

        String[][] board2 = new String[world.rows][world.columns];
        int[] moveInt = new int[4];
        for (int i = 0; i < world.rows; i++) {
            board2[i] = Arrays.copyOf(tmpboard[i], world.columns);
        }

        for (int j = 0; j < move.length(); j++) {
            moveInt[j] = Integer.parseInt(Character.toString(move.charAt(j)));
        }

        if ((tmpboard[moveInt[0]][moveInt[1]] == "WP" && moveInt[2] == 0) || (tmpboard[moveInt[0]][moveInt[1]] == "BP" && moveInt[2] == 6)) {
            board2[moveInt[0]][moveInt[1]] = " ";
            board2[moveInt[2]][moveInt[3]] = " ";
        } else {
            board2[moveInt[2]][moveInt[3]] = tmpboard[moveInt[0]][moveInt[1]];
            board2[moveInt[0]][moveInt[1]] = " ";
        }
        return board2;
    }
}