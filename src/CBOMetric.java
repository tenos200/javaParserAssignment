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
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;


public class CBOMetric implements Metric {

    static ArrayList<ClassOrInterfaceDeclaration> listOfClasses= new ArrayList<>();
    Set<String> classesInDir = new HashSet<>();
    HashSet<String> visitedClasses = new HashSet<>();
    HashMap<String, HashSet<String>> coupledClasses = new HashMap<>();
    /* The assumption we make here is that we do not count method calls to remote classes declared field variables
     * e.g., private String name = someClass.getName(); <- this variable does not necessarily have to be used, hence we make this assumption.
    */

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

        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
    }
    public int getCBO(String filename) {
        HashSet<String> metric = coupledClasses.get(filename);
        System.out.println(coupledClasses);
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
            visited.add(v.getTypeAsString());
        }

        public HashSet<String> getVisited() {
            return visited; 
        }
    }
}