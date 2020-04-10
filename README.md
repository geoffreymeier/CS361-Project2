# Project 2: Nondeterministic Finite Automata

* Author: Parker Erway and Geoffrey Meier
* Class: CS361-002
* Semester: Spring 2020

## Overview

This program models *non*-deterministic finite automota based on the project
spefification. It is a natural extension of the previous project, which
modelled deterministic finite automota.

## Compiling and Using


To compile `fa.nfa.NFADriver` from the top directory of these files:

```
[you@onyx]$ javac fa/nfa/NFADriver.java
```

To run fa.nfa.NFADriver:
```
[you@onyx]$ java fa.nfa.NFADriver ./tests/p2tc0.txt
```

## Discussion

This project was very straightforward. We copied the provided DFA code, and
adapted it for the NFA project. Much of it carries over with no changes. 
Our first solution for DFS was iterative, later changed to recursive after
it was made clear on Piazza that this is a requirement. 

## Testing

We tested our program against the provided test files, as well as some
test files that we created.
