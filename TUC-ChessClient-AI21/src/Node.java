import java.util.ArrayList;

public class Node {
    private String[][] ourboard;
    private int playerColor;
    private  int visitCount;
    private  double nodeValue;
    private Node parent;
    private ArrayList<Node> childArray;

    public Node(String[][] board, Node parentNode, int playerColor){
        this.childArray = new ArrayList<Node>();
        this.ourboard = board;
//        System.out.println("parentNode  "+parentNode);
        this.parent = parentNode;
//        System.out.println("thisparentNode  "+this.parent);
        this.playerColor = playerColor;
        this.visitCount = 0;
        this.nodeValue = 0;
    }

    public Node clone(Node currentNode){
        Node newnode = new Node(currentNode.getBoard(),currentNode.getParent(),currentNode.getPlayerColor());
        newnode.setVisitCount(currentNode.getVisitCount());
        newnode.setNodeValue(currentNode.getNodeValue());
        if (currentNode.getChildren()==null){
            return newnode;
        }
        for (int i=0;i<currentNode.getChildren().size();i++){
            newnode.addChild(currentNode.getChildren().get(i));
        }
        return newnode;
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
//        System.out.println("oldthis.nodeValue"+this.nodeValue);
        this.nodeValue = newValue;
//        System.out.println("nodeValue"+this.nodeValue);
    }

    public void updateNodeValue(double newValue) {
//        System.out.println("oldthis.nodeValue"+this.nodeValue);
        this.nodeValue += (newValue - this.nodeValue) / this.visitCount;

        System.out.println("nodeValue"+this.nodeValue);
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
        System.out.println("this.childArray.size()"+this.childArray.size());
        for (int i = 0; i < this.childArray.size(); i++) {
            if (this.childArray.get(i).getNodeValue() > maxValue) {
                maxValue = this.childArray.get(i).getNodeValue();
                selectedNode = this.childArray.get(i);
            }
        }
        return selectedNode;
    }
}