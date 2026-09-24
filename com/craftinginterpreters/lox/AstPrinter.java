package com.craftinginterpreters.lox;

class AstPrinter implements Expr.Visitor<String>, Stmt.Visitor<String> {

    String print(Expr expr) {
        return expr.accept(this);
    }

    String print(Stmt stmt) {
        return stmt.accept(this);
    }

    @Override
    public String visitRiverStmt(Stmt.River stmt) {

        String result =
            "(river " +
            stmt.name.lexeme +
            " " +
            stmt.response.accept(this);

        if (stmt.inflow != null) {
            result += " inflow " +
                      stmt.inflow.accept(this);
        }

        return result + ")";
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(
            expr.operator.lexeme,
            expr.left,
            expr.right
        );
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize(
            "group",
            expr.expression
        );
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {

        if (expr.value == null) {
            return "nil";
        }

        return expr.value.toString();
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(
            expr.operator.lexeme,
            expr.right
        );
    }

    @Override
    public String visitFlowExpr(Expr.Flow expr) {
        return "flow[" +
                expr.startFlow +
                " -> " +
                expr.peakFlow +
                " @ " +
                expr.peakDay +
                " ~ " +
                expr.recession +
                "]";
    }

    @Override
    public String visitRiverRefExpr(Expr.RiverRef expr) {
        return expr.name.lexeme;
    }

    @Override
    public String visitConfluenceExpr(Expr.Confluence expr) {
        return "(<> " +
                expr.left.accept(this) +
                " " +
                expr.right.accept(this) +
                ")";
    }

    private String parenthesize(
        String name,
        Expr... exprs
    ) {

        StringBuilder builder =
            new StringBuilder();

        builder.append("(").append(name);

        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(
                expr.accept(this)
            );
        }

        builder.append(")");

        return builder.toString();
    }
}