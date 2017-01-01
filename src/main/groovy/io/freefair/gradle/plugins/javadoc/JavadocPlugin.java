package io.freefair.gradle.plugins.javadoc;

import io.freefair.gradle.plugins.JavadocJarPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * @author Lars Grefer
 * @see JavadocLinksPlugin
 * @see JavadocIoPlugin
 * @see JavadocJarPlugin
 */
@SuppressWarnings("unused")
public class JavadocPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPluginManager().apply(JavadocLinksPlugin.class);
        project.getPluginManager().apply(JavadocIoPlugin.class);
        project.getPluginManager().apply(JavadocJarPlugin.class);
    }
}
