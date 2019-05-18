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
    NOT_EQUAL,
    LESS,
    GREATER,
    LESS_OR_EQUAL,
    GREATER_OR_EQUAL,
    QUESTION_MARK,
    COLON,
    LPAREN,
    RPAREN,
    COMMA,
    SEMICOLON,
    ASSIGNMENT,
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
            case LESS: return "<";
            case GREATER: return ">";
            case QUESTION_MARK: return "?";
            case COLON: return ":";
            case LPAREN: return "'('";
            case RPAREN: return "')'";
            case COMMA: return ",";
            case SEMICOLON: return ";";
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
        start = cur.skipWhile(Character::isWhitespace);
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
            case '<':
                if (follow.satisfies(x -> x == '=')) {
                    follow = follow.skip();
                    tag = Tag.LESS_OR_EQUAL;
                } else if (follow.satisfies(x -> x == '>')) {
                    follow = follow.skip();
                    tag = Tag.NOT_EQUAL;
                } else {
                    tag = Tag.LESS;
                }
                break;
            case '>':
                if (follow.satisfies(x -> x == '=')) {
                    follow = follow.skip();
                    tag = Tag.GREATER_OR_EQUAL;
                } else {
                    tag = Tag.GREATER;
                }
                break;
            case '?':
                tag = Tag.QUESTION_MARK;
                break;
            case ':':
                if (follow.satisfies(x -> x == '=')) {
                    follow = follow.skip();
                    tag = Tag.ASSIGNMENT;
                } else {
                    tag = Tag.COLON;
                }
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
            case ';':
                tag = Tag.SEMICOLON;
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

class Vertex {
    private String vert;
    private ArrayList<Vertex> outEdges;
    private int T1, comp, low;

    Vertex(String vert) {
        this.vert = vert;
        T1 = comp = 0;
        outEdges = new ArrayList<>();
    }

    ArrayList<Vertex> outEdges() {
        return outEdges;
    }

    void addVertex(Vertex v) {
        outEdges.add(v);
    }

    String getVert() {
        return vert;
    }

    int getT1() {
        return T1;
    }

    void setT1(int T1) {
        this.T1 = T1;
    }

    int getComp() {
        return comp;
    }

    void setComp(int comp) {
        this.comp = comp;
    }

    int getLow() {
        return low;
    }

    void setLow(int low) {
        this.low = low;
    }

    @Override
    public String toString() {
        return vert;
    }
}

class Graph {
    private int time, count;
    private static Collection<Vertex> vertexes;
    private Stack<Vertex> stack;

    Graph(Collection<Vertex> vertexes) {
        this.vertexes = vertexes;
        time = count = 1;
    }

    public int getCount() {
        return count;
    }

    public void Tarjan() {
        stack = new Stack<>();
        vertexes.stream().filter(v -> v.getT1() == 0).forEach(this::VisitVertex_Tarjan);
    }

    private void VisitVertex_Tarjan(Vertex v) {
        v.setT1(time);
        v.setLow(time);
        time++;
        stack.push(v);
        for (Vertex t : v.outEdges()) {
            if (t.getT1() == 0) {
                VisitVertex_Tarjan(t);
            }
            if (t.getComp() == 0 && v.getLow() > t.getLow()) {
                v.setLow(t.getLow());
            }
        }
        if (v.getT1() == v.getLow()) {
            while (true) {
                Vertex u = stack.pop();
                u.setComp(count);
                if (u == v) {
                    break;
                }
            }
            count++;
        }
    }
}

public class Modules {
    private static Token sym;
    private static Set<String> argsSet = new HashSet<>();
    private static Stack<Integer> argCountStack = new Stack<>();
    private static HashMap<String, Integer> functionsMap = new HashMap<>();
    private static HashMap<String, Vertex> vertexMap = new HashMap<>();
    private static Set<String> mustDefine = new HashSet<>();
    private static String curIdent, curFunction;
    private static Vertex curVertex;

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        in.useDelimiter("\\Z");
        String text = in.next();

        try {
            sym = new Token(text);
            parse();
            Graph graph = new Graph(vertexMap.values());
            graph.Tarjan();
            System.out.println(graph.getCount() - 1);
        }
        catch (SyntaxError e) {
            System.out.println("error");
        }
        catch (RuntimeException e) {
            System.out.println("error");
        }

    }

    private static void expect(Tag tag) throws SyntaxError {
        if (!sym.matches(tag)) {
            sym.throwError(tag.toString() + " expected");
        }
        sym = sym.next();
    }

    private static void parse() throws SyntaxError {
        program();
        if (!mustDefine.isEmpty()) {
            sym.throwError("not defined functions");
        }
        expect(Tag.END_OF_TEXT);
    }

    private static void program() throws SyntaxError {
        if (sym.matches(Tag.IDENT)) {
            function();
            program();
        }
    }

    private static void function() throws SyntaxError {
        curFunction = sym.toString();
        expect(Tag.IDENT);
        expect(Tag.LPAREN);
        argsSet.clear();
        formalArgsList();
        functionsMap.put(curFunction, argsSet.size());
        expect(Tag.RPAREN);
        expect(Tag.ASSIGNMENT);
        if (mustDefine.contains(curFunction)) {
            mustDefine.remove(curFunction);
        }
        Vertex temp = vertexMap.get(curFunction);
        if (temp == null) {
            curVertex = new Vertex(curFunction);
            vertexMap.put(curFunction, curVertex);
        } else {
            curVertex = temp;
        }
        expr();
        expect(Tag.SEMICOLON);
    }

    private static void formalArgsList() throws SyntaxError {
        if (sym.matches(Tag.IDENT)) {
            identList();
        }
    }

    private static void identList() throws SyntaxError {
        argsSet.add(sym.toString());
        expect(Tag.IDENT);
        identListRecursion();
    }

    private static void identListRecursion() throws SyntaxError {
        if (sym.matches(Tag.COMMA)) {
            sym = sym.next();
            identList();
        }
    }

    private static void expr() throws SyntaxError {
        comparisonExpr();
        exprRecursion();
    }

    private static void exprRecursion() throws SyntaxError {
        if (sym.matches(Tag.QUESTION_MARK)) {
            sym = sym.next();
            comparisonExpr();
            expect(Tag.COLON);
            expr();
        }
    }

    private static void comparisonExpr() throws SyntaxError {
        arithExpr();
        comparisonExprRecursion();
    }

    private static void comparisonExprRecursion() throws SyntaxError {
        if (sym.matches(Tag.EQUAL, Tag.NOT_EQUAL, Tag.LESS, Tag.GREATER, Tag.LESS_OR_EQUAL, Tag.GREATER_OR_EQUAL)) {
            comparisonOp();
            arithExpr();
        }
    }

    private static void comparisonOp() throws SyntaxError {
        if (sym.matches(Tag.EQUAL, Tag.NOT_EQUAL, Tag.LESS, Tag.GREATER, Tag.LESS_OR_EQUAL, Tag.GREATER_OR_EQUAL)) {
            sym = sym.next();
        } else {
            sym.throwError("comparison operation expected");
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
        } else if (sym.matches(Tag.IDENT)) {
            curIdent = sym.toString();
            sym = sym.next();
            factorRecursion();
        } else if (sym.matches(Tag.LPAREN)) {
            sym = sym.next();
            expr();
            expect(Tag.RPAREN);
        } else if (sym.matches(Tag.MINUS)) {
            sym = sym.next();
            factor();
        } else {
            sym.throwError("number, ident, left parentheses or minus expected");
        }
    }

    private static void factorRecursion() throws SyntaxError {
        if (sym.matches(Tag.LPAREN)) {
            String curIdentFunction = curIdent;
            Vertex temp = vertexMap.get(curIdentFunction);
            if (!curIdentFunction.equals(curFunction)) {
                if (temp != null) {
                    curVertex.addVertex(temp);
                } else {
                    Vertex v = new Vertex(curIdentFunction);
                    curVertex.addVertex(v);
                    vertexMap.put(curIdentFunction, v);
                    mustDefine.add(curIdentFunction);
                }
            }
            sym = sym.next();
            argCountStack.push(0);
            actualArgsList();
            Integer get = functionsMap.get(curIdentFunction);
            int top = argCountStack.pop();
            if (get != null && !get.equals(top)) {
                sym.throwError("number of args don't matches");
            }
            expect(Tag.RPAREN);
        } else if (!argsSet.contains(curIdent)) {
            sym.throwError("wrong variable name");
        }
    }

    private static void actualArgsList() throws SyntaxError {
        if (sym.matches(Tag.NUMBER, Tag.IDENT, Tag.LPAREN, Tag.MINUS)) {
            incArgCount();
            exprList();
        }
    }

    private static void exprList() throws SyntaxError {
        expr();
        exprListRecursion();
    }

    private static void exprListRecursion() throws SyntaxError {
        if (sym.matches(Tag.COMMA)) {
            sym = sym.next();
            incArgCount();
            exprList();
        }
    }

    private static void incArgCount() {
        argCountStack.push(argCountStack.pop() + 1);
    }
}