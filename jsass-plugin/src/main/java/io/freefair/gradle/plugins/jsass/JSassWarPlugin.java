package io.freefair.gradle.plugins.jsass;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.plugins.WarPlugin;
import org.gradle.api.plugins.WarPluginConvention;
import org.gradle.api.tasks.bundling.War;

import java.io.File;

public class JSassWarPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {

        project.getPlugins().apply(JSassWebjarsPlugin.class);
        project.getPlugins().apply(WarPlugin.class);

        SassCompile compileWebappSass = project.getTasks().create("compileWebappSass", SassCompile.class);
        compileWebappSass.setGroup(BasePlugin.BUILD_GROUP);
        compileWebappSass.setDescription("Compile sass and scss files for the webapp");

        WarPluginConvention warPluginConvention = project.getConvention().getPlugin(WarPluginConvention.class);
        compileWebappSass.source(warPluginConvention.getWebAppDir());

        War war = (War) project.getTasks().getByName(WarPlugin.WAR_TASK_NAME);

        compileWebappSass.getDestinationDir().set(new File(project.getBuildDir(), "jsass/webapp"));
        war.from(compileWebappSass);
    }
}
