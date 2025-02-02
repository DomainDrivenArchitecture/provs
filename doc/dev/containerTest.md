# Container Tests

The method `defaultTestContainer()` provides a prov instance in a container for integration-tests.  

Container-tests should be annotated by tag: `@ContainerTest` and if long-lasting (ca. > 10 - 20 sec) with @ExtensiveContainerTest 


For performance reasons the test container is re-used among the tests.

In case you want a fresh container for your test, add the following option: 

`ContainerStartMode.CREATE_NEW_KILL_EXISTING` 

example: `val container = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)`

