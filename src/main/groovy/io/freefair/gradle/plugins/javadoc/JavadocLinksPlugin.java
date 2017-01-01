package io.freefair.gradle.plugins.javadoc;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gradle.api.*;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

@SuppressWarnings("unused")
@Getter
@NoArgsConstructor
public class JavadocLinksPlugin implements Plugin<Project> {

    private JavadocLinksExtension extension;

    private Task configureJavadocLinks;
    private Project project;

    @Override
    public void apply(final Project project) {
        this.project = project;

        setupBase();

        setupDocsOracleLink();

        setupDependencyGuesses();

    }

    private void setupDependencyGuesses() {

        project.getConfigurations().getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME);

        Task guessJavadocLinks = project.getTasks().create("guessJavadocLinks");
        guessJavadocLinks.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP);

        getConfigureJavadocLinks().dependsOn(guessJavadocLinks);

        guessJavadocLinks.doFirst(new Action<Task>() {
            @Override
            public void execute(Task guessJavadocLinks) {
                for (ResolvedArtifact resolvedArtifact : project.getConfigurations().getByName(JavaPlugin.COMPILE_CLASSPATH_CONFIGURATION_NAME).getResolvedConfiguration().getResolvedArtifacts()) {

                    String name = resolvedArtifact.getName();
                    String version = resolvedArtifact.getModuleVersion().getId().getVersion();

                    String majorVersion;
                    if(version.contains(".")) {
                        majorVersion = version.substring(0, version.indexOf("."));
                    } else {
                        majorVersion = version;
                    }

                    switch (name) {
                        case "spring-boot":
                            extension.links("http://docs.spring.io/spring-boot/docs/" + version + "/api/");
                            break;
                        case "spring-core":
                        case "spring-context":
                        case "spring-beans":
                        case "spring-web":
                            extension.links("http://docs.spring.io/spring/docs/" + version + "/javadoc-api/");
                            break;
                        case "okio":
                        case "okhttp":
                        case "retrofit":
                            extension.links("http://square.github.io/" + name + "/" + majorVersion + ".x/" + name + "/");
                            break;
                    }
                }
            }
        });

    }

    private void setupBase() {

        extension = project.getExtensions().create("javadocLinks", JavadocLinksExtension.class);

        configureJavadocLinks = project.getTasks().create("configureJavadocLinks");
        configureJavadocLinks.setGroup(JavaBasePlugin.DOCUMENTATION_GROUP);

        project.getTasks().withType(Javadoc.class, new Action<Javadoc>() {
            @Override
            public void execute(Javadoc javadoc) {
                javadoc.dependsOn(configureJavadocLinks);
            }
        });

        configureJavadocLinks.doFirst(new Action<Task>() {
            @Override
            public void execute(Task configureJavadocLinks) {
                project.getTasks().withType(Javadoc.class, new Action<Javadoc>() {
                    @Override
                    public void execute(Javadoc javadoc) {
                        StandardJavadocDocletOptions options = (StandardJavadocDocletOptions) javadoc.getOptions();
                        for (String link : extension.getLinks()) {
                            project.getLogger().info("Adding link {} to javadoc task {}", link, javadoc);
                            options.links(link);
                        }
                    }
                });
            }
        });
    }

    private void setupDocsOracleLink() {
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {

                JavaVersion javaVersion = extension.getJavaVersion();
                if(javaVersion == null) {
                    project.getLogger().debug("Not adding Oracle Java Docs link because javaVersion is null");
                    return;
                }

                switch (javaVersion) {
                    case VERSION_1_5:
                        extension.links("http://docs.oracle.com/javase/5/docs/api/");
                        break;
                    case VERSION_1_6:
                        extension.links("http://docs.oracle.com/javase/6/docs/api/");
                        break;
                    case VERSION_1_7:
                        extension.links("http://docs.oracle.com/javase/7/docs/api/");
                        break;
                    default:
                        project.getLogger().warn("Unknown java version {}", javaVersion);
                    case VERSION_1_8:
                        extension.links("http://docs.oracle.com/javase/8/docs/api/");
                        break;
                }
            }
        });
    }
}
