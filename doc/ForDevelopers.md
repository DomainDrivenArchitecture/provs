This page provides information for developers.

# Tasks

## What is a task ?

A task is the **basic execution unit** in provs. When executed, each task produces exactly one result (line) with either success or failure.

The success or failure is computed automatically in the following way:
* a **task** fails if it calls subtasks and if at least one subtask has failed
* a **taskWithResult** works the same except that it requires an additional result to be returned which is also included in the success calculation
* a task defined with **optional** (i.e. `= optional { /* ... */ }` always returns success (even if there are failing subtasks)
* **requireLast** defines a task which must provide an explicit result and solely this result counts for success calculation

## Task declaration 

### Recommended way

A task can be declared by 

```kotlin
fun Prov.myCustomTask() = task { /* ... code and subtasks come here ... */ }
// e.g.
fun Prov.myEchoTask() = task { 
    cmd("echo hello world!") 
}
```

The task will succeed if all sub-tasks (called tasks during execution) have succeeded resp. if no sub-task was called.  

### Alternative ways

The following ways are equivalent but are more verbose:

```kotlin
// Redundant declaration of the return type (ProvResult), which is already declared by task
fun Prov.myCustomTask(): ProvResult = task { /* ... code and subtasks come here ... */ }

// Redundant parentheses behind task
fun Prov.myCustomTask() = task() { /* ... code and subtasks come here ... */ }

// Redundant definition of the task name, but could be used to output a different task name  
fun Prov.myCustomTask() = task("myCustomTask") { /* ... code and subtasks come here ... */ }

// Functionally equal, but with additional curly brackets 
fun Prov.myCustomTask() { task { /* ... code and subtasks come here ... */ } }
```

Btw, the following lines and WILL NOT work as expected. 
Due to too much lamda nesting, the code within the task is NOT executed:

```kotlin
fun Prov.myCustomTask() = { task { /* ... code and subtasks come here ... */ } }
fun Prov.myCustomTask() {{ task { /* ... code and subtasks come here ... */ } }}
```

### Add custom results

If you want to add a result explicitly, you can use method `addResultToEval`.
This maxy be used e.g. to add explicitly an error line, like in: 

```kotlin
fun Prov.myCustomTask() = task {
    /* some other code ... */
    addResultToEval(ProvResult(false, err = "my error msg"))
    /* some other code ... */
}
```
or alternatively you can use `taskWithResult`.

#### TaskWithResult

In case you want to include the return value (of type `ProvResult`) of a task to be added to the evaluation, 
you can use `taskWithResult` instead of `task` and return the value, e.g. like 

```kotlin
fun Prov.myEchoTask() = taskWithResult { 
    cmd("echo hello world!") 
    // ...
    ProvResult(false, "Error: ... error message ...")  // will be the returned as return value and included in the evaluation 
}
```

IMPORTANT: the value you want to return must be placed at the end of the lambda code (as usual in functional programming)!
The following will NOT work as expected:

```kotlin
fun Prov.myEchoTask() = taskWithResult {
    ProvResult(false, "Error: ... error message ...")  // will be ignored
    // the result from the call below (i.e. from task "cmd") will be returned by myEchoTask,
    // which is redundant as its result is already included in the evaluation anyway. 
    cmd("echo hello world!")  
}
```


### Task output

If a task is run e.g. with `local().myEchoTask()`, it will produce output like
```
>  Success -- myEchoTask 
--->  Success -- cmd [/bin/bash, -c, echo hello world!]
```


## Call hierarchy

In the following link you can find an example of a sequence diagram when provisioning a desktop:

[ProvisionDesktopSequence.md](ProvisionDesktopSequence.md)


## Create a provs jar-file

* Clone this repo
* Build the jar-file by `./gradlew uberjarDesktop`
* In folder build/libs you'll find the file `provs-desktop.jar`

This uberjar is a Java jar-file including all required dependencies.
