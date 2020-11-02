import java.io.*;
import java.util.*;

/**
 * @Author: YuQicode
 * @email 441765930@qq.com
 * @Data:2020/10/26
 */

public class Subway {
    private static final int HASH_BASE = 100000;
    private static int stationCount = -1;  //记录站点的总数
    private static HashMap<String, Integer> stationMap=new HashMap<>(), lineStart=new HashMap<>();
    private static ArrayList<Station> stations=new ArrayList<>();  //记录各个站点的信息
    private static Map<Integer, String> edgeLines=new HashMap<>();   //
    private static String o=null;

    /**
     * 打印到文本文件中
     * @param data
     * @throws IOException
     */
    private static void printf(Object data) throws IOException{
        String text = String.valueOf(data);
        if (o!=null){
            BufferedWriter out = new BufferedWriter(new FileWriter(o,true));  //写在文件的末尾处
            out.write(text);
            System.out.print(text);
            out.close();
        }else {
            System.out.print(text);
        }
    }

    /**
     * 获取站点ID，每个站点ID唯一
     * @param stationName
     * @return
     */
    private static int getStationId(String stationName) {
        if(stationMap.containsKey(stationName)){
            return stationMap.get(stationName);
        }
        stationCount++;
        stationMap.put(stationName, stationCount);
        stations.add(new Station(stationCount, stationName));
        return stationCount;
    }

    /**
     * 用hash来唯一标识每一个条边
     * @param a
     * @param b
     * @return
     */
    private static int getEdgeHash(int a, int b){
        return a*HASH_BASE+b;
    }

    /**
     * 添加站点与站点之间的边
     * @param id1
     * @param id2
     * @param lineName
     */
    private static void addEdge(int id1,int id2,String lineName){
        stations.get(id1).addEdges(id2);
        stations.get(id2).addEdges(id1);
        edgeLines.put(getEdgeHash(id1,id2),lineName);
        edgeLines.put(getEdgeHash(id2,id1),lineName);
    }

    /**
     * 数据初始化
     * @param map
     */
    private static void data_init(String map){
        try(InputStreamReader isr = new InputStreamReader(new FileInputStream(map), "UTF-8");BufferedReader br=new BufferedReader(isr)) {
            String line;
            String lineName="";
            while ((line = br.readLine())!=null){
                String[] list = line.split(" ");
                if (list[0].contains("#")){
                    lineName = list[0].substring(1);
                    lineStart.put(lineName,getStationId(list[1]));
                    stations.get(getStationId(list[1])).addLines(lineName);
                    //printf(lineName+" ");
                }
                for (int i=1; i < list.length-1; i++){
                    int id1=getStationId(list[i]);
                    int id2=getStationId(list[i+1]);
                    stations.get(id2).addLines(lineName);
                    addEdge(id1, id2, lineName);
                }
            }
            Station.count=stationCount;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取指定地铁路线的所有站点
     * @param lineName
     * @throws IOException
     */
    private static void getLine(String lineName) throws IOException {
        if (!lineStart.containsKey(lineName)){
            printf("error：不存在这条路线。");
            return;
        }
        printf(lineName+"："+"\n");
        int start=lineStart.get(lineName);
        Queue<Integer> q = new LinkedList<>();
        q.offer(start);
        stations.get(start).setVisited(true);
        boolean first=true;
        while (!q.isEmpty()){
            int now=q.poll();
            Station nowStation= stations.get(now);
            if (first){
                printf(nowStation.getName());
                first=false;
            }else {
                printf("-->"+nowStation.getName());
            }
            for (int it:nowStation.getEdges()){
                Station tmp=stations.get(it);
                if (tmp.isVisited()) continue;
                if (tmp.checkLine(lineName)){
                    q.offer(it);
                    tmp.setVisited(true);
                }
            }
        }
    }

    /**
     * 利用Hash的唯一性，可以判断这条边唯一属于的路线，如果判断路线一样则不用换乘，否则需要换乘
     * @param id1
     * @param id2
     * @param id3
     * @return
     */
    private static String checkSwitchLine(int id1, int id2, int id3){
        String linea=edgeLines.get(getEdgeHash(id1,id2));
        String lineb=edgeLines.get(getEdgeHash(id2,id3));
        if(linea.equals(lineb)) return null;
        return lineb;
    }

    /**
     * 获取两个站点的最短路径
     * @param startStation 起始站
     * @param endStation 终点站
     * @throws IOException
     */
    private static void getStationPath(String startStation,String endStation) throws IOException {
        int startID=getStationId(startStation);
        if (startID>Station.count){
            printf("error：没有"+startStation+"这个站点。");
            return;
        }
        int endID=getStationId(endStation);
        if (endID>Station.count){
            printf("error：没有"+endStation+"这个站点。");
            return;
        }
        Queue<Integer> q=new LinkedList<>();
        q.offer(startID);
        stations.get(startID).setVisited(true);
        while (!q.isEmpty()){
            Station now=stations.get(q.poll());
            for (int it:now.getEdges()){
                Station tmp=stations.get(it);
                if (tmp.isVisited()) continue;
                q.offer(it);
                tmp.setVisited(true);
                tmp.setLastPoint(now.getId());
                if (it==endID) break;
            }
        }

        //回溯找到最短路径
        Stack<Integer> path=new Stack<>();
        int step=endID;
        while (step!=startID){
            path.push(step);
            step=stations.get(step).getLastPoint();
        }

        //记录经过的各个站点的唯一ID
        ArrayList<Integer> res=new ArrayList<>();
        res.add(startID);
        while(!path.empty()){
            int nowStationID=path.pop();
            res.add(nowStationID);
        }
        printf("从"+startStation+"到"+endStation+"共经过"+res.size()+"站\n");  //打印一共经过了几个站点
        printf("（"+edgeLines.get(getEdgeHash(res.get(0), res.get(1)))+"）\n");
        //判断是否是换乘车站
        int id1=-1,id2=-1;
        for(int it:res){
            if(id1!=-1){
                String switchMsg=checkSwitchLine(id1,id2,it);
                if(switchMsg!=null){
                    printf("（换乘"+switchMsg+"）\n");
                }
            }
            printf(stations.get(it).getName()+"\n");
            id1=id2;
            id2=it;
        }
    }

    public static void main(String[] args) throws IOException {
        String a=null,b1=null,b2=null,map=null,cmd=null;
        for (int i = 0 ; i < args.length ; i++){
            String arg=args[i];
            if (arg.startsWith("-")){
                if (cmd!=null){
                    printf("error：输入指令有误，请重新输入。");
                    return;
                }
                cmd = arg.substring(1);
            } else {
                if ( cmd == null ){
                    printf("error：输入指令有误，请重新输入。");
                    return;
                }
                switch (cmd){
                    case "a":a=arg;break;
                    case "map":map=arg;break;
                    case "b":b1=arg;i++;b2=args[i];break;
                    case "o":
                        o=arg;
                        try {
                            new BufferedWriter(new FileWriter(o));
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:printf("error：输入指令有误，请重新输入。");return;
                }
                cmd=null;
            }
        }
        if (map==null){
            printf("error：没有找到相关的地图数据文件。");
            return;
        }
        data_init(map);
        if (a!=null){
            getLine(a);
            return;
        }
        if (b1!=null && b2!=null){
            getStationPath(b1,b2);
            return;
        }
        printf("error：参数错误！");
    }
}
