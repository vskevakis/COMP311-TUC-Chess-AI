import java.io.IOException;

public class Minmax
{

    public String minmax(World world, int depth, boolean maxPlayer) {

        if (depth == 0 || terminalState(world)) {

        }
        if (maxPlayer) {
            double value = double.MAX_VALUE;
            for (int i = 0; world.availableMoves.length(); i++) {
                double tempvalue = evaluate(world, world.availableMoves[i]);
                if (world.board[world.availableMoves[i].charAt(0)][world.availableMoves[i].charAt(1)] == "BP" && col = 6)  {
                    world
                }
                else if (world.board[world.availableMoves[i].charAt(0)][world.availableMoves[i].charAt(1)] == "WP" && col = 0)  {
                    return 1;
                }
            }
        }

    }

    /* Checking if we are on terminal state */
    public boolean terminalState(World world) {
        String board[][] = world.board;

        int kings = 0;
        int other = 0;
        for (int row = 0; row < 7; row++) {
            for (int col = 0; col < 5; col++) {
                if (board[row][col].equals("BK") || board[row][col].equals("WK")) {
                    kings++;
                }else if (!board[row][col].equals(" ") && !board[row][col].equals("P")) {
                    other++;
                }
            }
        }
        if ((kings == 2 && other == 0) || kings <= 1) {
            return true;
        }
        else {
            return false;
        }
    }

    /* Basic move evaluation based on points
    * String move = "3758"
    * row/col --> row/col
    * */
    public double evaluate(World world, String move) {
        int col = move.charAt(2);
        int row = move.charAt(3);

        if (world.board[move.charAt(0)][move.charAt(1)] == "BP" && col = 6)  {
            return 1;
        }
        else if (world.board[move.charAt(0)][move.charAt(1)] == "WP" && col = 0)  {
            return 1;
        }
        else if (world.board[col][row] == " ") {
            return 0;
        }
        else if (world.board[col][row] == "P") {
            return 0.7;
        }
        else if (world.board[col][row].charAt(1) == "P") {
            return 1;
        }
        else if (world.board[col][row].charAt(1) == "K") {
            return 7;
        }
        else if (world.board[col][row].charAt(1) == "R") {
            return 3;
        }
    }
}