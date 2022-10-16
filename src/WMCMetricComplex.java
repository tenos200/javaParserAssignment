import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
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

public class WMCMetricComplex implements Metric {

    /* The assumption we make for this count of metric is that the following statements: 
    switch, if, while, do, try, for each, for.
    All of these have the possibility to affect the flow graph in some way, 
    as they are able to lead to different decisions while visited
    */
    static int cyclomaticMetric = 0;

    @Override
    public void calculateMetric(File file) {
        cyclomaticMetric = 0;
        try {
            CompilationUnit cu;
            FileInputStream in = new FileInputStream(file);
            cu = StaticJavaParser.parse(in);
            new CCM().visit(cu, null);

        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public int getCCM() {
        return cyclomaticMetric;
    }

    public static class CCM extends VoidVisitorAdapter  {

        @Override
        public void visit(ConstructorDeclaration md, Object arg) {
            //We use this to ensure that we don't count the if statements in the constructors
        }

        @Override
        public void visit(MethodDeclaration md, Object arg) {
            cyclomaticMetric++;
            super.visit(md, arg);
        }
        
        @Override
        public void visit(BlockStmt m, Object arg) {
            for(Statement statement : m.getStatements()) {
                if(statement.isIfStmt() || statement.isSwitchStmt() || statement.isWhileStmt() || statement.isDoStmt() || statement.isTryStmt() || statement.isForEachStmt() || statement.isForStmt()) {
                    cyclomaticMetric++;
                    statement.accept(this, arg);
                }
            }
        }

        @Override
        public void visit(BinaryExpr expr, Object arg) {
            if(expr.getOperator() == BinaryExpr.Operator.AND || expr.getOperator() == BinaryExpr.Operator.OR) {
                cyclomaticMetric++;
            }
            super.visit(expr, arg);
        }
    }
}