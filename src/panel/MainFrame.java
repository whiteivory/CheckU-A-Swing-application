package panel;

import model.CUTableRow;
import model.CUtable;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created by promtou on 2016/3/28.
 */
public class MainFrame {
    private JFrame mainFrame;
    private JScrollPane scrollPane;
    private JTable table;
    private String[][] data;
    private ArrayList<CUTableRow> dataToBeSaved; //changed value
    private ArrayList<String> dataToBeDeleted;
    private boolean saveFlag = false;
    private TreeSet<CUTableRow> ts;  //set to deduplicate

    private JPanel jPanel;
    private JButton addButton;
    private JButton findButton;
    private JButton deleteButton;
    private JTextField addT;
    private JTextField findT;
    private JTextField deleteT;

    private File openFile;
    private CUtable cUtable;

    public MainFrame (){
        prepareGUI();
    }

    public static void main(String[] args){
        MainFrame  mainFrame = new MainFrame();
        mainFrame.showMenuDemo();
    }

    private void prepareGUI(){
        dataToBeSaved = new ArrayList<>();
        dataToBeDeleted = new ArrayList<>();
        mainFrame = new JFrame("CheckU");
        mainFrame.setSize(400,400);
        mainFrame.setLayout(new GridLayout(2, 1));

        //table
        table = new JTable();
        scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        scrollPane.setSize(350,100);

        //second panel
        addButton = new JButton("add(separate by , )");
        findButton = new JButton("find");
        deleteButton = new JButton("delete");
        addT = new JTextField();
        findT = new JTextField();
        deleteT = new JTextField();
        GridLayout gl = new GridLayout(3,2);
        jPanel = new JPanel(gl);
        jPanel.add(addButton);jPanel.add(addT);
        jPanel.add(findButton);jPanel.add(findT);
        jPanel.add(deleteButton);jPanel.add(deleteT);
//        statusLabel = new JLabel("2",JLabel.CENTER);

//        statusLabel.setSize(350,100);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent){
                if(saveFlag==true){
                    saveFile();
                }
                System.exit(0);
            }
        });
        mainFrame.add(scrollPane);
        mainFrame.add(jPanel);
        mainFrame.setVisible(true);
    }

    private void showMenuDemo(){
        //create a menu bar
        final JMenuBar menuBar = new JMenuBar();

        //create menus
        JMenu fileMenu = new JMenu("File");
//        JMenu editMenu = new JMenu("Edit");
//        final JMenu aboutMenu = new JMenu("About");
//        final JMenu linkMenu = new JMenu("Links");

        //create menu items
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        newMenuItem.setActionCommand("New");

        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setActionCommand("Open");

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setActionCommand("Save");

        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setActionCommand("Exit");

        MenuItemListener menuItemListener = new MenuItemListener();

        newMenuItem.addActionListener(menuItemListener);
        openMenuItem.addActionListener(new OpenFileListener());
        saveMenuItem.addActionListener(new SaveFileListener());
        exitMenuItem.addActionListener(new ExitFileListener());

        //button
        addButton.addActionListener(new AddButtonListener());
        deleteButton.addActionListener(new RemoveButtonListener());
        findButton.addActionListener(new FindButtonListener());

        //add menu items to menus
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);

        //add menu to menubar
        menuBar.add(fileMenu);

        //add menubar to the frame
        mainFrame.setJMenuBar(menuBar);
        mainFrame.setVisible(true);
    }

    class MenuItemListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {

        }
    }

   public CUTableRow[] readFile(File file){
       ArrayList<String[]> al  = new ArrayList<>();
       ts = new TreeSet<>();

       try{
           BufferedReader br = new BufferedReader(new FileReader(file));
           try {
               String line = null; //not declared within while loop
        /*
        * readLine is a bit quirky :
        * it returns the content of a line MINUS the newline.
        * it returns null only for the END of the stream.
        * it returns an empty String if two newlines appear in a row.
        */
               while (( line = br.readLine()) != null){
                   String[] strtmp = line.split(" ");
/*                   String[] strs = new String[2];
                   String[] strtmp = line.split(" ");
                   if(strtmp.length == 1){
                       strs[0] = strtmp[0];
                       strs[1] = " ";
                   }
                   else
                       strs= strtmp;*/
                   CUTableRow cutr = new CUTableRow(strtmp);
                   ts.add(cutr);
               }
           }
           finally {
               br.close();
           }
       }
       catch (IOException ex){
           ex.printStackTrace();
       }
       return ts.toArray(new CUTableRow[ts.size()]);
    }

    public void saveFile(){
        if(saveFlag==true){
            //先写文件
            try{
                BufferedWriter bw = new BufferedWriter(new FileWriter(openFile,true));
                try{
                    for(CUTableRow r:dataToBeSaved){
                        for(int i =0;i<CUTableRow.LEN;i++){
                            bw.write(r.get(i)+" ");
                        }
                        bw.newLine();
                    }
                }
                finally {
                    bw.close();
                }

            }
            catch(IOException e){
                e.printStackTrace();
            }
            //remove lines in dataToBeDeleted
            File tempFile = new File("tmp.txt");
            try{
                BufferedReader br=  new BufferedReader(new FileReader(openFile));
                BufferedWriter bw2 = new BufferedWriter(new FileWriter(tempFile));
                try{
                    String currentLine;
                    String str1;
                    while((currentLine=br.readLine())!=null ){
                        str1 = currentLine.split(" ")[0];
                        if(dataToBeDeleted.contains(str1)||str1==null) continue;
                        bw2.write(currentLine);
                        bw2.newLine();
                    }
                }
                finally {
                    br.close();
                    bw2.close();
                    openFile.delete();
                    tempFile.renameTo(openFile);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void find(String str){
        String t = cUtable.getRemark(str);
        if(t!=null){
            findT.setText("Remark is:"+t);
        }
        else {
            findT.setText("doesn't esist!");
        }
    }
    public void remove(CUTableRow r){
        if(!ts.contains(r)) return;
        saveFlag=true;
        dataToBeDeleted.add(r.get(0));
        cUtable.removeRow(r);
    }
    public void add(CUTableRow cutr){
        if(ts.contains(cutr)) return;
        saveFlag=true;
        if(dataToBeDeleted.contains(cutr.get(0))) dataToBeDeleted.remove(cutr.get(0));//如果之前删除过，则从delete array里面删除
        dataToBeSaved.add(cutr);
        cUtable.addRoww(cutr);
        ts.add(cutr);

    }
    class OpenFileListener implements ActionListener{
        public void actionPerformed(ActionEvent e) {
            File workingDirectory = new File(System.getProperty("user.dir"));
            final JFileChooser fc = new JFileChooser(workingDirectory);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
            fc.setFileFilter(filter);
            int returnVal = fc.showOpenDialog(mainFrame);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                openFile = fc.getSelectedFile();
                CUTableRow[] ar = readFile(openFile);
                cUtable = new CUtable(ar);
                table.setModel(cUtable);
                //This is where a real application would open the file.

            } else {
            }
        }
    }
    class SaveFileListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            saveFile();
        }
    }
    class AddButtonListener implements  ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String line = addT.getText();
            add(new CUTableRow(line.split(",")));
        }
    }
    class RemoveButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] s = new String[1];
            s[0]=deleteT.getText();
            CUTableRow r = new CUTableRow(s);
            remove(r);
        }
    }
    class FindButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            String s = findT.getText();
            find(s);
        }
    }
    class ExitFileListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(saveFlag==true){
                saveFile();
            }
            System.exit(0);
        }
    }
}
