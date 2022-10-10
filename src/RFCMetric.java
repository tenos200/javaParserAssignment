import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class RFCMetric implements Metric {
    static int rfcCounter;
    static Set <String> hashset = new HashSet<>();

    public void calculateMetric(File file) {
        rfcCounter = 0;
        hashset.clear();
        try {
            CompilationUnit cu;
            FileInputStream in = new FileInputStream(file);
            cu = StaticJavaParser.parse(in);
            new MethodCountVisitor().visit(cu, null);

        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public int getRFC() {
        return rfcCounter;
    }

    public static class MethodCountVisitor extends VoidVisitorAdapter {

        @Override
        public void visit(FieldDeclaration fd, Object arg) {
            fd.accept(new MethodCallVisitor(), arg);
        }

        @Override
        public void visit(ConstructorDeclaration cd, Object arg) {
            cd.accept(new MethodCallVisitor(), null);
        }

        @Override
        public void visit(MethodDeclaration md, Object arg) {
            if(!hashset.contains(md.getNameAsString())) {
                hashset.add(md.getNameAsString());
                System.out.println(md.getNameAsString());
                rfcCounter++;
            }
            md.accept(new MethodCallVisitor(), arg);
        }
    }

    public static class MethodCallVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(MethodCallExpr md, Object arg) {
            if(!hashset.contains(md.getNameAsString())) {
                System.out.println(md.getNameAsString());
                hashset.add(md.getNameAsString());
                rfcCounter++;
            }
            super.visit(md, arg);
        }
    }
}