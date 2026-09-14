package com.craftinginterpreters.lox;

abstract class Stmt {

    interface Visitor<R> {
        R visitRiverStmt(River stmt);
    }

    static class River extends Stmt {

        final Token name;
        final Expr.Flow response;

        River(Token name, Expr.Flow response) {
            this.name = name;
            this.response = response;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitRiverStmt(this);
        }
    }

    abstract <R> R accept(Visitor<R> visitor);
}
