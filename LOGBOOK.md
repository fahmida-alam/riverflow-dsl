# COMP3000 Development Log

This log records my weekly learning, workshop activities, and progress towards
Assignment 1.

## Week 1 - Team Based Learning

**Logbook entry date:** 28 July 2026

### Before the workshop

I reviewed the Team Based Learning (TBL) overview and learned how the weekly
preparation, readiness tests, and team activities would work.

### During the workshop

I completed iRAT 1 and participated in the team activities. We also worked on
our team contract, where we discussed expectations and how we would work
together throughout the semester.

### After the workshop

There was no major Assignment 1 development this week. My focus was on
understanding the unit structure and the team-based workshop process.

### What I learned

I understood how the TBL process would work and what was expected from me
before and during future workshops.


## Week 2 - Compiling and Interpreting Material

**Logbook entry date:** 4 August 2026

### Before the workshop

I attended the Week 2 lecture and read Chapters 1 and 2 of *Crafting
Interpreters*. I reviewed little languages, the compiler/interpreter pipeline,
and the difference between compilers and interpreters.

### During the workshop

I completed iRAT 2 and worked with my team using Regexr to experiment with
regular expressions. We tried exact matches, alternatives, repeated patterns,
and capture groups on sample text.

We also explored extracting particular words from dialogue and discussed
whether regular expressions are a complete language. We identified features
such as capture groups for storing matched values and repetition using `*` and
`+`, but found that ordinary regex does not provide the same general control
flow as a general-purpose programming language.

### After the workshop

I reviewed how the compiler/interpreter pipeline connects to the assignment.
In particular, I understood that the scanner converts source code into tokens
and the parser later turns those tokens into a structured tree.

### What I learned

I learned that a language can be small and specialised for one domain. This
later helped me think about designing a domain-specific language for river
systems.

## Week 3 - Lox

**Logbook entry date:** 11 August 2026

### Before the workshop

I attended the Week 3 lecture, read Chapter 3 of *Crafting Interpreters*, and
experimented with Lox before the workshop. I prepared for the iRAT and tRAT by
reviewing Lox features such as expressions, variables, functions, and classes.

### During the workshop

I completed iRAT 3 and participated in the tRAT with my team. I experimented
with Lox in the online playground by writing and running small programs using
variables, functions, loops, and output.

For the application exercise, I used Lox to generate Logo turtle-graphics
commands. I created my own geometric star design, compared it with my
teammates' work and other workshop designs, and presented my result to the
class.

![Week 3 Lox Playground practice](logbook-evidence/week-03-lox-playground.png)
![Week 3 Logo turtle graphics](logbook-evidence/week-03-logo-graphics.png)

### After the workshop

I continued experimenting with Lox and reviewed how a program can generate
another program. This helped me become more familiar with Lox syntax before
starting the scanner and parser implementation in later weeks.

### What I learned

I became more comfortable writing Lox programs and understood how loops and
functions can generate repeated output instead of manually writing every
command.