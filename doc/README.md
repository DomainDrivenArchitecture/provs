This repository holds the documentation of the provs framework.

# Design principles

## "Implarative" 

Configuration management tools are usually classified as either **imperative** or **declarative**. 
Imperative means that you define the steps which are necessary to achieve the goal. 
Declarative means that you just define the state which you want to achieve, and the tooling figures out itself how this goal is achieved. 

The provs framework is aimed to offer the best of both worlds. 
Based on the imperative paradigm it offers advantages as: full control of execution order, clear control flow, 
all kinds of looping and conditional constructs, easy debugging. 
Furthermore, you are not limited by a DSL, you can make use of the full power of shell commands.  

On the other hand, the built-in functions of the provs framework also provide important advantages of the declarative paradigm, as for example idempotence resp. quasi-idempotence. 

## Idempotence vs quasi-idempotence

Idempotence means that you can run the same function once or several times without problems, you'll always get the same result.  
However, there are cases where you don't want strict idempotence. E.g. if you are installing a program or cloning a git repo a second time, you might want to get just the latest version,
even if an older version has already been installed earlier. This behavior is also known as quasi-idempotence. 
The provs framework uses quasi-idempotence where "real" idempotence is not possible or does not make sense.

In the following document we describe how we implement idempotence: 

https://gitlab.com/domaindrivenarchitecture/overview/-/blob/master/adr-provs/quasi-idempotence.md

