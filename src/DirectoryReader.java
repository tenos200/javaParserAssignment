import java.io.File;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class DirectoryReader {

    public ArrayList<File> getFiles() {
        ArrayList <File> list = new ArrayList<>();
        //change the perfered path here
        File directory = new File("./cs409TestSystem2022");
        for(File files : directory.listFiles()) {
            if(files.isDirectory()) {
                list.add(files);
            }
        }
        return list;
    }
} 