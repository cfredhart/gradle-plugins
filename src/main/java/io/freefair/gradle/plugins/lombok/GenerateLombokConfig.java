package io.freefair.gradle.plugins.lombok;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Properties;

@Getter
@Setter
public class GenerateLombokConfig extends DefaultTask {

    @OutputFile
    private File outputFile = getProject().file("lombok.config");

    @Input
    private Map<String, String> properties;

    public GenerateLombokConfig() {
        onlyIf(t -> getProperties() != null && !getProperties().isEmpty());
    }

    @TaskAction
    @SneakyThrows
    public void generateLombokConfig() {
        Properties prop = new Properties();
        prop.putAll(properties);
        try (OutputStream out = new FileOutputStream(outputFile)) {
            prop.store(out, "This file is generated by the 'io.freefair.lombok' Gradle plugin");
        }
    }
}