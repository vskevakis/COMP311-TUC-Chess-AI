import java.util.ArrayList;

public class Node {
    private String[][] ourboard;
    private int playerColor;
    private  int visitCount;
    private  double nodeValue;
    private Node parent;
    private ArrayList<Node> childArray;
    private String move;

    public Node(String[][] board, Node parentNode, int playerColor, String move){
        this.childArray = new ArrayList<Node>();
        this.ourboard = board;
        this.parent = parentNode;
        this.playerColor = playerColor;
        this.visitCount = 0;
        this.nodeValue = 0;
        this.move = move;
    }

    public void printNode() {
        System.out.println("****");
        System.out.println("Node: " + this);
        System.out.println("Visit Count: " + this.visitCount);
        System.out.println("Children: " + this.childArray);
        System.out.println("Parent: " + this.parent);
        System.out.println("Value: " + this.nodeValue);
        System.out.println("****");
    }

    public String getMove() {
        return this.move;
    }

    public void incVisitCount() {
        this.visitCount += 1;
    }

    public void setVisitCount(int visit) {
        this.visitCount = visit;
    }

    public void addChild(Node childNode) {
        this.childArray.add(childNode);
    }

    public ArrayList<Node> getChildren() {
        return this.childArray;
    }

    public Node getParent() {
        return this.parent;
    }

    public int getVisitCount() {
        return this.visitCount;
    }

    public void setNodeValue(double newValue) {
        this.nodeValue = newValue;
    }

    public void updateNodeValue(double newValue) {
//        System.out.println("oldthis.nodeValue"+this.nodeValue);
        this.nodeValue += (newValue - this.nodeValue) / this.visitCount;
    }

    public double getNodeValue() {
        return this.nodeValue;
    }

    public String[][] getBoard() {
        return this.ourboard;
    }

    public int getPlayerColor() {
        return this.playerColor;
    }

    public Node getRandomChild() {
        int index = (int)(Math.random() * this.getChildren().size());
        return this.getChildren().get(index);
    }

    public Node getBestChild() {
        double maxValue = Double.NEGATIVE_INFINITY;
        Node selectedNode = null;
        for (int i = 0; i < this.childArray.size(); i++) {
            if (this.childArray.get(i).getNodeValue() > maxValue) {
                maxValue = this.childArray.get(i).getNodeValue();
                selectedNode = this.childArray.get(i);
            }

        }

        return selectedNode;
    }
}