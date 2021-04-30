import java.util.ArrayList;
import java.util.Arrays;

public class MonteCarloTreeSearch {


    public String findNextMove(String[][] board, int playerColor) {
        // define an end time which will act as a terminating condition
        World world = new World();
        Node rootNode = new Node(board, null, playerColor);

        for (int i = 0; i < 1000; i++) {
            //				if (playerColor != myColor) {
            //					Node promisingNode = selec
            //				}
            //TODO What should I do if I am opponent?
            Node promisingNode = selectPromisingNode(rootNode);
            if (!world.terminalState(promisingNode.getBoard())) {
                expandNode(promisingNode, playerColor);
            }
            Node nodeToExplore = promisingNode;
            if (promisingNode.getChildren().size() > 0) {
                nodeToExplore = promisingNode.getRandomChild();
            }
            double playoutResult = simulateRandomPlayout(nodeToExplore);
            backPropagation(nodeToExplore, playoutResult);
        }

        Node bestNode = rootNode.getBestChild();

        return findMoveFromBoard(rootNode.getBoard(), bestNode.getBoard());
    }


    public String findMoveFromBoard(String[][] oldBoard, String[][] newBoard) {
        String oldpos = "";
        String newpos = "";
        String move;
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 5; col++) {
                if (oldBoard[row][col] != newBoard[row][col]) {
                    if (oldBoard[row][col] == " ") {
                        oldpos = Integer.toString(row) + Integer.toString(col);
                    } else {
                        newpos = Integer.toString(row) + Integer.toString(col);
                    }
                }
            }
        }
        move = oldpos + newpos;
        System.out.print("Move is: " + move);
        System.out.println("Old Board: ");
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 5; col++) {
                System.out.print(oldBoard[row][col]);
            }
            System.out.print("\n");
        }
        System.out.println("New Board: ");
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 5; col++) {
                System.out.print(newBoard[row][col]);
            }
            System.out.print("\n");
        }
        System.out.println("*******");
        return move;
    }

    public void backPropagation(Node nodeToExplore, double totalValue) {
        World world = new World();
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.incVisitCount();
            if (tempNode.getPlayerColor() == world.myColor) {
                tempNode.updateNodeValue(totalValue);
            }
            tempNode = tempNode.getParent();
        }
    }

    public double simulateRandomPlayout(Node node) {
        World world = new World();
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
            int index = (int) (Math.random() * world.availableMoves.size());
            rndboard = moveOnBoard(rndboard, world.availableMoves.get(index));
        }
        return world.evaluate(rndboard);
    }

    public Node selectPromisingNode(Node rootNode) {
        World world = new World();
        Node node = rootNode;
        UCT uct = new UCT();
        while (node.getChildren().size() != 0) {
			/*
			If opponent is playing, select a random node (child)
			Else we find the best node with UCT
			 */
            if (node.getPlayerColor() != world.myColor) {
                node = node.getRandomChild();
            } else {
                node = uct.findBestNodeWithUCT(node);
            }
        }
        return node;
    }


    public void expandNode(Node node, int myColor) {
        World world = new World();
        ArrayList<String> availableMoves;
        int newColor;

        if (myColor == 0) {    // I am the white player
            availableMoves = world.whiteMoves2(node.getBoard());
            newColor = 1;
        } else {            // I am the black player
            availableMoves = world.blackMoves2(node.getBoard());
            newColor = 0;
        }

        for (int i = 0; i < availableMoves.size(); i++) {
            Node newNode = new Node(moveOnBoard(node.getBoard(), availableMoves.get(i)), node, newColor);
            node.addChild(newNode);
        }

    }

    public String[][] moveOnBoard(String[][] tmpboard, String move) {
        World world = new World();

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