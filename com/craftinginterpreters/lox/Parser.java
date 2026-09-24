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

    // program -> riverDeclaration* EOF ;
    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();

        while (!isAtEnd()) {
            try {
                statements.add(riverDeclaration());
            } catch (ParseError error) {
                synchronize();
            }
        }

        return statements;
    }

    // riverDeclaration -> "river" IDENTIFIER "{"
    //                     "response" ":" flowLiteral ";"
    //                     ( "inflow" ":" confluence ";" )?
    //                     "}" ;
    private Stmt riverDeclaration() {
        consume(RIVER, "Expect 'river'.");

        Token name = consume(
            IDENTIFIER,
            "Expect river name."
        );

        consume(
            LEFT_BRACE,
            "Expect '{' after river name."
        );

        consume(
            RESPONSE,
            "Expect 'response' inside river declaration."
        );

        consume(
            COLON,
            "Expect ':' after 'response'."
        );

        if (!match(FLOW)) {
            throw error(
                peek(),
                "Expect flow literal after 'response:'."
            );
        }

        Expr.Flow response = flowLiteral();

        consume(
            SEMICOLON,
            "Expect ';' after flow response."
        );

        Expr inflow = null;

        if (match(INFLOW)) {
            consume(
                COLON,
                "Expect ':' after 'inflow'."
            );

            inflow = confluence();

            consume(
                SEMICOLON,
                "Expect ';' after inflow."
            );
        }

        consume(
            RIGHT_BRACE,
            "Expect '}' after river declaration."
        );

        return new Stmt.River(
            name,
            response,
            inflow
        );
    }

    // confluence -> riverReference ( "<>" riverReference )* ;
    private Expr confluence() {
        Expr expr = riverReference();

        while (match(CONFLUENCE)) {
            Expr right = riverReference();

            expr = new Expr.Confluence(
                expr,
                right
            );
        }

        return expr;
    }

    // riverReference -> IDENTIFIER ;
    private Expr riverReference() {
        Token name = consume(
            IDENTIFIER,
            "Expect river name in inflow."
        );

        return new Expr.RiverRef(name);
    }

    // expression -> equality ;
    private Expr expression() {
        return equality();
    }

    // equality -> comparison ( ( "!=" | "==" ) comparison )* ;
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

    // comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
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

    // term -> factor ( ( "-" | "+" ) factor )* ;
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

    // factor -> unary ( ( "/" | "*" ) unary )* ;
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

    // unary -> ( "!" | "-" ) unary | primary ;
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

    // primary -> "false"
    //          | "true"
    //          | "nil"
    //          | NUMBER
    //          | STRING
    //          | flowLiteral
    //          | "(" expression ")" ;
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

    // flowLiteral -> "flow" "[" NUMBER "->" NUMBER "@" NUMBER "~" NUMBER "]" ;
    private Expr.Flow flowLiteral() {
        consume(
            LEFT_SQUARE,
            "Expect '[' after 'flow'."
        );

        Token start = consume(
            NUMBER,
            "Expect starting flow."
        );

        consume(
            RISE,
            "Expect '->' after starting flow."
        );

        Token peak = consume(
            NUMBER,
            "Expect peak flow after '->'."
        );

        consume(
            AT,
            "Expect '@' after peak flow."
        );

        Token day = consume(
            NUMBER,
            "Expect peak day after '@'."
        );

        consume(
            RECESSION,
            "Expect '~' after peak day."
        );

        Token recession = consume(
            NUMBER,
            "Expect recession factor after '~'."
        );

        consume(
            RIGHT_SQUARE,
            "Expect ']' after flow literal."
        );

        return new Expr.Flow(
            (Double) start.literal,
            (Double) peak.literal,
            (Double) day.literal,
            (Double) recession.literal
        );
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }

        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

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
                case RIVER:
                    return;
            }

            advance();
        }
    }
}