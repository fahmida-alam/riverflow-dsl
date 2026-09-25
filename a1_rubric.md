# A1 Rubric Explanation

This document is part of your first submission.  Complete it and include it in your submission zip, alongside your parser, example programs.

## How it works

The rubric explanation is the set of questions below.  The first set, the basic questions, is graded directly and is worth 10\% of your marks for this submission.  Answer them accurately to earn those marks.

The remaining sections ask one question for each of the other rubric items.  These are not graded directly, but your answers help your marker award you the marks for each rubric item, so write your answers below each question text in markdown format and point your marker to where the evidence lives in your submission.

## Basic questions (10)

1. Which chapter of the book did you use as the starting point for your solution?

### Your answer

Chapter 6, "Parsing Expressions", of Robert Nystrom's *Crafting Interpreters* was the main starting point for my parser. My implementation also builds on the scanner from Chapter 4 and the AST and Visitor structure from Chapter 5.

2. What is the "working folder", and what command(s) compile your parser?

### Your answer

The working folder is the top-level `COMP3000_Assignment1` folder, which contains the `com`, `examples`, `README.md`, and `a1_rubric.md` files/folders.

From this folder, the parser can be compiled with:

```powershell
javac com\craftinginterpreters\lox\*.java
```

The three example programs can then be run with:

```powershell
java com.craftinginterpreters.lox.Lox examples/basic_confluence.lox
java com.craftinginterpreters.lox.Lox examples/multi_stage_confluence.lox
java com.craftinginterpreters.lox.Lox examples/grouped_confluence.lox
```

3. What literal in your language represents a river that gets 10L/s of flow on the first day after 1mm of rainfall?

### Your answer

A flow literal beginning with `10` represents a river receiving 10 L/s of flow on the first day after 1 mm of rainfall. For example:

```text
flow[10 -> 20 @ 3 ~ 0.5]
```

In this literal, `10` is the flow on Day 1, `20` is the peak flow, `@ 3` specifies that the peak occurs on Day 3, and `~ 0.5` is the recession factor used after the peak.

The rainfall itself is not represented directly in Submission One; the `flow` literal describes the river's response to the rainfall event.

4. What symbol in your language shows two rivers combine, and is it a "unary", "binary", or "literal"?

### Your answer

The `<>` symbol represents two rivers combining at a confluence. For example:

```text
Nepean <> Grose
```

It is a **binary operator** because it combines two operands: a river expression on the left and a river expression on the right.

In the AST, a confluence is represented by `Expr.Confluence`, which stores a left expression and a right expression.

5. Does your language include statements, or is it an expression language?

### Your answer

My language includes both statements and expressions.

River declarations and outlet declarations are statements represented in `Stmt.java`. For example:

```text
river Nepean {
    response: flow[2 -> 18 @ 3 ~ 0.5];
}

outlet Hawkesbury;
```

Expressions are represented in `Expr.java`. These include `Flow`, `RiverRef`, `Confluence`, and `Grouping` expressions.

Therefore, RiverFlow is not only an expression language; it uses statements to define the river network and expressions to represent flow responses and river relationships.

6. In your language, how long does it take all the water to work through a river system after 1 day of rain?

### Your answer

In my language, the response to one day of rainfall is modelled over a maximum 10-day period. This follows the domain assumption that all water from a rainfall event will make its way into the river within 10 days.

The `flow` literal describes how that response develops over this period. For example:

```text
flow[2 -> 18 @ 3 ~ 0.5]
```

starts at 2 L/s on Day 1, rises to a peak of 18 L/s on Day 3, and then follows the specified recession factor for the remaining response period. After the 10-day response window, that rainfall event contributes no further water.

## Log-book submissions (10)

Which file in the zip are your log-book entries and when did you make them?  Your teacher needs to have seen them during the semester.

### Your answer

_Write your answer here._

## Grammar given in the document in Nystrom's notation (20)

Provide the grammar for your language, and how does each of your example programs parse according to it?

### Your answer

The complete grammar for my RiverFlow language is:

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

A `program` consists of zero or more declarations followed by the end of the file (EOF). A declaration can either define a river or identify the final outlet.

A `riverDeclaration` gives the river a name and requires a `response` containing a `flowLiteral`. The optional `inflow` section describes upstream rivers that combine to feed that river.

A `confluence` uses the `<>` binary operator to combine river references. Parentheses can be used to explicitly group confluences.

A `flowLiteral` describes the rainfall response of a river. The four numeric parameters represent the Day 1 flow, peak flow, peak day, and recession factor, respectively. For example:

```text
flow[2 -> 18 @ 3 ~ 0.5]
```

represents a response starting at 2 L/s on Day 1, rising to 18 L/s on Day 3, then receding using a factor of 0.5.

### How `basic_confluence.lox` parses

`basic_confluence.lox` demonstrates the simplest complete river network in the
language.

The `Nepean` and `Grose` blocks each match `riverDeclaration`. Their `response`
fields contain `flowLiteral` expressions and they have no `inflow`, so they
represent root rivers.

The `Hawkesbury` block also matches `riverDeclaration`, but includes:

```text
inflow: Nepean <> Grose;
```

`Nepean` and `Grose` each match `confluencePrimary` as river identifiers. The
`<>` operator combines them using the `confluence` rule, producing an
`Expr.Confluence` node with `Nepean` as the left river reference and `Grose`
as the right river reference.

Finally:

```text
outlet Hawkesbury;
```

matches `outletStatement` and identifies `Hawkesbury` as the output river of
the system.

The resulting network represented by the program is:

```text
Nepean ──┐
         ├──> Hawkesbury ──> outlet
Grose ───┘
```

### How `multi_stage_confluence.lox` parses

`multi_stage_confluence.lox` demonstrates a river network containing multiple
stages of confluences.

`Yarra`, `Plenty`, and `Merri` each match `riverDeclaration`. Their declarations
contain a `flowLiteral` response but no `inflow`, so they represent root rivers.

`Maribyrnong` contains:

```text
inflow: Plenty <> Merri;
```

`Plenty` and `Merri` each match `confluencePrimary`, and the `<>` operator
combines them into an `Expr.Confluence`.

`Melbourne` contains:

```text
inflow: Yarra <> Maribyrnong;
```

This creates another `Expr.Confluence`. Unlike the first confluence,
`Maribyrnong` already represents a downstream river receiving water from
`Plenty` and `Merri`. This demonstrates that the output of one part of the
network can feed into another confluence.

Finally:

```text
outlet Melbourne;
```

matches `outletStatement` and identifies `Melbourne` as the output river.

The resulting network is:

```text
Plenty ──┐
         ├──> Maribyrnong ──┐
Merri ───┘                  │
                            ├──> Melbourne ──> outlet
Yarra ──────────────────────┘
```

### How `grouped_confluence.lox` parses

`grouped_confluence.lox` demonstrates explicit grouping of confluences in a
larger river network.

`Murrumbidgee`, `Molonglo`, `Cotter`, and `Queanbeyan` each match
`riverDeclaration`. They contain a `flowLiteral` response but no `inflow`, so
they represent root rivers.

`Canberra` contains:

```text
inflow: (Molonglo <> Cotter) <> Queanbeyan;
```

Inside the parentheses, `Molonglo <> Cotter` matches the `confluence` rule and
creates an `Expr.Confluence`. The parentheses match `confluencePrimary` and
wrap this inner confluence in an `Expr.Grouping`.

The grouped result is then combined with `Queanbeyan` using another `<>`,
creating an outer `Expr.Confluence`. Its structure is:

```text
<>
├── Grouping
│   └── <>
│       ├── Molonglo
│       └── Cotter
└── Queanbeyan
```

`LowerRiver` contains:

```text
inflow: Murrumbidgee <> Canberra;
```

This creates another `Expr.Confluence` and demonstrates that the grouped
network can itself feed into a downstream river.

Finally:

```text
outlet LowerRiver;
```

matches `outletStatement` and identifies `LowerRiver` as the output river.

The resulting river network is:

```text
Molonglo ──┐
           ├──┐
Cotter ────┘  │
              ├──> Canberra ──┐
Queanbeyan ───┘               │
                              ├──> LowerRiver ──> outlet
Murrumbidgee ─────────────────┘
```

## Three example programs (20)

Provide your three example programs here and identify which files in your zip contain them.

### Your answer

_Write your answer here._

## Parser written in Java based on Lox codebase (20)

Which chapter of the book is your parser based on?  What did you add beyond the Chapter 6 code, and where is that explained?

### Your answer

_Write your answer here._

## Uniqueness and Creativity (20)

What did you do beyond the in-class work?  Point your marker to where it lives in your submission.

### Your answer

_Write your answer here._
