import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class Runner {
    public static void main(String[] args) {
        DirectoryReader reader = new DirectoryReader();
        WMCMetric wmc = new WMCMetric();
        WMCMetricComplex ccm = new WMCMetricComplex();
        RFCMetric rfc = new RFCMetric();
        CBOMetric cbo = new CBOMetric();
        LCOMMetric lcom = new LCOMMetric();

        for(File file : reader.getFiles()) {
            for(File fl : file.listFiles()) {
                if(fl.getName().endsWith((".java"))) {
                    //wmc.calculateMetric(fl);
                    //System.out.format("The class: %s has %d methods\n", fl.getName().replace(".java", ""), wmc.getCount());
                    //ccm.calculateMetric(fl);
                    //System.out.format("The class: %s has a ccm of %d\n", fl.getName().replace(".java", ""), ccm.getCCM());
                    //rfc.calculateMetric(fl);
                    //System.out.format("The class: %s has a rfc of %d\n", fl.getName().replace(".java", ""), rfc.getRFC());
                    //cbo.calculateMetric(fl);
                    //System.out.format("The class: %s has a cbo of %d\n", fl.getName().replace(".java", ""), cbo.getCBO(fl.getName().replace(".java", "")));
                    lcom.calculateMetric(fl);
                    System.out.format("The class: %s has a LCOM of %d\n", fl.getName().replace(".java", ""), lcom.getLCOM());
                }
            }
        }
    }
}