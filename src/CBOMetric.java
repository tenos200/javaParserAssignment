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
import com.github.javaparser.ast.body.Parameter;
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
    Set<String> classesInDir = new HashSet<>();
    HashSet<String> visitedClasses = new HashSet<>();
    HashMap<String, HashSet<String>> coupledClasses = new HashMap<>();

    public void calculateMetric(File file) {

        listOfClasses.clear();
        coupledClasses.clear();
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

            for(ClassOrInterfaceDeclaration classes : listOfClasses) {
                //new ClassVisitor().visit(classes, null);
                visitedClasses.add(classes.getNameAsString());
                //System.out.println(declarationsFound);
            }
            
            CompilationUnit cu;
            FileInputStream in = new FileInputStream(file);
            cu = StaticJavaParser.parse(in);
            ClassVisitor visitor = new ClassVisitor();
            visitor.visit(cu, null);
            HashSet<String> foundClasses = visitor.getVisited();
            System.out.println(foundClasses);
            System.exit(1);
            String filename = file.getName().replace(".java", "");
            coupledClasses.put(filename, new HashSet<>());
            foundClasses.retainAll(visitedClasses);
            HashSet <String> setCoupled = coupledClasses.get(filename);

            for(ClassOrInterfaceDeclaration c1: listOfClasses) {
                if(!c1.getNameAsString().equals(filename) && !setCoupled.contains(c1.getNameAsString())) {
                    ClassVisitor newVisit = new ClassVisitor();
                    newVisit.visit(c1, null);
                    HashSet<String> declarationsFound = newVisit.getVisited();
                    if(declarationsFound.contains(filename)) {
                        HashSet<String> temp = coupledClasses.get(filename);
                        temp.add(c1.getNameAsString());
                        coupledClasses.put(filename, temp);
                    }
                }
            }
            
            HashSet<String> temp = coupledClasses.get(filename);

            for(String s : foundClasses) {
                if(!s.contains(filename)) {
                    temp.add(s);
                    coupledClasses.put(filename, temp);
                }
            }
            System.out.println(coupledClasses);

        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
    }
    public int getCBO(String filename) {
        HashSet<String> metric = coupledClasses.get(filename);
        coupledClasses.clear();
        return metric.size();
    }

    //Visitor that is used for counting the classes in the directory
    public static class ClassVisitorCount extends VoidVisitorAdapter {
        @Override
        public void visit(ClassOrInterfaceDeclaration cl, Object arg) {
            listOfClasses.add(cl);
            super.visit(cl, arg);
        }
    }

    //Visitor used for visiting all the declarations 
    public static class ClassVisitor extends VoidVisitorAdapter {
        HashSet<String> visited = new HashSet<>();

        @Override
        public void visit(ConstructorDeclaration cd, Object arg) {
            for(Parameter p : cd.getParameters()) {
                System.out.println(p.getType());
                for(ClassOrInterfaceDeclaration classes : listOfClasses) {
                    if(classes.getNameAsString().equals(p.getTypeAsString())) {
                        visited.add(p.getTypeAsString());
                    }
                }
            }
        }
        @Override
        public void visit(MethodDeclaration md, Object arg) {
            for(Parameter p : md.getParameters()) {
                for(ClassOrInterfaceDeclaration classes : listOfClasses) {
                    if(classes.getNameAsString().equals(p.getTypeAsString())) {
                        visited.add(p.getTypeAsString());
                    }
                }
            }
            super.visit(md, arg);
        }
        @Override
        public void visit(FieldDeclaration fd, Object arg) {
            visited.add(fd.getElementType().asString());
        }

        @Override
        public void visit(VariableDeclarator v, Object arg) {
            visited.add(v.getTypeAsString());
        }

        public HashSet<String> getVisited() {
            return visited; 
        }
    }
}