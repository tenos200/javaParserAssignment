import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class WMCMetric implements Metric{
    static int methodCounter;
    static Map<String, Integer> hmap = new LinkedHashMap<>();

    @Override
    public void calculateMetric() {
        DirectoryReader reader = new DirectoryReader();
        for(File files: reader.getFiles()) {
            try {
                CompilationUnit cu;
                FileInputStream in = new FileInputStream(files);
                cu = StaticJavaParser.parse(in);
                new MethodCountVisitor().visit(cu, null);
                methodCounter = 0;

            } catch(FileNotFoundException e) {
                System.err.println(e);
            }
        }
    }

    public Map<String, Integer> getCount()  {
        return hmap;
    }

    public static class MethodCountVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(ClassOrInterfaceDeclaration cl, Object arg) {
            super.visit(cl, arg);
            hmap.put(cl.getNameAsString(), methodCounter);
        }

        @Override
        public void visit(MethodDeclaration md, Object arg) {
            methodCounter++;
        }

    }  
}