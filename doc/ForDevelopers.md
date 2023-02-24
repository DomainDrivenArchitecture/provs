# Information for developers

## Create a provs jar-file

* Clone this repo
* Build the jar-file by `./gradlew uberjarDesktop`
* In folder build/libs you'll find the file `provs-desktop.jar`

This uberjar is a Java jar-file including all required dependencies.

## Task

```kotlin
fun Prov.provisionK8s() = task { /* ... code and subtasks come here ... */ }
```

If you're having a deeper look into the provs code, you'll see regularly a task definition like this and might wonder ...

### What is a task ?

A task is the **basic execution unit** in provs. When executed, each task produces exactly one result (line) with either success or failure.

The success or failure is computed automatically in the following way:
* a **task** fails if it calls subtasks and if at least one subtask has failed
* a **taskWithResult** works the same except that it requires an additional result to be returned which is also included in the success calculation
* a task defined with **optional** (i.e. `= optional { /* ... */ }` always returns success (even if there are failing subtasks)
* **requireLast** defines a task which must provide an explicit result and solely this result counts for success calculation


## Call hierarchy

In the following link you can find an example of a sequence diagram when provisioning a desktop:

[ProvisionDesktopSequence.md](ProvisionDesktopSequence.md)
