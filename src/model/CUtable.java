package model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by promtou on 2016/3/28.
 */
public class CUtable extends AbstractTableModel {
    private String[] columnNames = { "ID", "Remark" };
    private ArrayList<CUTableRow> ar;

    public CUtable(CUTableRow[] arrayList){
        ar = new ArrayList<>();
        for(CUTableRow r:arrayList){
            ar.add(r);
        }
    }
    //如果数据量很大，可以考虑牺牲空间再加一个hashmap
    public String getRemark(String key){
        for(CUTableRow r:ar){
            if (r.get(0).equals(key))
                return r.get(1);
        }
        return null;
    }
    public void addRoww(CUTableRow r){
        ar.add(r);
        fireTableDataChanged();
    }
    public void removeRow(CUTableRow r){
        ar.remove(r);
        fireTableDataChanged();
    }
    private class CUtableComp implements Comparator<String[]>{
        @Override
        public int compare(String[] o1, String[] o2) {
            return o1[0].compareTo(o2[0]);
        }
    }
    public ArrayList<CUTableRow> getAr(){
        return ar;
    }
    @Override
    public int getRowCount() {
        return ar.size();
    }

    @Override
    public int getColumnCount() {
        return CUTableRow.LEN;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ar.get(rowIndex).get(columnIndex);
    }
    public String getColumnName(int col) {
        return columnNames[col];
    }
}
