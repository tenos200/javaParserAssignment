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
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
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
import java.util.ArrayList;
import java.util.HashMap;


public class CBOMetric implements Metric {

    static ArrayList<ClassOrInterfaceDeclaration> listOfClasses= new ArrayList<>();
    static Set<String> classesInDir = new HashSet<>();
    static Set<String> declarationsFound = new HashSet<>();
    static HashSet<String> visitedClasses = new HashSet<>();
    static int counter = 0;



    public void calculateMetric(File file) {

        try {
            /* We add the files that are in each directory because these are the classes that can be coupled with our class.
            we don't need to count library classes, 
            so essentially our only intrest will be the classes with the correlating names to the one in the directory
            */

            //we get the parent directory
            File parentDir = file.getParentFile();

            for(File fl : parentDir.listFiles()) {
            
                if(fl.getName().endsWith(".java")) {
                    CompilationUnit cu;
                    FileInputStream in = new FileInputStream(fl);
                    cu = StaticJavaParser.parse(in);
                    new ClassVisitorCount().visit(cu, null);
                }
            }

            HashMap<String, HashSet<String>> coupledClasses = new HashMap<>();

            for(ClassOrInterfaceDeclaration classes : listOfClasses) {
                //new ClassVisitor().visit(classes, null);
                visitedClasses.add(classes.getNameAsString());
                coupledClasses.put(classes.getNameAsString(), new HashSet<>());
                //System.out.println(declarationsFound);
            }
            

            for(ClassOrInterfaceDeclaration classes : listOfClasses) {
                declarationsFound.clear();
                HashSet<String> foundClasses = visitedClasses;
                new ClassVisitor().visit(classes, null);
                foundClasses.retainAll(declarationsFound);
                HashSet <String> setCoupled = coupledClasses.get(classes.getNameAsString());
                setCoupled = foundClasses; 
                for(ClassOrInterfaceDeclaration c1: listOfClasses) {
                    declarationsFound.clear();
                    if(!c1.getNameAsString().equals(classes.getNameAsString())) {
                        System.out.println(c1.getNameAsString());
                        System.exit(1);
                        new ClassVisitor().visit(c1, null);
                        if(declarationsFound.contains(classes.getNameAsString())) {
                            HashSet<String> temp = coupledClasses.get(classes.getNameAsString());
                            temp.add(c1.getNameAsString());
                            coupledClasses.put(classes.getNameAsString(), temp);
                        }
                    }

                }
                System.exit(1);
            }


        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public static class ClassVisitorCount extends VoidVisitorAdapter {
        @Override
        public void visit(ClassOrInterfaceDeclaration cl, Object arg) {
            listOfClasses.add(cl);
            super.visit(cl, arg);
        }
    }

    public static class ClassVisitor extends VoidVisitorAdapter {
        @Override
        public void visit(FieldDeclaration fd, Object arg) {
            declarationsFound.add(fd.getElementType().asString());
        }
        @Override
        public void visit(VariableDeclarator v, Object arg) {
            declarationsFound.add(v.getTypeAsString());
        }
    }
}