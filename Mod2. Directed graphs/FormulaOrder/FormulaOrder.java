/**
 * Created by vsevolodmolchanov on 03.04.18.
 */

import java.util.*;
import java.util.function.IntPredicate;

class Position {
    private String text;
    private int index, line, col;

    public Position(String text) {
        this(text, 0, 1, 1);
    }

    private Position(String text, int index, int line, int col) {
        this.text = text;
        this.index = index;
        this.line = line;
        this.col = col;
    }

    public int getChar() {
        return index < text.length() ? text.codePointAt(index) : -1;
    }

    public boolean satisfies(IntPredicate p) {
        return p.test(getChar());
    }

    public Position skip() {
        int c = getChar();
        switch (c) {
            case -1:
                return this;
            case '\n':
                return new Position(text, index+1, line+1, 1);
            default:
                return new Position(text, index + (c > 0xFFFF ? 2 : 1), line, col+1);
        }
    }

    public Position skipWhile(IntPredicate p) {
        Position pos = this;
        while (pos.satisfies(p)) pos = pos.skip();
        return pos;
    }

    String substring(Position follow) {
        return text.substring(this.index, follow.index);
    }

    public String toString() {
        return String.format("(%d, %d)", line, col);
    }
}

class SyntaxError extends Exception {
    public SyntaxError(Position pos, String msg) {
        super(String.format("Syntax error at %s: %s", pos.toString(), msg));
    }
}

enum Tag {
    IDENT,
    NUMBER,
    PLUS,
    MINUS,
    MUL,
    DIV,
    EQUAL,
    LPAREN,
    RPAREN,
    COMMA,
    NEWLINE,
    END_OF_TEXT;

    public String toString() {
        switch (this) {
            case IDENT: return "identifier";
            case NUMBER: return "number";
            case PLUS: return "+";
            case MINUS: return "-";
            case MUL: return "*";
            case DIV: return "/";
            case EQUAL: return "=";
            case LPAREN: return "'('";
            case RPAREN: return "')'";
            case COMMA: return ",";
            case NEWLINE: return "\n";
            case END_OF_TEXT: return "end of text";
        }
        throw new RuntimeException("unreachable code");
    }
}

class Token {
    private Tag tag;
    private Position start, follow;

    public Token(String text) throws SyntaxError {
        this(new Position(text));
    }

    private Token(Position cur) throws SyntaxError {
        start = cur.skipWhile(c -> c == ' ' || c == '\t');
        follow = start.skip();
        switch (start.getChar()) {
            case -1:
                tag = Tag.END_OF_TEXT;
                break;
            case '+':
                tag = Tag.PLUS;
                break;
            case '-':
                tag = Tag.MINUS;
                break;
            case '*':
                tag = Tag.MUL;
                break;
            case '/':
                tag = Tag.DIV;
                break;
            case '=':
                tag = Tag.EQUAL;
                break;
            case '(':
                tag = Tag.LPAREN;
                break;
            case ')':
                tag = Tag.RPAREN;
                break;
            case ',':
                tag = Tag.COMMA;
                break;
            case '\n':
                tag = Tag.NEWLINE;
                break;
            default:
                if (start.satisfies(Character::isLetter)) {
                    follow = follow.skipWhile(Character::isLetterOrDigit);
                    tag = Tag.IDENT;
                } else if (start.satisfies(Character::isDigit)) {
                    follow = follow.skipWhile(Character::isDigit);
                    if (follow.satisfies(Character::isLetter)) {
                        throw new SyntaxError(follow, "delimiter expected");
                    }
                    tag = Tag.NUMBER;
                } else {
                    throwError("invalid character");
                }
        }
    }

    public void throwError(String msg) throws SyntaxError {
        throw new SyntaxError(start, msg);
    }

    public boolean matches(Tag ...tags) {
        return Arrays.stream(tags).anyMatch(t -> tag == t);
    }

    public Token next() throws SyntaxError {
        return new Token(follow);
    }

    @Override
    public String toString() {
        return start.substring(follow);
    }
}

class Color {
    private String s;

    public Color(String s) {
        this.s = s;
    }

    public String getColor() {
        return s;
    }

    @Override
    public String toString() {
        return s;
    }
}

class Vertex {
    private String vert;
    private ArrayList<Vertex> outEdges;
    private Color color;

    Vertex(String vert) {
        this.vert = vert;
        outEdges = new ArrayList<>();
        color = new Color("white");
    }

    public ArrayList<Vertex> outEdges() {
        return outEdges;
    }

    public void addVertex(Vertex v) {
        outEdges.add(v);
    }

    public String getVert() {
        return vert;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return vert;
    }
}

class Graph {
    private static Collection<Vertex> vertexes;
    private Stack<Vertex> stack;

    Graph(Collection<Vertex> vertexes) {
        this.vertexes = vertexes;
    }

    public Stack<Vertex> DFS_First() {
        stack = new Stack<>();
        for (Vertex vertex : vertexes) {
            if (vertex.getColor().getColor() == "white") {
                DFS_Second(vertex);
            }
        }

        return stack;
    }

    public void DFS_Second(Vertex v) {
        v.setColor(new Color("gray"));
        for (Vertex u : v.outEdges()) {
            if (u.getColor().getColor() == "white") {
                DFS_Second(u);
            } else if (u.getColor().getColor() == "gray") {
                throw new RuntimeException("cycle");
            }
        }
        stack.push(v);
        v.setColor(new Color("black"));
    }
}

public class FormulaOrder {
    private static Token sym;
    private static Scanner scanner;
    private static Map<String, Vertex> definedVertexMap = new HashMap<>();
    private static Map<String, ArrayList<Vertex>> variableToFormulaMap = new HashMap<>();
    private static Collection<Vertex> vertexes = new ArrayList<>();
    private static int leftIdent, rightIdent;
    private static Vertex curFormula;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        in.useDelimiter("\\Z");
        String text = in.next() + '\n';

        try {
            scanner = new Scanner(text);
            sym = new Token(text);
            parse();
            Graph graph = new Graph(vertexes);
            for (Vertex vertex : graph.DFS_First()) {
                System.out.println(vertex);
            }
        }
        catch (SyntaxError e) {
            System.out.println("syntax error");
        }
        catch (RuntimeException e) {
            System.out.println("cycle");
        }
    }

    private static void expect(Tag tag) throws SyntaxError {
        if (!sym.matches(tag)) {
            sym.throwError(tag.toString() + " expected");
        }
        sym = sym.next();
    }

    private static void parse() throws SyntaxError {
        formulas();
        expect(Tag.END_OF_TEXT);
        if (!variableToFormulaMap.isEmpty()) {
            sym.throwError("undefined functions");
        }
    }

    private static void formulas() throws SyntaxError {
        while (true) {
            if (!sym.matches(Tag.IDENT)) {
                break;
            }
            formula();
        }
    }

    private static void formula() throws SyntaxError {
        curFormula = new Vertex(scanner.nextLine());
        vertexes.add(curFormula);
        leftIdent = 0;
        ident();
        expect(Tag.EQUAL);
        rightIdent = 0;
        expr();
        if (leftIdent != rightIdent) {
            sym.throwError("number of variables don't match");
        }
        expect(Tag.NEWLINE);
    }

    private static void ident() throws SyntaxError {
        String s = sym.toString();
        Vertex get = definedVertexMap.get(s);
        if (get != null) {
            sym.throwError("twice defined variable");
        }
        definedVertexMap.put(s, curFormula);
        List<Vertex> temp = variableToFormulaMap.get(s);
        if (temp != null) {
            for (Vertex v : temp) {
                v.addVertex(curFormula);
            }
            variableToFormulaMap.remove(s);
        }
        expect(Tag.IDENT);
        leftIdent++;
        if (sym.matches(Tag.COMMA)) {
            sym = sym.next();
            ident();
        }
    }

    private static void expr() throws SyntaxError {
        rightIdent++;
        arithExpr();
        exprRecursion();
    }

    private static void exprRecursion() throws SyntaxError {
        if (sym.matches(Tag.COMMA)) {
            sym = sym.next();
            expr();
        }
    }

    private static void arithExpr() throws SyntaxError {
        term();
        arithExprRecursion();
    }

    private static void arithExprRecursion() throws SyntaxError {
        if (sym.matches(Tag.PLUS, Tag.MINUS)) {
            sym = sym.next();
            term();
            arithExprRecursion();
        }
    }

    private static void term() throws SyntaxError {
        factor();
        termRecursion();
    }

    private static void termRecursion() throws SyntaxError {
        if (sym.matches(Tag.MUL, Tag.DIV)) {
            sym = sym.next();
            factor();
            termRecursion();
        }
    }

    private static void factor() throws SyntaxError {
        if (sym.matches(Tag.NUMBER)) {
            sym = sym.next();
        } else if (sym.matches(Tag.LPAREN)) {
            sym = sym.next();
            arithExpr();
            expect(Tag.RPAREN);
        } else if (sym.matches(Tag.MINUS)) {
            sym = sym.next();
            factor();
        } else if (sym.matches(Tag.IDENT)) {
            String s = sym.toString();
            Vertex get = definedVertexMap.get(s);
            if (get == null) {
                List<Vertex> temp = variableToFormulaMap.get(s);
                if (temp != null) {
                    temp.add(curFormula);
                } else {
                    ArrayList<Vertex> l = new ArrayList<>();
                    l.add(curFormula);
                    variableToFormulaMap.put(s, l);
                }
            } else {
                curFormula.addVertex(get);
            }
            sym = sym.next();
        } else {
            sym.throwError("number, ident, left parentheses or minus expected");
        }
    }
}





//import java.util.*;
//import java.util.function.IntPredicate;
////import java.util.function.Predicate;
//
//class Position {
//    private String text;
//    private int index, line, col;
//
//    public Position(String text) {
//        this(text, 0, 1, 1);
//    }
//
//    private Position(String text, int index, int line, int col) {
//        this.text = text;
//        this.index = index;
//        this.line = line;
//        this.col = col;
//    }
//
//    public int getChar() {
//        return index < text.length() ? text.codePointAt(index) : -1;
//    }
//
//    public boolean satisfies(IntPredicate p) {
//        return p.test(getChar());
//    }
//
//    public Position skip() {
//        int c = getChar();
//        switch (c) {
//            case -1:
//                return this;
//            case '\n':
//                return new Position(text, index+1, line+1, 1);
//            default:
//                return new Position(text, index + (c > 0xFFFF ? 2 : 1), line, col+1);
//        }
//    }
//
//    public Position skipWhile(IntPredicate p) {
//        Position pos = this;
//        while (pos.satisfies(p)) pos = pos.skip();
//        return pos;
//    }
//
//    String substring(Position follow) {
//        return text.substring(this.index, follow.index);
//    }
//
//    public String toString() {
//        return String.format("(%d, %d)", line, col);
//    }
//}
//
//class SyntaxError extends Exception {
//    public SyntaxError(Position pos, String msg) {
//        super(String.format("Syntax error at %s: %s", pos.toString(), msg));
//    }
//}
//
//enum Tag {
//    IDENT,
//    NUMBER,
//    PLUS,
//    MINUS,
//    MUL,
//    DIV,
//    EQUAL,
//    LPAREN,
//    RPAREN,
//    COMMA,
//    NEWLINE,
//    END_OF_TEXT;
//
//    public String toString() {
//        switch (this) {
//            case IDENT: return "identifier";
//            case NUMBER: return "number";
//            case PLUS: return "+";
//            case MINUS: return "-";
//            case MUL: return "*";
//            case DIV: return "/";
//            case EQUAL: return "=";
//            case LPAREN: return "'('";
//            case RPAREN: return "')'";
//            case COMMA: return ",";
//            case NEWLINE: return "\n";
//            case END_OF_TEXT: return "end of text";
//        }
//        throw new RuntimeException("unreachable code");
//    }
//}
//
//class Token {
//    private Tag tag;
//    private Position start, follow;
//
//    public Token(String text) throws SyntaxError {
//        this(new Position(text));
//    }
//
//    private Token(Position cur) throws SyntaxError {
//        start = cur.skipWhile(c -> c == ' ' || c == '\t');
//        follow = start.skip();
//        switch (start.getChar()) {
//            case -1:
//                tag = Tag.END_OF_TEXT;
//                break;
//            case '+':
//                tag = Tag.PLUS;
//                break;
//            case '-':
//                tag = Tag.MINUS;
//                break;
//            case '*':
//                tag = Tag.MUL;
//                break;
//            case '/':
//                tag = Tag.DIV;
//                break;
//            case '=':
//                tag = Tag.EQUAL;
//                break;
//            case '(':
//                tag = Tag.LPAREN;
//                break;
//            case ')':
//                tag = Tag.RPAREN;
//                break;
//            case ',':
//                tag = Tag.COMMA;
//                break;
//            case '\n':
//                tag = Tag.NEWLINE;
//                break;
//            default:
//                if (start.satisfies(Character::isLetter)) {
//                    follow = follow.skipWhile(Character::isLetterOrDigit);
//                    tag = Tag.IDENT;
//                } else if (start.satisfies(Character::isDigit)) {
//                    follow = follow.skipWhile(Character::isDigit);
//                    if (follow.satisfies(Character::isLetter)) {
//                        throw new SyntaxError(follow, "delimiter expected");
//                    }
//                    tag = Tag.NUMBER;
//                } else {
//                    throwError("invalid character");
//                }
//        }
//    }
//
//    public void throwError(String msg) throws SyntaxError {
//        throw new SyntaxError(start, msg);
//    }
//
//    public boolean matches(Tag ...tags) {
//        return Arrays.stream(tags).anyMatch(t -> tag == t);
//    }
//
//    public Token next() throws SyntaxError {
//        return new Token(follow);
//    }
//
//    @Override
//    public String toString() {
//        return start.substring(follow);
//    }
//}
//
//class FormulaOrder {
//    public static void main(String[] args) {
//        Scanner in = new Scanner(System.in);
//        in.useDelimiter("\\Z");
//        try {
//            Parser p = new Parser("a, b = 10, 15\n" +
//                    "h = (a + b) / 2\n" +
//                    "S = a * b\n" +
//                    "V = S*h / 3\n");
//            Graph g = new Graph(p.parse());
//            for (Vertex vertex : g.DFS_First()) {
//                System.out.println(vertex);
//            }
//        } catch (SyntaxError e) {
//            System.out.println("syntax error");
//        } catch (RuntimeException e) {
//            System.out.println("cycle");
//        }
//    }
//}
//
//class Parser {
//    private final Scanner scanner;
//    private Token sym;
//    private Map<String, Vertex> definedVertexMap;
//    private Map<String, List<Vertex>> variableToFormulaMap;
//    private Collection<Vertex> vertexes;
//    private int leftIdent, rightIdent;
//    private Vertex curFormula;
//
//    Parser(String text) throws SyntaxError {
//        this.scanner = new Scanner(text);
//        sym = new Token(text);
//        variableToFormulaMap = new HashMap<>();
//        vertexes = new ArrayList<>();
//        definedVertexMap = new HashMap<>();
//    }
//
//    private void expect(Tag type) throws SyntaxError {
//        if (!sym.matches(type)) {
//            sym.throwError(type + " expected");
//        }
//        sym = sym.next();
//    }
//
//    public Collection<Vertex> parse() throws SyntaxError {
//        formulas();
//        expect(Tag.END_OF_TEXT);
//        if (!variableToFormulaMap.isEmpty()) {
//            sym.throwError("undefined functions");
//        }
//
//        return vertexes;
//    }
//
//    private void formulas() throws SyntaxError {
//        while (true) {
//            if (!sym.matches(Tag.IDENT)) {
//                break;
//            }
//            formula();
//        }
//    }
//
//    private void formula() throws SyntaxError {
//        curFormula = new Vertex(scanner.nextLine());
//        vertexes.add(curFormula);
//        leftIdent = 0;
//        ident();
//        expect(Tag.EQUAL);
//        rightIdent = 0;
//        expr();
//        if (leftIdent != rightIdent) {
//            sym.throwError("number of variables don't match");
//        }
//        expect(Tag.NEWLINE);
//    }
//
//    private void ident() throws SyntaxError {
//        String s = sym.toString();
//        Vertex get = definedVertexMap.get(s);
//        if (get != null) {
//            sym.throwError("twice defined variable");
//        }
//        definedVertexMap.put(s, curFormula);
//        List<Vertex> temp = variableToFormulaMap.get(s);
//        if (temp != null) {
//            for (Vertex v : temp) {
//                v.addVertex(curFormula);
//            }
//            variableToFormulaMap.remove(s);
//        }
//        expect(Tag.IDENT);
//        leftIdent++;
//        if (sym.matches(Tag.COMMA)) {
//            sym = sym.next();
//            ident();
//        }
//    }
//
//    private void expr() throws SyntaxError {
//        rightIdent++;
//        arithExpr();
//        exprRecursion();
//    }
//
//    private void exprRecursion() throws SyntaxError {
//        if (sym.matches(Tag.COMMA)) {
//            sym = sym.next();
//            expr();
//        }
//    }
//
//    private void arithExpr() throws SyntaxError {
//        term();
//        arithExprRecursion();
//    }
//
//    private void arithExprRecursion() throws SyntaxError {
//        if (sym.matches(Tag.PLUS, Tag.MINUS)) {
//            sym = sym.next();
//            term();
//            arithExprRecursion();
//        }
//    }
//
//    private void term() throws SyntaxError {
//        factor();
//        termRecursion();
//    }
//
//    private void termRecursion() throws SyntaxError {
//        if (sym.matches(Tag.MUL, Tag.DIV)) {
//            sym = sym.next();
//            factor();
//            termRecursion();
//        }
//    }
//
//    private void factor() throws SyntaxError {
//        if (sym.matches(Tag.NUMBER)) {
//            sym = sym.next();
//        } else if (sym.matches(Tag.LPAREN)) {
//            sym = sym.next();
//            arithExpr();
//            expect(Tag.RPAREN);
//        } else if (sym.matches(Tag.MINUS)) {
//            sym = sym.next();
//            factor();
//        } else if (sym.matches(Tag.IDENT)) {
//            String s = sym.toString();
//            Vertex get = definedVertexMap.get(s);
//            if (get == null) {
//                List<Vertex> temp = variableToFormulaMap.get(s);
//                if (temp != null) {
//                    temp.add(curFormula);
//                } else {
//                    ArrayList<Vertex> l = new ArrayList<>();
//                    l.add(curFormula);
//                    variableToFormulaMap.put(s, l);
//                }
//            } else {
//                curFormula.addVertex(get);
//            }
//            sym = sym.next();
//        } else {
//            sym.throwError("number, ident, left parentheses or minus expected");
//        }
//    }
//}
//
////class Token {
////    private final Position start;
////    private Position follow;
////    private Tag tag;
////
////    Token(String text) throws SyntaxError {
////        this(new Position(text));
////    }
////
////    private Token(Position currentPos) throws SyntaxError {
////        start = currentPos.skipWhile(c -> c == ' ' || c == '\t');
////        follow = start.skip();
////        switch (start.getChar()) {
////            case -1:
////                tag = Tag.END_OF_TEXT;
////                break;
////            case '+':
////                tag = Tag.PLUS;
////                break;
////            case '-':
////                tag = Tag.MINUS;
////                break;
////            case '*':
////                tag = Tag.MUL;
////                break;
////            case '/':
////                tag = Tag.DIV;
////                break;
////            case '=':
////                tag = Tag.EQUAL;
////                break;
////            case '(':
////                tag = Tag.LPAREN;
////                break;
////            case ')':
////                tag = Tag.RPAREN;
////                break;
////            case ',':
////                tag = Tag.COMMA;
////                break;
////            case '\n':
////                tag = Tag.NEWLINE;
////                break;
////            default:
////                if (start.satisfies(Character::isLetter)) {
////                    follow = follow.skipWhile(Character::isLetterOrDigit);
////                    tag = Tag.IDENT;
////                } else if (start.satisfies(Character::isDigit)) {
////                    follow = follow.skipWhile(Character::isDigit);
////                    if (follow.satisfies(Character::isLetter)) {
////                        throw new SyntaxError(follow, "delimiter expected");
////                    }
////                    tag = Tag.NUMBER;
////                } else {
////                    throwError("invalid character");
////                }
////        }
////    }
////
////    @Override
////    public String toString() {
////        return start.substring(follow);
////    }
////
////    boolean matches(Tag... types) {
////        return Arrays.stream(types).anyMatch(x -> x == tag);
////    }
////
////    void throwError(String msg) throws SyntaxError {
////        throw new SyntaxError(start, msg);
////    }
////
////    Token next() throws SyntaxError {
////        return new Token(follow);
////    }
////}
//
//class Color {
//    private String s;
//
//    public Color(String s) {
//        this.s = s;
//    }
//
//    public String getColor() {
//        return s;
//    }
//
//    @Override
//    public String toString() {
//        return s;
//    }
//}
//
//class Vertex {
//    private String vert;
//    private ArrayList<Vertex> outEdges;
//    private Color color;
//
//    Vertex(String vert) {
//        this.vert = vert;
//        outEdges = new ArrayList<>();
//        color = new Color("white");
//    }
//
//    public ArrayList<Vertex> outEdges() {
//        return outEdges;
//    }
//
//    public void addVertex(Vertex v) {
//        outEdges.add(v);
//    }
//
//    public String getVert() {
//        return vert;
//    }
//
//    public Color getColor() {
//        return color;
//    }
//
//    public void setColor(Color color) {
//        this.color = color;
//    }
//
//    @Override
//    public String toString() {
//        return vert;
//    }
//}
//
//class Graph {
//    private static Collection<Vertex> vertexes;
//    private Stack<Vertex> stack;
//
//    Graph(Collection<Vertex> vertexes) {
//        this.vertexes = vertexes;
//    }
//
//    public Stack<Vertex> DFS_First() {
//        stack = new Stack<>();
//        for (Vertex vertex : vertexes) {
//            if (vertex.getColor().getColor() == "white") {
//                DFS_Second(vertex);
//            }
//        }
//
//        return stack;
//    }
//
//    public void DFS_Second(Vertex v) {
//        v.setColor(new Color("gray"));
//        for (Vertex u : v.outEdges()) {
//            if (u.getColor().getColor() == "white") {
//                DFS_Second(u);
//            } else if (u.getColor().getColor() == "gray") {
//                throw new RuntimeException("cycle");
//            }
//        }
//        stack.push(v);
//        v.setColor(new Color("black"));
//    }
//}