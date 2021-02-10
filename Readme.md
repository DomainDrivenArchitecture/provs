# Provs-core

the core engine of the provs framework.

## Provs framework

Framework for automating shell- and other system-tasks for provisioning reasons or other purposes.

Can easily be run

* locally
* remotely
* in a docker container
* or with any self-defined custom processor

Combines the 
* convenience and robustness of a modern programming language (Kotlin) with 
* power of being able to use shell commands and with 
* clear and detailed result summary of the built-in failure handling. 

## Provs-core

Provs-core provides the core component with
* execution engine
* failure handling
* multiple execution processors
* execution summary and logging
* support for secrets

## Usage

### Run hello world
Locally:

`kotlinc -cp build/libs/provs-latest.jar -script scripts/HelloWorldLocal.kts`

Remotely:

`kotlinc -cp build/libs/provs-latest.jar -script scripts/HelloWorldRemote.kts`

### Other examples
For a bunch of usage examples please have a look at provs-ubuntu-extensions.

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 
[Apache license 2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software distributed under the License 
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and limitations under the License.
