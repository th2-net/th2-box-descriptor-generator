# TH2 Gradle plugin

### Plugin configuration

`outputDirectory` - directory for writing the final file. Default `.`

`fileName` - name of final file. Default `serviceProtoDescription.json`

`configurationTypes` - gradle dependency type set. Default `["implementation"]`

`namePatterns` - set of substrings that will be checked when searching for jar files. If empty then match all files. Default `[]`

Config example (with default values): 
``` 
apply plugin: 'th2plugin'

th2parameters {
    targetDirectory = "."
    fileName = "serviceProtoDescription.json"
    configurationTypes = ["implementation"]
    namePatterns = []
}

```

Plugin must be applied outside of the build process before all other plugins, for example.
```
tasks.matching { it.name != 'generateServiceDescriptions' }.all { Task task ->
    task.dependsOn generateServiceDescriptions
}
```

Or before 'build' command

```
gradle generateServiceDescriptions clean build
```