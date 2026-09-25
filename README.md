# RiverFlow DSL

RiverFlow is a domain-specific language (DSL) written in Java for describing river networks, rainfall-response behaviour, and downstream river topology.

The project explores how programming-language concepts such as lexical analysis, recursive-descent parsing, abstract syntax trees, statements, expressions, and custom language syntax can be applied to river-system modelling.

> **Status:** Assignment 1 parser implementation complete. Evaluation and simulation are planned for later development.

---

## Overview

River networks can become difficult to describe as multiple upstream rivers combine and feed larger downstream systems.

RiverFlow provides a small domain-specific syntax for describing:

- named rivers
- rainfall-response behaviour
- upstream river confluences
- grouped river topology
- final network outlets

A river can be declared using:

```text
river Nepean {
    response: flow[2 -> 18 @ 3 ~ 0.5];
}
```

The response is represented using a custom flow literal:

```text
flow[2 -> 18 @ 3 ~ 0.5]
```

The four values describe:

- `2` — Day 1 starting flow
- `18` — peak flow
- `@ 3` — peak occurs on Day 3
- `~ 0.5` — recession factor after the peak

RiverFlow also provides the `<>` operator for representing a confluence:

```text
Nepean <> Grose
```

---

## Language Example

A complete RiverFlow program can describe several rivers and how they connect:

```text
river Nepean {
    response: flow[2 -> 18 @ 3 ~ 0.5];
}

river Grose {
    response: flow[4 -> 12 @ 2 ~ 0.4];
}

river Hawkesbury {
    response: flow[3 -> 20 @ 4 ~ 0.6];
    inflow: Nepean <> Grose;
}

outlet Hawkesbury;
```

This describes two upstream rivers, `Nepean` and `Grose`, joining to feed `Hawkesbury`.

The resulting topology is:

```text
Nepean ──┐
         ├──> Hawkesbury ──> outlet
Grose ───┘
```

---

## Current Features

RiverFlow currently includes:

- lexical scanning and tokenisation
- recursive-descent parsing
- Abstract Syntax Tree (AST) representation
- Visitor-based AST traversal
- named river declarations
- custom `flow[...]` response literals
- dedicated `<>` confluence operator
- river references
- optional upstream inflow declarations
- grouped confluence expressions
- outlet declarations
- parser error reporting and synchronisation
- AST printing for inspecting parsed programs
- parsing of complete multi-river programs

The current implementation focuses on **syntax and parsing**. Numerical evaluation of river flow is intended for later development.

---

## Flow Literals

RiverFlow uses a domain-specific flow literal:

```text
flow[start -> peak @ peakDay ~ recession]
```

For example:

```text
flow[2 -> 18 @ 3 ~ 0.5]
```

represents a response that:

1. begins at 2 L/s on Day 1,
2. rises towards a peak of 18 L/s,
3. reaches that peak on Day 3,
4. then follows a recession factor of 0.5.

The parser stores these four parameters in a dedicated `Expr.Flow` AST node.

The current parser recognises the structure of the response. Numerical simulation of the response is outside the current Assignment 1 parser stage.

---

## River Declarations

A river is declared using the `river` keyword:

```text
river Nepean {
    response: flow[2 -> 18 @ 3 ~ 0.5];
}
```

Every river declaration contains:

- a river name
- a required response

A river may additionally contain an upstream inflow:

```text
river Hawkesbury {
    response: flow[3 -> 20 @ 4 ~ 0.6];
    inflow: Nepean <> Grose;
}
```

River declarations are represented by `Stmt.River`.

---

## Confluences

The `<>` operator represents two upstream river expressions joining at a confluence.

```text
Nepean <> Grose
```

A confluence is represented by a dedicated `Expr.Confluence` AST node containing a left and right expression.

This separates river topology from ordinary arithmetic operations.

Confluences can also be chained:

```text
A <> B <> C
```

and explicitly grouped:

```text
(A <> B) <> C
```

or:

```text
A <> (B <> C)
```

Grouping is represented using `Expr.Grouping`.

---

## Outlets

The final river in a network can be identified using an outlet statement:

```text
outlet Hawkesbury;
```

Outlet declarations are represented by `Stmt.Outlet`.

---

## Grammar

The main RiverFlow grammar is:

```text
program           -> declaration* EOF ;

declaration       -> riverDeclaration
                   | outletStatement ;

riverDeclaration  -> "river" IDENTIFIER "{"
                     "response" ":" flowLiteral ";"
                     ( "inflow" ":" confluence ";" )?
                     "}" ;

outletStatement   -> "outlet" IDENTIFIER ";" ;

confluence        -> confluencePrimary
                     ( "<>" confluencePrimary )* ;

confluencePrimary -> IDENTIFIER
                   | "(" confluence ")" ;

flowLiteral       -> "flow" "["
                     NUMBER "->"
                     NUMBER "@"
                     NUMBER "~"
                     NUMBER
                     "]" ;
```

---

## Architecture

RiverFlow follows a traditional language-processing pipeline:

```text
RiverFlow Source
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
 AST Inspection
       |
       v
Future Evaluation / Simulation
```

### Scanner

`Scanner.java` performs lexical analysis.

It converts RiverFlow source code into tokens understood by the parser.

For example:

```text
flow[2 -> 18 @ 3 ~ 0.5]
```

contains domain-specific tokens representing:

```text
FLOW
LEFT_SQUARE
NUMBER
RISE
NUMBER
AT
NUMBER
RECESSION
NUMBER
RIGHT_SQUARE
```

The scanner also recognises RiverFlow keywords including:

```text
river
response
flow
inflow
outlet
```

and the confluence operator:

```text
<>
```

### Parser

`Parser.java` uses recursive-descent parsing based on the approach developed in Chapter 6 of *Crafting Interpreters*.

The parser has been extended to parse complete RiverFlow programs containing multiple declarations.

It handles:

- river declarations
- flow literals
- river references
- confluences
- grouped confluences
- outlet statements

### Abstract Syntax Tree

RiverFlow extends the Lox AST with domain-specific structures.

The expression hierarchy includes:

```text
Expr
├── Binary
├── Grouping
├── Literal
├── Unary
├── Flow
├── RiverRef
└── Confluence
```

The statement hierarchy includes:

```text
Stmt
├── River
└── Outlet
```

`Flow`, `RiverRef`, `Confluence`, `River`, and `Outlet` provide AST representations for RiverFlow-specific concepts.

### AST Printer

`AstPrinter.java` supports RiverFlow expressions and statements so the result of parsing can be inspected directly.

For example:

```text
inflow: Nepean <> Grose;
```

is represented structurally as:

```text
(<> Nepean Grose)
```

This provides a way to verify that the parser has constructed the intended tree before evaluation is implemented.

---

## Project Structure

```text
COMP3000_Assignment1/
│
├── README.md
├── a1_rubric.md
├── LOGBOOK.md
├── .gitignore
│
├── examples/
│   ├── basic_confluence.lox
│   ├── multi_stage_confluence.lox
│   └── grouped_confluence.lox
│
├── logbook-evidence/
│   ├── COMP3000_logbook.pdf
│   ├── week-03-logo-graphics.png
│   └── week-03-lox-playground.png
│
└── com/
    └── craftinginterpreters/
        ├── lox/
        │   ├── AstPrinter.java
        │   ├── Expr.java
        │   ├── Lox.java
        │   ├── Parser.java
        │   ├── Scanner.java
        │   ├── Stmt.java
        │   ├── Token.java
        │   └── TokenType.java
        │
        └── tool/
            └── GenerateAst.java
```

---

## Example Programs

Three example RiverFlow programs are included in the `examples` directory.

### Basic Confluence

```text
examples/basic_confluence.lox
```

Demonstrates two root rivers combining into a downstream river.

### Multi-Stage Confluence

```text
examples/multi_stage_confluence.lox
```

Demonstrates multiple levels of river confluences.

### Grouped Confluence

```text
examples/grouped_confluence.lox
```

Demonstrates explicit grouping within a larger river network.

---

## Running the Project

### Requirements

- Java Development Kit (JDK)
- Java 21 or compatible version

From the top-level `COMP3000_Assignment1` folder, compile the RiverFlow implementation with:

```powershell
javac com\craftinginterpreters\lox\*.java
```

Run the three example programs with:

```powershell
java com.craftinginterpreters.lox.Lox examples/basic_confluence.lox

java com.craftinginterpreters.lox.Lox examples/multi_stage_confluence.lox

java com.craftinginterpreters.lox.Lox examples/grouped_confluence.lox
```

If the generated AST classes need to be regenerated, compile and run the AST generator first:

```powershell
javac com\craftinginterpreters\tool\GenerateAst.java

java com.craftinginterpreters.tool.GenerateAst com/craftinginterpreters/lox

javac com\craftinginterpreters\lox\*.java
```

---

## Example AST Output

Running `basic_confluence.lox` produces output representing the parsed program:

```text
(river Nepean flow[2.0 -> 18.0 @ 3.0 ~ 0.5])
(river Grose flow[4.0 -> 12.0 @ 2.0 ~ 0.4])
(river Hawkesbury flow[3.0 -> 20.0 @ 4.0 ~ 0.6] inflow (<> Nepean Grose))
(outlet Hawkesbury)
```

This output demonstrates that the parser recognises river declarations, flow literals, confluences, and outlet statements.

---

## Development Roadmap

### Assignment 1

- [x] Scanner
- [x] Recursive-descent parser
- [x] Abstract Syntax Tree
- [x] Visitor structure
- [x] Custom flow literal
- [x] Named river declarations
- [x] River response definitions
- [x] River reference expressions
- [x] River confluence operator
- [x] Grouped confluences
- [x] Multi-stage river topology
- [x] Outlet declarations
- [x] AST printing
- [x] Three example river networks
- [x] Grammar documentation
- [x] Development log

### Future Development

- [ ] River-flow evaluation
- [ ] Rainfall input
- [ ] Response calculation
- [ ] Ten-day response semantics
- [ ] Dam modelling
- [ ] River-network simulation
- [ ] Additional validation
- [ ] Automated tests

---

## Design Goals

RiverFlow is designed around three main goals.

### Readability

River networks should be understandable without requiring large amounts of general-purpose programming syntax.

For example:

```text
inflow: Nepean <> Grose;
```

expresses a river confluence directly.

### Domain-Specific Representation

Important concepts such as river responses, confluences, river declarations, and outlets have dedicated syntax and AST representations.

### Extensibility

The language architecture separates scanning, parsing, AST representation, and future evaluation so additional river-system concepts can be introduced incrementally.

---

## Background

RiverFlow was developed while studying programming-language implementation in COMP3000.

The project builds on the architecture introduced in Robert Nystrom's *Crafting Interpreters*, including scanning, recursive-descent parsing, AST generation, and Visitor-based traversal.

These concepts were extended with RiverFlow-specific syntax and AST structures for rainfall responses and river-network topology.

---

## Author

Developed by Fahmida Alam, student ID 48588466, as a COMP3000 programming-languages project using Java.