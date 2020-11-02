import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: YuQicode
 * @email 441765930@qq.com
 * @Data:2020/10/22
 */

public class Station {
    static int count=0;  //记录共有几条路线
    private int id;
    private String name;
    private Set<String> lines;
    private ArrayList<Integer> edges;
    private boolean visited;
    private int lastPoint;

    Station(int id, String name){
        this.id = id;        //站点ID
        this.name = name;     //站点名
        this.lines = new HashSet<>();  //记录站点属于的路线，可能会同时属于好几条路线，即为换成车站
        this.edges = new ArrayList<>();   //记录相邻站点
        this.visited = false;   //判断是否已经访问过
        this.lastPoint = 0;     //再BFS时记录上一站点的ID
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean checkLine(String line) {
        return lines.contains(line);
    }

    public Set<String> getLines() {
        return lines;
    }

    public void addLines(String line) {
        lines.add(line);
    }

    public ArrayList<Integer> getEdges() {
        return edges;
    }

    public void addEdges(int id) {
        edges.add(id);
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public int getLastPoint() {
        return lastPoint;
    }

    public void setLastPoint(int lastPoint) {
        this.lastPoint = lastPoint;
    }

}
