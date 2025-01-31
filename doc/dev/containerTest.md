Should be added in dev_circumstances for single unit tests to get repeatable results ('fresh container'): 
ContainerStartMode.CREATE_NEW_KILL_EXISTING

like this way:
+import org.domaindrivenarchitecture.provs.test.tags.ContainerTest

//annotate to ContainerTest
+@ContainerTest

//and configured Testcontainer
+val container = defaultTestContainer(ContainerStartMode.CREATE_NEW_KILL_EXISTING)