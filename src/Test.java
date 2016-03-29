import model.CUTableRow;
import model.CUtable;

import java.util.TreeSet;

/**
 * Created by promtou on 2016/3/28.
 */
public class Test {
    public static void main(String[] args){
        TreeSet<String> ts  = new TreeSet<>();
        ts.add("hello");
        ts.add("aplle");
        ts.add("bb");
        for (String s:ts){
            System.out.println(s);
        }

    }
}
