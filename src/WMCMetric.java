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
    static int methodCounter = 0;
    static String className;

    @Override
    public void calculateMetric(File file) {
            methodCounter = 0;
            try {
                CompilationUnit cu;
                FileInputStream in = new FileInputStream(file);
                cu = StaticJavaParser.parse(in);
                new MethodCountVisitor().visit(cu, null);

            } catch(FileNotFoundException e) {
                System.err.println(e);
            }
        }

    public int getCount() {
        return methodCounter;
    }
    public String getClassName() {
        return className;
    }


    public static class MethodCountVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(ClassOrInterfaceDeclaration cl, Object arg) {
            className = cl.getNameAsString();
            super.visit(cl, arg);
        }

        @Override
        public void visit(MethodDeclaration md, Object arg) {
            methodCounter++;
        }

    }  
}