package io.provs.entry

/**
 * Calls a static method of a class.
 * Only methods are supported with either no parameters or with one vararg parameter of type String.
 * Methods with a vararg parameter must be called with at least one argument.
 *
 * @param args specify class and (optionally) method and parameters, in detail:
 * @param args[0] class to be called
 * @param args[1] (optional) static method of the class; if not specified, the "main" method is used
 * @param args[2...] String parameters that are passed to the method, only applicable if method has vararg parameter
 */
fun main(vararg args: String) {

    if (args.isNotEmpty()) {
        val className = args[0]

        val jClass = Class.forName(className)

        val parameterTypeStringArray = arrayOf<Class<*>>(
            Array<String>::class.java
        )
        val method = if (args.size == 1) jClass.getMethod("main", *parameterTypeStringArray) else
            (if (args.size == 2 && args[1] != "main") jClass.getMethod(args[1])
            else jClass.getMethod(args[1], *parameterTypeStringArray))

        if ((args.size == 1) || (args.size == 2 && args[1] == "main")) {
            method.invoke(null, emptyArray<String>())
        } else if (args.size == 2) {
            method.invoke(null)
        } else {
            method.invoke(null, args.drop(2).toTypedArray())
        }
    } else {
        println("Usage: <packageName.className> <static methodName>")
    }
}
