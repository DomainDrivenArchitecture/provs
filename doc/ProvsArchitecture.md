# Bounded Contexts

```plantuml
@startuml
package "configuration" {
  [TargetCommand]
}

package "desktop" {
    [DesktopCommand]
}

package "server" {
    [ServerCommand]
}

server ..> configuration
desktop ..> configuration

using ..> used
@enduml
```

# DDD in Provs

```plantuml
@startuml
'https://plantuml.com/class-diagram

package application {
    class Application
}

package domain {
    class Service {
      cleanup (): means remove parts (install & configuration) to enable e.g. re-provision in some cases, results in an error otherwise.
      provision (): means install & configure.
      install (): install packages, files but not configuration files. Fire & forget (re-) installation is possible.
      configure (): install and apply configuration.
    }
    class Domain
}
Application ..> Service
Application ..> Domain

package infrastructure {
    class Repository
    class Provs {

        create<type>(): create a new item - might also have a parameter like: skipIfExisting
        delete<type>(): delete an item - might also have a parameter like: failIfNotExisting
        check<type>(): check if an item exists (returns true resp. false)
        check<type>Content(): check content of item (returns true resp. false), e.g. checkFileContent
        check<type>Runs(): check if container/service is running (returns true resp. false)
        start<type>(): start e.g. a container (or a service)
        stop<type>(): stop a container/service
        remove<type>(): remove e.g. a container or image

        —————————————————————————————————————————————————————— () 
        Remark1: types can be e.g.: "file", "dir", "user", "container" (depending on the command)
        Remark2: References to external software (or modules) should generally follow the external naming conventions.
        (E.g. for kubectl commands the verbs might include: apply, delete, get, describe, etc)
    }
}
Service ..> Domain
Service ..> Service
Service ..> Repository
Service ..> Provs

using ..> used
@enduml
```
