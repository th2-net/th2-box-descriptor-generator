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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleScriptException;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.TaskAction;
import org.slf4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ServiceDescriptions extends DefaultTask {
    private final Logger LOGGER = getProject().getLogger();
    private static final String PARAMETERS = "parameters";
    private static final String PROTO_FILE = ".proto";
    private Parameters parameters;
    private final boolean isNamesEmpty;
    private final Set<String> configurationTypes;
    private final Set<String> namesPatterns;
    private static final ObjectMapper jacksonMapper = new ObjectMapper();

    public ServiceDescriptions() {
        parameters = (Parameters) getProject().getExtensions().findByName(PARAMETERS);
        if (parameters == null) {
            parameters = new Parameters();
            LOGGER.debug(String.format("Using default parameters: %s", parameters));
        }
        isNamesEmpty = parameters.getNamePatterns().isEmpty();
        configurationTypes = parameters.getConfigurationTypes();
        namesPatterns = parameters.getNamePatterns();
    }

    private boolean matchName(String pattern) {
        return isNamesEmpty ||
                namesPatterns.stream()
                        .anyMatch(name -> name.toLowerCase().contains(pattern.toLowerCase()));
    }

    private Set<File> getDependenciesFiles(final Project project) {
        var configurations = project.getConfigurations().stream()
                .filter(conf -> configurationTypes.contains(conf.getName()))
                .collect(Collectors.toList());

        Set<File> dependencySet = new HashSet<>();
        for (Configuration configuration : configurations) {
            try {
                configuration.setVisible(true);
                configuration.setCanBeResolved(true);
                configuration.setCanBeConsumed(true);
                configuration.forEach(file -> {
                    if (matchName(file.getName())) {
                        dependencySet.add(file);
                    }
                });
            } catch (Exception ex) {
                project.getLogger().error("Cannot process configuration  " + configuration.getName(), ex);
            }
        }
        return dependencySet;
    }

    private String readFile(JarFile jarFile, JarEntry entry) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader rdr = new BufferedReader(
                new InputStreamReader(jarFile.getInputStream(entry),
                        StandardCharsets.UTF_8))) {
            for (int c = 0; (c = rdr.read()) != -1; ) {
                sb.append((char) c);
            }
        } catch (IOException e) {
            LOGGER.error("Cannot process file: " + entry.getRealName(), e);
        }
        return sb.toString();
    }

    private Map<String, String> getAllProtoInJar(File file) {
        Map<String, String> protoNameToContent = null;
        try {
            var jarFile = new JarFile(file);
            protoNameToContent = new HashMap<>();
            for (Enumeration<JarEntry> enums = jarFile.entries(); enums.hasMoreElements(); ) {
                JarEntry entry = enums.nextElement();
                if (!entry.isDirectory() && entry.getRealName().endsWith(PROTO_FILE)) {
                    String protoFile = readFile(jarFile, entry);
                    protoNameToContent.put(entry.getRealName(), protoFile);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Cannot process jar file: " + file.getName(), e);
        }
        return protoNameToContent;
    }

    private String convertToJson(Map<String, Map<String, String>> jarToAllProto) {
        try {
            return jacksonMapper.writeValueAsString(jarToAllProto);
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot convert to json " + jarToAllProto, e);
            throw new GradleScriptException("Cannot convert to json " + jarToAllProto, e);
        }
    }

    private void writeToFile(String jsonString) {
        File outputDir = new File(parameters.getOutputDirectory());
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            LOGGER.error("Cannot create directory " + outputDir.getAbsolutePath());
            throw new GradleScriptException("Cannot create directory " + outputDir.getAbsolutePath(), null);
        }
        File finalPath = new File(outputDir + File.separator + parameters.getFileName());
        try (OutputStreamWriter writer =
                     new OutputStreamWriter(
                             new FileOutputStream(finalPath),
                             StandardCharsets.UTF_8)) {
            writer.write(jsonString);
            writer.flush();
        } catch (IOException e) {
            LOGGER.error(String.format("Cannot write to json file: '%s' path: '%s'",
                    parameters.getFileName(),
                    parameters.getOutputDirectory()), e);
            throw new GradleScriptException(String.format("Cannot write to json file: '%s' path: '%s'",
                    parameters.getFileName(),
                    parameters.getOutputDirectory()), e);
        }
    }

    @TaskAction
    public void action() {
        Project project = getProject();
        Set<File> dependencyFiles = getDependenciesFiles(project);
        Map<String, Map<String, String>> jarToAllProto = new HashMap<>();
        dependencyFiles.forEach(file -> {
                    Map<String, String> protoInJar = getAllProtoInJar(file);
                    if (protoInJar != null && !protoInJar.isEmpty()) {
                        jarToAllProto.put(file.getName(), protoInJar);
                    }
                }
        );
        String jsonString = convertToJson(jarToAllProto);
        writeToFile(jsonString);
    }
}
