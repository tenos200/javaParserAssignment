import java.util.HashMap;
import java.util.Map;

public class Runner {
    public static void main(String[] args) {
        WMCMetric m = new WMCMetric();
        WMCMetricComplex ccm = new WMCMetricComplex();
        m.calculateMetric();
        Map map = m.getCount();
        System.out.println(map);
        ccm.calculateMetric();

        /* 
        for(map.entry() entry : map.entrySet()) {
            System.out.format("The class: %s has %d methods.\n", entry.getKey(), entry.getValue());

        }*/

    }
}