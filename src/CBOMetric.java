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
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.HashSet;


public class CBOMetric implements Metric {

    static Set<String> set = new HashSet<>();
    static Set<String> classesInDir = new HashSet<>();
    static int counter = 0;

    public void calculateMetric(File file) {

        try {
            /* We add the files that are in each directory because these are the classes that can be coupled with our class.
            /we don't need to count library classes, 
            so essentially our only intrest will be the classes with the correlating names to the one in the directory
            */

            for(File fl : file.listFiles()) {
                if(fl.getName().endsWith(".java")) {
                    classesInDir.add(fl.getName().replace(".java", ""));
                }
            }

            System.out.println(classesInDir.size());
            System.exit(1);
            CompilationUnit cu;
            FileInputStream in = new FileInputStream(file);
            cu = StaticJavaParser.parse(in);
            new FieldVisitor().visit(cu, null);

        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public static class FieldVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(FieldDeclaration fd, Object arg) {
        }
    }
}