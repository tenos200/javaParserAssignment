import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class Runner {
    public static void main(String[] args) {
        int count = 0;
        WMCMetric m = new WMCMetric();
        DirectoryReader reader = new DirectoryReader();
        for(File file : reader.getFiles()) {
            for(File fl : file.listFiles()) {
                if(fl.getName().endsWith((".java"))) {
                    m.calculateMetric(fl);
                    System.out.format("The class: %s has %d methods\n", fl.getName().replace(".java", ""), m.getCount());
                }
            }
        }
    }
}