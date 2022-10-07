import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class WMCMetricComplex implements Metric {

    static int cyclomaticMetric = 0;
    static Map<String, Integer> hmap = new HashMap<>();

    @Override
    public void calculateMetric(File file) {
        try {
            CompilationUnit cu;
            FileInputStream in = new FileInputStream(file);
            cu = StaticJavaParser.parse(in);
            new CCM().visit(cu, null);
            cyclomaticMetric = 0;

        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public static class CCM extends VoidVisitorAdapter  {
        @Override
        public void visit(ClassOrInterfaceDeclaration cl, Object arg) {
            super.visit(cl, arg);
            hmap.put(cl.getNameAsString(), cyclomaticMetric);
        }
        @Override
        public void visit(MethodDeclaration md, Object arg) {
            md.getBody().ifPresent(l -> l.accept(new BlockVisitor(), arg));
        }

        public static class BlockVisitor extends VoidVisitorAdapter {
            @Override
            public void visit(BlockStmt stmt, Object arg) {
                for(Statement statement : stmt.getStatements()) {
                    if(statement.isIfStmt() || statement.isSwitchStmt()) {
                        cyclomaticMetric++;
                        statement.accept(new BinaryExprVisitor(), null);
                    }
                }
            }
        }
        private static class BinaryExprVisitor extends VoidVisitorAdapter {
            @Override
            public void visit(BinaryExpr expr, Object arg) {
                if(expr.getOperator() == BinaryExpr.Operator.AND) {
                    //insert some code here ya phoneys
                }
            }
        }
    }
}