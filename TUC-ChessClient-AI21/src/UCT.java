import java.util.Collections;
import java.util.Comparator;

public class UCT {
    public static double uctValue(
            int totalVisit, double nodeValue, int nodeVisit) {
        if (nodeVisit == 0) {
            return Double.MAX_VALUE;
        }
        return ((double) nodeValue)
                + 2 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static Node findBestNodeWithUCT(Node node) {

        int parentVisit = node.getVisitCount();
//        System.out.println("findUCT");
//        Collections.max(node.getChildren(),
//                Comparator.comparing(c -> uctValue(parentVisit, c.getNodeValue(), c.getVisitCount()))).printNode();

        return Collections.max(node.getChildren(),
                Comparator.comparing(c -> uctValue(parentVisit, c.getNodeValue(), c.getVisitCount())));
    }
}