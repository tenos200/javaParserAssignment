import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Set;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;

public class CBOMetric implements Metric {

    //list for all the classes
    static ArrayList<ClassOrInterfaceDeclaration> listOfClasses= new ArrayList<>();
    //set to store all class name in the directory where the file resides
    Set<String> classesInDir = new HashSet<>();
    //set to see what classes has been visited
    HashSet<String> visitedClasses = new HashSet<>();
    //Hashmap which keeps track of what class is coupled with what classes
    HashMap<String, HashSet<String>> coupledClasses = new HashMap<>();

    public void calculateMetric(File file) {

        listOfClasses.clear();
        coupledClasses.clear();
        try {

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
                visitedClasses.add(classes.getNameAsString());
            }
            
            CompilationUnit cu;
            FileInputStream in = new FileInputStream(file);
            cu = StaticJavaParser.parse(in);
            ClassVisitor visitor = new ClassVisitor();
            visitor.visit(cu, null);
            HashSet<String> foundClasses = visitor.getVisited();
            String filename = file.getName().replace(".java", "");
            coupledClasses.put(filename, new HashSet<>());
            //gets the classes that resides in our current class
            foundClasses.retainAll(visitedClasses);
            HashSet <String> setCoupled = coupledClasses.get(filename);

            //loops through the remaining classes, and if they find the current class name we add to coupling
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

        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
    }
    public int getCBO(String filename) {
        HashSet<String> metric = coupledClasses.get(filename);
        System.out.println(metric);
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
                for(ClassOrInterfaceDeclaration classes : listOfClasses) {
                    if(classes.getNameAsString().equals(p.getTypeAsString())) {
                        visited.add(p.getTypeAsString());
                    }
                }
            }
            super.visit(cd, arg);
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
            if(v.getInitializer().isPresent()) {
                super.visit(v, arg);
            }
            visited.add(v.getTypeAsString());
        }

        @Override
        public void visit(ObjectCreationExpr oExpr, Object arg) {
            visited.add(oExpr.getTypeAsString());
        }

        public HashSet<String> getVisited() {
            return visited; 
        }
    }
}