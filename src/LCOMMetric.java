import java.io.File;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.util.ArrayList;
import java.util.HashMap;


public class LCOMMetric implements Metric {

    static ArrayList<MethodDeclaration> allMethods = new ArrayList<>();
    static HashMap<String, HashSet<String>> variableNamesInMethods = new HashMap<>();
    static HashSet<String> fieldDeclarations = new HashSet<>();
    static String current;
    static int LCOMCount; 
    public void calculateMetric(File file) {
        /* 
            We assume that an instantiation of a local variable will not have the same name as an instance variable,
            e.g., if a field variable (int martin) has been declared at the top we assume that no locale method will declare another (int martin).
        */
        try {

            //we need to null all the values before starting this on a new class
            LCOMCount = 0;
            allMethods.clear();
            variableNamesInMethods.clear();
            fieldDeclarations.clear();
            current = "";

            CompilationUnit cu;
            FileInputStream in = new FileInputStream(file);
            cu = StaticJavaParser.parse(in);
            //we start by visiting the fields because the fields variables will be what is LCOM in the class
            new VisitFields().visit(cu, null);
            //We then count the number of non instansiated variables in the method we are visiting
            new VisitMethodVariables().visit(cu, null);

            ArrayList<HashSet<String>> list = new ArrayList<>();
            //We add this to a list in order to be able to do a nested iteration
            for(HashSet <String> subsets : variableNamesInMethods.values()) {
                list.add(subsets);
            }

            //we loop through the list nested and compare each set to each other once only.
            for(int i = 0; i < list.size(); i++) {
                for(int j = i + 1; j < list.size(); j++) {
                    //We create a new set to see the intersection between the sets, i.e, which fields variables they share
                    HashSet<String> intersection = new HashSet<>(list.get(i));
                    intersection.retainAll(list.get(j));
                    if(intersection.size() > 0) {
                        LCOMCount--;
                    } else {
                        LCOMCount++;
                    }
                }
            }
        } catch(FileNotFoundException e) {
            System.err.println(e);
        }
    }

    public int getLCOM() {
        if(LCOMCount < 0) {
            return 0;
        } 
        return LCOMCount;
    }

    //This visitor makes sure to find all the field declarations as they will the comparisons between the methods
    public static class VisitFields extends VoidVisitorAdapter {
        @Override
        public void visit(FieldDeclaration fl, Object arg) {
            for(VariableDeclarator vd : fl.getVariables()) {
                fieldDeclarations.add(vd.getNameAsString());
            }
        }
    }

    /* This visitor visits all the methods and their methods bodies, we are only intrested in the things that are not declared in 
     * 
    */
    public static class VisitMethodVariables extends VoidVisitorAdapter {
        @Override
        public void visit(MethodDeclaration md, Object arg) {
            allMethods.add(md);
            variableNamesInMethods.put(md.getNameAsString(), new HashSet<>());
            current = md.getNameAsString();
            md.getBody().ifPresent(l -> l.accept(new NameVisitor(), arg));
        }
    }

    /*This visitor looks at all the name of what the initaliser and compares these to the field declarations.
     *If one of the initializers is in the field declarations set then we add this into the variables name set. 
    */
    
    public static class NameVisitor extends VoidVisitorAdapter  {
        @Override
        public void visit(VariableDeclarator vd, Object arg) {
            HashSet<String> set  = variableNamesInMethods.get(current);
            Optional<Expression> initalizer = vd.getInitializer();
            if(initalizer.isPresent() && fieldDeclarations.contains(initalizer.get().toString())) {
                set.add(initalizer.get().toString());
                variableNamesInMethods.put(current, set);
            }
        }

        //After this we visits all the remaining simple names that is in the class
        @Override
        public void visit(SimpleName name, Object arg) {
            HashSet<String> set  = variableNamesInMethods.get(current);
            if(fieldDeclarations.contains(name.getIdentifier())) {
                set.add(name.getIdentifier());
                variableNamesInMethods.put(current, set);
            }
        }
    }
}