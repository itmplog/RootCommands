# RootCommands

_Currently RootCommands is not ready to be used in production_

This is a library to simplify the execution of root commands.
It is a Java wrapper around native executables used to execute root commands.

# Examples

```java
ShellExecutor exec = new ShellExecutor(true, 100, null, null, 25000);
exec.openShell();

Toolbox toolbox = new Toolbox(exec);

boolean rootAccess =  toolbox.isAccessGiven();

if (rootAccess) {
  boolean success = toolbox.killProcess("zygote");
}

exec.closeShell();
```

# Contribute

Fork RootCommands and do a Pull Request. I will merge your changes back into the main project.

# Add the lib to your project

* New -> Android Project -> Create project from existing source, choose org_donations 
* Add org_donations as Android Lib (Properties of your project -> Android -> Library -> add org_donations as android library)