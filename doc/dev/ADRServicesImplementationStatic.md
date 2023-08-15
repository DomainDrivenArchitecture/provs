# ADR: We implement domain services static

Domain services can be implemented either as object (and composed like done in spring / example1 ) or with extension 
function and composed static (see example2).

## example1
```kotlin
class DesktopServie(val aptApi: AptApi, val prov: Prov) {
    fun provisionIdeDesktop(onlyModules: List<String>? = null) {
        prov.task {
            if (onlyModules == null) {
                aptApi.aptInstall(OPEN_VPM)
            }
        }
    }
}
```

## example2
```kotlin
fun Prov.provisionIdeDesktop(onlyModules: List<String>? = null) {
    if (onlyModules == null) {
        aptInstall(OPEN_VPM)
    }
}
```

## Decission

We use extension function and composed static.

## Reason

1. Similar to composed objects we can easily mock `aptInstall` in tests. Both solutions are equivalent.
2. Inheritance in case of composed objects we can solve by static composition.
3. Object composition we can solve by static composition.

There is no reason left to change the current implementd pattern.
