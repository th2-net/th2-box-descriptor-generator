# TH2 Gradle plugin

### Plugin configuration

`outputDirectory` - directory for writing the final file. Default `.`

`fileName` - name of final file. Default `serviceProtoDescription.json`

`configurationTypes` - gradle dependency type set. Default `["implementation"]`

`namePatterns` - set of substrings that will be checked when searching for jar files. If empty then match all files. Default `[]`

Config example (with default values): 
``` 
apply plugin: 'th2plugin'

parameters {
    targetDirectory = "."
    fileName = "serviceProtoDescription.json"
    configurationTypes = ["implementation"]
    namePatterns = []
}

```