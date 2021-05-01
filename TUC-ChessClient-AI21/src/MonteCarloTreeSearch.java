import java.util.ArrayList;
import java.util.Arrays;

public class MonteCarloTreeSearch {


    public String findNextMove(String[][] board, int playerColor,World world) {
        UCT uct = new UCT();
        // define an end time which will act as a terminating condition
        Node rootNode = new Node(board, null, playerColor, null);

        for (int i = 0; i < 2000; i++) {
            //TODO What should I do if I am opponent? nothing we good just chill and let maxi int

//            Node promisingNode = selectPromisingNode(rootNode,world);//.clone(selectPromisingNode(rootNode,world));

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
                    promisingNode = promisingNode.getRandomChild();
                } else {
                    promisingNode = uct.findBestNodeWithUCT(promisingNode);
                }
            }
//        System.out.println("count"+count );


//            rootNode.printNode();
            if (!world.terminalState(promisingNode.getBoard())) {
//                promisingNode = expandNode(promisingNode, playerColor,world);//.clone(expandNode(promisingNode, playerColor,world));
                /* expandNode */
                ArrayList<String> availableMoves;
                int newColor;

                if (promisingNode.getPlayerColor() == 0) {    // I am the white player
                    availableMoves = world.whiteMoves2(promisingNode.getBoard());
                    newColor = 1;
                } else {            // I am the black player
                    availableMoves = world.blackMoves2(promisingNode.getBoard());
                    newColor = 0;
                }
//        System.out.println("node"+node);
                for (int j = 0; j < availableMoves.size(); j++) {
                    Node newNode = new Node(moveOnBoard(promisingNode.getBoard(), availableMoves.get(j),world), promisingNode, newColor, availableMoves.get(j));
                    promisingNode.addChild(newNode);
//            System.out.println("node    : "+node+"    childnode   : "+newNode +"   :::: "+ node.getChildren());
                }
            }
//            promisingNode.printNode();
//            rootNode.printNode();

            Node nodeToExplore = promisingNode;//.clone(promisingNode);
            if (promisingNode.getChildren().size() > 0) {
                nodeToExplore = promisingNode.getRandomChild();
            }
            double playoutResult = simulateRandomPlayout(nodeToExplore,world);
//            rootNode = backPropagation(nodeToExplore, playoutResult,world).clone(backPropagation(nodeToExplore, playoutResult,world));

            /* Backpropagation */
            Node tempNode = nodeToExplore;//.clone(nodeToExplore);
            while (tempNode != null) {
                tempNode.incVisitCount();
                if (tempNode.getPlayerColor() == world.myColor) {
                    tempNode.updateNodeValue(playoutResult);
                }

                tempNode = tempNode.getParent(); //.clone(tempNode.getParent());//no link
            }

//            rootNode.printNode();

        }

        Node bestNode = rootNode.getBestChild();//.clone(rootNode.getBestChild());

//        return findMoveFromBoard(rootNode.getBoard(), bestNode.getBoard(), world.myColor);
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
//                    if (newBoard[row][col] == "WP" || oldBoard[row][col] == "BP") {
//                        if (playerColor == 0 && row == 0)
//                            newpos = Integer.toString(row) + Integer.toString(col);
//                        else if (playerColor == 1 && row == 6)
//                            newpos = Integer.toString(row) + Integer.toString(col);
//                    }
                }
            }
        }
        move = oldpos + newpos;
        return move;
    }

//    public Node backPropagation(Node nodeToExplore, double totalValue,World world) {
//        Node tempNode = nodeToExplore.clone(nodeToExplore);
//        while (tempNode != null) {
//            tempNode.incVisitCount();
//            if (tempNode.getPlayerColor() == world.myColor) {
//                tempNode.updateNodeValue(totalValue);
//            }
//            if (tempNode.getParent() != null)
//                tempNode = tempNode.getParent().clone(tempNode.getParent());//no link
//            else
//                break;
//        }
//        return tempNode;
//    }

    public double simulateRandomPlayout(Node node,World world) {
//        World world = new World();
        String[][] rndboard;
        rndboard = node.getBoard();
//		int boardStatus = node.getBoard().checkStatus();
//		if (boardStatus == opponent) {
//			tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
//			return boardStatus;
//		}
        //TODO Check if above is needed
        int player = node.getPlayerColor();
        while (!world.terminalState(rndboard)) {
            if (player == 0) {    // I am the white player
                world.availableMoves = world.whiteMoves2(rndboard);
                player = 1;
            } else {            // I am the black player
                world.availableMoves = world.blackMoves2(rndboard);
                player = 0;
            }
            if (world.availableMoves.size()==0){
                break;
            }
            int index = (int) (Math.random() * world.availableMoves.size());
            rndboard = moveOnBoard(rndboard, world.availableMoves.get(index),world);
        }
        return world.evaluate(rndboard);
    }

//    public Node selectPromisingNode(Node rootNode,World world) {
//        Node node = rootNode.clone(rootNode);
//        UCT uct = new UCT();
//        int count=0;
//        if (node.getChildren().size() == 0) {
//            return rootNode;
//        }
//        while (node.getChildren().size() != 0) {
//            count++;
//			/*
//			If opponent is playing, select a random node (child)
//			Else we find the best node with UCT
//			 */
//            System.out.println("player Color is: " + node.getPlayerColor() + "World color is: "+ world.myColor);
//            if (node.getPlayerColor() != world.myColor) {
//                node = node.getRandomChild();
//            } else {
//                node = uct.findBestNodeWithUCT(node);
//            }
//        }
////        System.out.println("count"+count );
//        return node;
//    }


//    public Node expandNode(Node node, int myColor,World world) {
////        World world = new World();
//        ArrayList<String> availableMoves;
//        int newColor;
//
//        if (myColor == 0) {    // I am the white player
//            availableMoves = world.whiteMoves2(node.getBoard());
//            newColor = 1;
//        } else {            // I am the black player
//            availableMoves = world.blackMoves2(node.getBoard());
//            newColor = 0;
//        }
////        System.out.println("node"+node);
//        for (int i = 0; i < availableMoves.size(); i++) {
//            Node newNode = new Node(moveOnBoard(node.getBoard(), availableMoves.get(i),world), node, newColor);
//            node.addChild(newNode);
////            System.out.println("node    : "+node+"    childnode   : "+newNode +"   :::: "+ node.getChildren());
//        }
//        return node;
//
//    }

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