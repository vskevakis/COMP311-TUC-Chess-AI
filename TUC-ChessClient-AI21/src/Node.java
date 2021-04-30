import java.util.ArrayList;

public class Node {
    public String[][] ourboard;
    public int playerColor;
    public  int visitCount;
    public  double nodeValue;
    public Node parent;
    public ArrayList<Node> childArray;

    public Node(String[][] board, Node parentNode, int playerColor){
        this.childArray = new ArrayList<Node>();
        this.ourboard = board;
        this.parent = parentNode;
        this.playerColor = playerColor;
        this.visitCount = 0;
        this.nodeValue = 0;
    }

    public void incVisitCount() {
        this.visitCount += 1;
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

    public void updateNodeValue(double newValue) {
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