import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class Runner {
    public static void main(String[] args) {
        WMCMetric m = new WMCMetric();
        WMCMetricComplex ccm = new WMCMetricComplex();
        DirectoryReader reader = new DirectoryReader();
        RFCMetric rfc = new RFCMetric();
        CBOMetric cbo = new CBOMetric();
        
        for(File file : reader.getFiles()) {
            cbo.calculateMetric(file);
            for(File fl : file.listFiles()) {
                if(fl.getName().endsWith((".java"))) {
                    //cbo.calculateMetric(fl);
                    //m.calculateMetric(fl);
                    //System.out.format("The class: %s has %d methods\n", fl.getName().replace(".java", ""), m.getCount());
                    //ccm.calculateMetric(fl);
                    //System.out.format("The class: %s has a ccm of %d\n", fl.getName().replace(".java", ""), ccm.getCCM());
                    //rfc.calculateMetric(fl);
                    //System.out.format("The class: %s has a rfc of %d\n", fl.getName().replace(".java", ""), rfc.getRFC());
                }
                if(fl.getName().equals("Rabbit.java")) {
                    System.exit(0);
                }
            }
        }

        /* 
        for(File file : reader.getFiles()) {
            for(File fl : file.listFiles()) {

            }
        }*/
    }
}