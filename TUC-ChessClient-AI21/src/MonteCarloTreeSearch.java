import java.util.ArrayList;
import java.util.Arrays;

public class MonteCarloTreeSearch {


    public String findNextMove(String[][] board, int playerColor,World world) {
        // define an end time which will act as a terminating condition
        Node rootNode = new Node(board, null, playerColor);
//        System.out.println(rootNode.getNodeValue());
        System.out.println(world.availableMoves);

        for (int i = 0; i < 1000; i++) {
            //				if (playerColor != myColor) {
            //					Node promisingNode = selec
            //				}
            //TODO What should I do if I am opponent? nothing we good just chill and let maxi int

//            System.out.println(rootNode.getNodeValue());
            Node promisingNode = selectPromisingNode(rootNode,world);//.clone(selectPromisingNode(rootNode,world));
//            System.out.println("promisingNode    :     "+promisingNode.getChildren().size());
            if (!world.terminalState(promisingNode.getBoard())) {
                expandNode(promisingNode, playerColor,world);
            }
//            System.out.println("promisingNode    after   :     "+promisingNode.getChildren().size());
//            System.out.println("rootNode.getChildren().size()"+rootNode.getChildren().size());
//            for (i=0;i<rootNode.getChildren().size();i++) {
//                if(rootNode.getChildren().get(i).getNodeValue()!=0){
//                    System.out.println("getNodeValu"+rootNode.getChildren().get(i).getNodeValue()+"getVisitCount"+rootNode.getChildren().get(i).getVisitCount()+"nodeid"+rootNode);
//                }
//            }
            Node nodeToExplore = promisingNode;//.clone(promisingNode);
            if (promisingNode.getChildren().size() > 0) {
                nodeToExplore = promisingNode.getRandomChild();
            }
//            System.out.println("promisingNode    :     "+nodeToExplore.getParent()+"    "+promisingNode+"           "+nodeToExplore);
            double playoutResult = simulateRandomPlayout(nodeToExplore,world);
//            System.out.println("playoutResult"+playoutResult);
            backPropagation(nodeToExplore, playoutResult,world);
//            for (i=0;i<rootNode.getChildren().size();i++) {
//                if(promisingNode.getChildren().get(i).getNodeValue()!=0){
//                    System.out.println("getNodeValu"+promisingNode.getChildren().get(i).getNodeValue()+"getVisitCount"+promisingNode.getChildren().get(i).getVisitCount()+"nodeid"+nodeToExplore);
//                }
//            }
        }

        Node bestNode = rootNode.getBestChild().clone(rootNode.getBestChild());

        return findMoveFromBoard(rootNode.getBoard(), bestNode.getBoard());
    }


    public String findMoveFromBoard(String[][] oldBoard, String[][] newBoard) {
        String oldpos = "";
        String newpos = "";
        String move;
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 5; col++) {
                if (oldBoard[row][col] != newBoard[row][col]) {
                    if (newBoard[row][col] == " ") {
                        oldpos = Integer.toString(row) + Integer.toString(col);
                    } else {
                        newpos = Integer.toString(row) + Integer.toString(col);
                    }
                }
            }
        }
        move = oldpos + newpos;
//        System.out.print("Move is: " + move);
//        System.out.println("Old Board: ");
//        for (int row = 0; row < 7; row++) {
//            for (int col = 0; col < 5; col++) {
//                System.out.print(oldBoard[row][col]);
//            }
//            System.out.print("\n");
//        }
//        System.out.println("New Board: ");
//        for (int row = 0; row < 7; row++) {
//            for (int col = 0; col < 5; col++) {
//                System.out.print(newBoard[row][col]);
//            }
//            System.out.print("\n");
//        }
//        System.out.println("*******");
        return move;
    }

    public void backPropagation(Node nodeToExplore, double totalValue,World world) {
//        World world = new World();
//        System.out.println("backPropagation:totalValue: "+totalValue);
        Node tempNode = nodeToExplore;//.clone(nodeToExplore);
//        System.out.println("backPropagation:tempNode: "+tempNode);
        while (tempNode != null) {
            tempNode.incVisitCount();
//            System.out.println("tempNode.getPlayerColor()   "+tempNode.getPlayerColor()+" world.myColor    "+world.myColor);
            if (tempNode.getPlayerColor() == world.myColor) {
                tempNode.updateNodeValue(totalValue);
                System.out.println("tempNode    "+tempNode+"    tempNode   getParent "+tempNode.getParent());
//                System.out.println("tempNode.getNodeValue();    "+tempNode.getNodeValue());
            }
//            if(tempNode.getParent()==null){
//            for (int i=0;i<tempNode.getChildren().size();i++) {
////                if(tempNode.getChildren().get(i).getNodeValue()!=0){
////                    System.out.println("getNodeValue    "+tempNode.getChildren().get(i).getNodeValue()+"getVisitCount"+tempNode.getChildren().get(i).getVisitCount()+"nodeid"+nodeToExplore);
////                }
//            }}
//            System.out.println("tempNodetempNode.getParent()   222 "+tempNode.getParent());
//            tempNode = tempNode.getParent();//no link
//            System.out.println("tempNodetempNo)   222 "+tempNode);
            if (tempNode.getParent()!=null){
                tempNode = tempNode.getParent();//no link
                System.out.println("tempNode  getChildren 222 "+tempNode.getChildren());
            }else {
                break;
            }

        }
    }

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

    public Node selectPromisingNode(Node rootNode,World world) {
//        World world = new World();
        Node node = rootNode.clone(rootNode);
        UCT uct = new UCT();
        int count=0;
//        System.out.println("selectPromisingNode"+node.getNodeValue());
        while (node.getChildren().size() != 0) {
            count++;
//            System.out.println("selectPromisingNode"+node.getNodeValue());
			/*
			If opponent is playing, select a random node (child)
			Else we find the best node with UCT
			 */
            if (node.getPlayerColor() != world.myColor) {
//                System.out.println("selectPromisingNode"+node.getNodeValue());
                node = node.getRandomChild();
            } else {
//                System.out.println("else");
                node = uct.findBestNodeWithUCT(node);
            }
        }
//        System.out.println("count"+count );
        return node;
    }


    public void expandNode(Node node, int myColor,World world) {
//        World world = new World();
        ArrayList<String> availableMoves;
        int newColor;

        if (myColor == 0) {    // I am the white player
            availableMoves = world.whiteMoves2(node.getBoard());
            newColor = 1;
        } else {            // I am the black player
            availableMoves = world.blackMoves2(node.getBoard());
            newColor = 0;
        }
//        System.out.println("node"+node);
        for (int i = 0; i < availableMoves.size(); i++) {
            Node newNode = new Node(moveOnBoard(node.getBoard(), availableMoves.get(i),world), node, newColor);
            node.addChild(newNode);
//            System.out.println("node    : "+node+"    childnode   : "+newNode +"   :::: "+ node.getChildren());
        }

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