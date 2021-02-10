/*******************************************************************************
 * Copyright 2020-2021 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.exactpro.th2;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Parameters {
    private String outputDirectory = ".";
    private String fileName = "serviceProtoDescription.json";
    private Set<String> namePatterns = Collections.emptySet();
    private Set<String> configurationTypes = new HashSet<>() {{
        add("implementation");
    }};

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Set<String> getConfigurationTypes() {
        return configurationTypes;
    }

    public void setConfigurationTypes(Set<String> configurationTypes) {
        this.configurationTypes = configurationTypes;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public Set<String> getNamePatterns() {
        return namePatterns;
    }

    public void setNamePatterns(Set<String> namePatterns) {
        this.namePatterns = namePatterns;
    }

    @Override
    public String toString() {
        return "Parameters{" +
                "outputDirectory='" + outputDirectory + '\'' +
                ", fileName='" + fileName + '\'' +
                ", namePatterns=" + namePatterns +
                ", configurationTypes=" + configurationTypes +
                '}';
    }
}
