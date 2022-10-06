import java.io.File;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class DirectoryReader {


    public ArrayList<File> getFiles() {
        ArrayList <File> list = new ArrayList<>();
        File dir1 = new File("./CS409TestSystem2022/foxes-and-rabbits-graph");
        File dir2 = new File("./CS409TestSystem2022/taxi-company-later-stage");
        File dir3 = new File("./CS409TestSystem2022/weblog-analyzer");



        File [] subDirs = dir1.listFiles();
        for(File sub : subDirs) {
            if(sub.getName().endsWith(".java")) {
                list.add(sub);
            }
        }
            
        subDirs = dir2.listFiles();
        for(File sub : subDirs) {
            if(sub.getName().endsWith(".java")) {
                list.add(sub);
            }
        }
        
        subDirs = dir3.listFiles();
        for(File sub : subDirs) {
            if(sub.getName().endsWith(".java")) {
                list.add(sub);
            }
        }
        return list;
    }
} 