# TH2 Gradle plugin

### Plugin configuration

.proto files should contain 'package' field. As example 'package th2;'

`outputDirectory` - directory for writing the final file. Default `.`

`fileName` - name of final file. Default `serviceProtoDescription.json`

`configurationTypes` - gradle dependency type set. Default `["implementation"]`

`namePatterns` - set of substrings that will be checked when searching for jar files. If empty then match all files. Default `[]`

build.gradle example (with default values): 
``` 
When installed from github artifacts (obsolete):
buildscript {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/th2-net/th2-gradle-plugin")
        }
        dependencies {
            classpath "com.exactpro.th2:box-descriptor-generator:0.1.6"
        }
    }
}

When installed from Sonatype (actual):
buildscript {
    repositories {
        maven {
            name 'Sonatype_snapshots'
            url 'https://s01.oss.sonatype.org/content/repositories/snapshots/'
        }
    }
    dependencies {
        classpath "com.exactpro.th2:box-descriptor-generator:0.1.8-sonatype_publish-985900696-SNAPSHOT"
    }
}

apply plugin: 'th2plugin'

parameters {
    outputDirectory = "."
    fileName = "serviceProtoDescription"
    configurationTypes = ["implementation"]
    namePatterns = ["grpc"]
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