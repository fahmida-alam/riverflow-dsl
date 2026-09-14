package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    // expression → equality ;
    private Expr expression() {
        return equality();
    }

    // equality → comparison ( ( "!=" | "==" ) comparison )* ;
    private Expr equality() {

        Expr expr = comparison();

        while (match(BANG_EQUAL, EQUAL_EQUAL)) {

            Token operator = previous();
            Expr right = comparison();

            expr = new Expr.Binary(
                expr,
                operator,
                right
            );
        }

        return expr;
    }

    // comparison → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private Expr comparison() {

        Expr expr = term();

        while (match(
            GREATER,
            GREATER_EQUAL,
            LESS,
            LESS_EQUAL
        )) {

            Token operator = previous();
            Expr right = term();

            expr = new Expr.Binary(
                expr,
                operator,
                right
            );
        }

        return expr;
    }

    // term → factor ( ( "-" | "+" ) factor )* ;
    private Expr term() {

        Expr expr = factor();

        while (match(MINUS, PLUS)) {

            Token operator = previous();
            Expr right = factor();

            expr = new Expr.Binary(
                expr,
                operator,
                right
            );
        }

        return expr;
    }

    // factor → unary ( ( "/" | "*" ) unary )* ;
    private Expr factor() {

        Expr expr = unary();

        while (match(SLASH, STAR)) {

            Token operator = previous();
            Expr right = unary();

            expr = new Expr.Binary(
                expr,
                operator,
                right
            );
        }

        return expr;
    }

    // unary → ( "!" | "-" ) unary | primary ;
    private Expr unary() {

        if (match(BANG, MINUS)) {

            Token operator = previous();
            Expr right = unary();

            return new Expr.Unary(
                operator,
                right
            );
        }

        return primary();
    }

    /*
     * primary → "false"
     *         | "true"
     *         | "nil"
     *         | NUMBER
     *         | STRING
     *         | flowLiteral
     *         | "(" expression ")" ;
     */
    private Expr primary() {

        if (match(FALSE)) {
            return new Expr.Literal(false);
        }

        if (match(TRUE)) {
            return new Expr.Literal(true);
        }

        if (match(NIL)) {
            return new Expr.Literal(null);
        }

        // River DSL flow literal.
        if (match(FLOW)) {
            return flowLiteral();
        }

        if (match(NUMBER, STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(LEFT_PAREN)) {

            Expr expr = expression();

            consume(
                RIGHT_PAREN,
                "Expect ')' after expression."
            );

            return new Expr.Grouping(expr);
        }

        throw error(
            peek(),
            "Expect expression."
        );
    }

    /*
     * flowLiteral →
     * "flow" "[" NUMBER ( "," NUMBER )* "]" ;
     *
     * Example:
     * flow[10, 6, 2, 0, 0, 0, 0, 0, 0, 0]
     */
    private Expr flowLiteral() {

        // FLOW has already been consumed by primary().
        consume(
            LEFT_SQUARE,
            "Expect '[' after 'flow'."
        );

        List<Double> values = new ArrayList<>();

        // Require at least one number.
        Token firstValue = consume(
            NUMBER,
            "Expect a number inside flow literal."
        );

        values.add((Double) firstValue.literal);

        // Read further comma-separated numbers.
        while (match(COMMA)) {

            Token value = consume(
                NUMBER,
                "Expect a number after ','."
            );

            values.add((Double) value.literal);
        }

        consume(
            RIGHT_SQUARE,
            "Expect ']' after flow values."
        );

        return new Expr.Flow(values);
    }

    // Checks whether current token matches any supplied type.
    private boolean match(TokenType... types) {

        for (TokenType type : types) {

            if (check(type)) {

                advance();
                return true;
            }
        }

        return false;
    }

    // Checks current token without consuming it.
    private boolean check(TokenType type) {

        if (isAtEnd()) {
            return false;
        }

        return peek().type == type;
    }

    // Moves forward one token.
    private Token advance() {

        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    // Returns true if current token is EOF.
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    // Current token.
    private Token peek() {
        return tokens.get(current);
    }

    // Most recently consumed token.
    private Token previous() {
        return tokens.get(current - 1);
    }

    // Requires a specific token type.
    private Token consume(
        TokenType type,
        String message
    ) {

        if (check(type)) {
            return advance();
        }

        throw error(
            peek(),
            message
        );
    }

    // Reports parser error.
    private ParseError error(
        Token token,
        String message
    ) {

        Lox.error(
            token,
            message
        );

        return new ParseError();
    }

    /*
     * Used later when parsing statements.
     * Helps the parser recover after an error.
     */
    private void synchronize() {

        advance();

        while (!isAtEnd()) {

            if (previous().type == SEMICOLON) {
                return;
            }

            switch (peek().type) {

                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }
}