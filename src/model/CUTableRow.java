package model;

import javax.swing.text.DefaultEditorKit;

/**
 * Created by promtou on 2016/3/28.
 */
public class CUTableRow implements Comparable<CUTableRow>{
    private String strs[] ;
    public static int LEN = 2;
    public CUTableRow(String[] pstr){
        strs = new String[LEN];
        int i = 0;
        for (;i<pstr.length&&i<LEN;i++){
            strs[i]=pstr[i];
        }
        while (i<LEN){
            strs[i] = " ";
            i++;
        }
    }
    @Override
    public boolean equals(Object obj){
        if(this == obj) return  true;
        if(!(obj instanceof CUTableRow)) return false;
        CUTableRow cutr = (CUTableRow)obj;
        return strs[0].equals(cutr.strs[0]);
    }
    @Override
    public int compareTo(CUTableRow rhs){
        return strs[0].compareTo(rhs.strs[0]);
    }
    public  String get(int index){
        return strs[index];
    }
}
