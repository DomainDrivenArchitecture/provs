package io.provs.entry

/**
 * Calls a static method of a class.
 * Only methods are supported with either no parameters or with one vararg parameter of type String.
 * Methods with a vararg parameter must be called with at least one argument.
 *
 * @param args specify class and (optionally) method and parameters, in detail:
 * @param args[0]  fully-qualified class name of the class to be called
 * @param args[1] (optional) static method of the class with a vararg parameter of type String; if not specified, the "main" method is used
 * @param args[2...] (optional) String parameters that are passed to the method; can be only used if method name (args[1]) is provided
 */
fun main(vararg args: String) {

    if (args.isNotEmpty()) {
        val className = args[0]

        val jClass = Class.forName(className)

        val parameterTypeStringArray = arrayOf<Class<*>>(
            Array<String>::class.java
        )
        val method = if (args.size == 1) {
            jClass.getMethod("main", *parameterTypeStringArray)
        } else {
            jClass.getMethod(args[1], *parameterTypeStringArray)
        }

        if (args.size <= 2) {
            method.invoke(null, emptyArray<String>())
        } else {
            method.invoke(null, args.drop(2).toTypedArray())
        }
    } else {
        println("Usage: <packageName.className> <optional static methodName> <optional parameters ... >")
    }
}
