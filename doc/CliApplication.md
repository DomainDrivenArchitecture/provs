```plantuml
@startuml

autonumber

skinparam sequenceBox {
     borderColor White
}

participant User

box "application" #LightBlue

participant CliWorkplace
participant CliWorkplaceParser
participant CliWorkplaceCommand
participant Application

end box

box  #White

participant CliUtils
participant "Prov (local or remote...)" as ProvInstance

end box

box "domain" #LightGreen

participant ProvisionWorkplace

end box

box "infrastructure" #CornSilk

participant ConfigRepository
participant "Infrastructure functions" as Infrastructure_functions

end box


User -> CliWorkplace ++ : main(args...)

CliWorkplace -> CliWorkplaceParser : parseWorkplaceArguments

CliWorkplace -> CliWorkplaceCommand : isValid ?

CliWorkplace -> ConfigRepository : getConfig

CliWorkplace -> CliUtils : createProvInstance
ProvInstance <- CliUtils : create

CliWorkplace -> Application : provision ( config )
Application -> ProvInstance : provisionWorkplace ( type, ssh, ...)
ProvInstance -> ProvisionWorkplace : provisionWorkplace

ProvisionWorkplace -> Infrastructure_functions: Various calls like:
ProvisionWorkplace -> Infrastructure_functions: install ssh, gpg, git ...
ProvisionWorkplace -> Infrastructure_functions: installVirtualBoxGuestAdditions
ProvisionWorkplace -> Infrastructure_functions: configureNoSwappiness, ...

@enduml
```