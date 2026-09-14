package com.craftinginterpreters.lox;

class AstPrinter implements Expr.Visitor<String> {

    String print(Expr expr) {
        return expr.accept(this);
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
        return parenthesize("group", expr.expression);
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

    // NEW: Prints the flow literal.
    @Override
    public String visitFlowExpr(Expr.Flow expr) {

        StringBuilder result = new StringBuilder("flow[");

        for (int i = 0; i < expr.values.size(); i++) {

            if (i > 0) {
                result.append(", ");
            }

            result.append(expr.values.get(i));
        }

        result.append("]");

        return result.toString();
    }

    private String parenthesize(String name, Expr... exprs) {

        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);

        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }

        builder.append(")");

        return builder.toString();
    }
}