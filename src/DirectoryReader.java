import java.io.File;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class DirectoryReader {


    public ArrayList<File> getFiles() {
        ArrayList <File> list = new ArrayList<>();
        //Change the path here, sometimes this can break the program
        File dir1 = new File("./CS409TestSystem2022/foxes-and-rabbits-graph");
        File dir2 = new File("./CS409TestSystem2022/taxi-company-later-stage");
        File dir3 = new File("./CS409TestSystem2022/weblog-analyzer");
        list.add(dir1);
        list.add(dir2);
        list.add(dir3);
        return list;
    }
} 