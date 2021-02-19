# TH2 Gradle plugin

### Plugin configuration

.proto files should contain 'package' field. As example 'package th2;'

`outputDirectory` - directory for writing the final file. Default `.`

`fileName` - name of final file. Default `serviceProtoDescription.json`

`configurationTypes` - gradle dependency type set. Default `["implementation"]`

`namePatterns` - set of substrings that will be checked when searching for jar files. If empty then match all files. Default `[]`

build.gradle example (with default values): 
``` 
buildscript {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/th2-net/th2-gradle-plugin")
        }
        dependencies {
            classpath "com.exactpro.th2:th2-gradle-plugin:0.1.6"
        }
    }
}

apply plugin: 'th2plugin'

parameters {
    targetDirectory = "."
    fileName = "serviceProtoDescription"
    configurationTypes = ["implementation"]
    namePatterns = []
}

```

the plugin should be applied as a separate task outside of the build tasks or before all other plugins, for example:
```
tasks.matching { it.name != 'generateServiceDescriptions' }.all { Task task ->
    task.dependsOn generateServiceDescriptions
}
```

Or before 'build' command

```
gradle generateServiceDescriptions clean build
```