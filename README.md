# RiverFlow DSL

RiverFlow is a domain-specific language (DSL) written in Java for describing and modelling river networks, rainfall response, and downstream water flow.

The project explores how programming-language concepts such as lexical analysis, recursive-descent parsing, abstract syntax trees, and custom language syntax can be applied to environmental modelling.

> **Status:** In active development.

---

## Overview

River systems can become difficult to describe as networks grow and multiple upstream rivers combine.

RiverFlow aims to provide a small, readable language for expressing these systems directly.

A river's response to rainfall can be represented using a custom flow literal:

```text
flow[10, 6, 2, 0, 0, 0, 0, 0, 0, 0]
```

The ten values represent the river's flow response over ten days.

The language is being extended to support named rivers and complete river networks:

```text
river Coxs {
    response: flow[10, 6, 2, 0, 0, 0, 0, 0, 0, 0];
}

river Kedumba {
    response: flow[8, 4, 1, 0, 0, 0, 0, 0, 0, 0];
}
```

---

## Current Features

RiverFlow currently includes:

- Lexical scanning and tokenisation
- Recursive-descent parsing
- Abstract Syntax Tree (AST) representation
- Visitor-based AST traversal
- Custom `flow[...]` literals
- Ten-day flow-response profiles
- Parser error reporting
- Standard arithmetic and comparison expressions inherited from the language core

---

## Language Example

A simple flow-response expression:

```text
flow[10, 6, 2, 0, 0, 0, 0, 0, 0, 0]
```

is tokenised by the scanner and converted by the parser into a dedicated `Flow` AST node.

Its current printed AST representation is:

```text
flow[10.0, 6.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]
```

---

## Architecture

RiverFlow uses a traditional language-processing pipeline:

```text
RiverFlow Source Code
        |
        v
     Scanner
        |
        v
      Tokens
        |
        v
      Parser
        |
        v
Abstract Syntax Tree
        |
        v
Interpreter / Simulation
```

### Scanner

The scanner performs lexical analysis.

It reads raw source code and converts characters into tokens that can be understood by the parser.

For example:

```text
flow[10, 6, 2]
```

is recognised conceptually as:

```text
FLOW
LEFT_SQUARE
NUMBER
COMMA
NUMBER
COMMA
NUMBER
RIGHT_SQUARE
```

### Parser

The parser is implemented using recursive descent.

It consumes the tokens produced by the scanner and constructs an Abstract Syntax Tree representing the structure of the program.

### Abstract Syntax Tree

The AST represents language constructs as Java objects.

The current expression hierarchy includes:

```text
Expr
├── Binary
├── Grouping
├── Literal
├── Unary
└── Flow
```

`Flow` is a custom AST node introduced specifically for RiverFlow.

---

## Flow Literals

A flow literal uses the following syntax:

```text
flow[value1, value2, ..., value10]
```

For example:

```text
flow[10, 6, 2, 0, 0, 0, 0, 0, 0, 0]
```

represents a ten-day response profile where the values correspond to successive days.

The grammar currently follows the form:

```text
flowLiteral → "flow" "[" NUMBER ( "," NUMBER )* "]" ;
```

Additional validation of the ten-day profile is planned as the language develops.

---

## Planned River Syntax

RiverFlow is being developed incrementally.

The intended syntax includes named river declarations such as:

```text
river Coxs {
    response: flow[10, 6, 2, 0, 0, 0, 0, 0, 0, 0];
}
```

and river confluences such as:

```text
river Jamison {
    response: flow[5, 3, 1, 0, 0, 0, 0, 0, 0, 0];
    inflow: Coxs <> Kedumba;
}
```

The `<>` operator is intended to represent the confluence of two upstream river systems.

---

## Technology

The project is currently implemented using:

- Java
- Recursive-descent parsing
- Abstract Syntax Trees
- Visitor design pattern
- Custom lexical analysis
- Domain-specific language design

---

## Project Structure

```text
riverflow-dsl/
│
├── README.md
├── .gitignore
│
└── com/
    └── craftinginterpreters/
        └── lox/
            ├── AstPrinter.java
            ├── Expr.java
            ├── Lox.java
            ├── Parser.java
            ├── Scanner.java
            ├── Token.java
            └── TokenType.java
```

As the project grows, the source layout and example programs may be reorganised into a more conventional project structure.

---

## Running the Project

### Requirements

- Java Development Kit (JDK)

Compile from the project root:

```bash
javac com/craftinginterpreters/lox/*.java
```

Run:

```bash
java com.craftinginterpreters.lox.Lox
```

Then enter a RiverFlow expression:

```text
flow[10, 6, 2, 0, 0, 0, 0, 0, 0, 0]
```

---

## Roadmap

Planned development includes:

- [x] Scanner
- [x] Recursive-descent expression parser
- [x] Abstract Syntax Tree
- [x] AST visitor
- [x] Custom flow literal
- [ ] Named river declarations
- [ ] River response definitions
- [ ] River confluence operator
- [ ] River network topology
- [ ] Outlet definitions
- [ ] Ten-day flow validation
- [ ] Rainfall input
- [ ] River-flow evaluation
- [ ] Dam modelling
- [ ] Simulation engine
- [ ] Example watershed models
- [ ] Automated tests

---

## Design Goals

RiverFlow is designed around three main goals:

**Readability**  
River systems should be understandable without requiring large amounts of general-purpose programming syntax.

**Domain-specific representation**  
Concepts such as flow profiles, rivers, confluences, and outlets should have meaningful language constructs.

**Extensibility**  
The language architecture should allow additional hydrological concepts to be introduced without redesigning the entire parser.

---

## Background

RiverFlow is built while exploring the implementation of interpreters and domain-specific programming languages.

The implementation follows established interpreter architecture, including scanning, parsing, AST construction, and visitor-based traversal, while extending those ideas with original syntax and AST structures for river-network modelling.

---

## Author

Developed as a programming-language and environmental modelling project using Java.