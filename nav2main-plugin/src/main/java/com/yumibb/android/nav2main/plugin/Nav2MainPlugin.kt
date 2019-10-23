package com.yumibb.android.nav2main.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.internal.pipeline.TransformTask
import com.yumibb.android.nav2main.plugin.extension.Nav2MainExtension
import com.yumibb.android.nav2main.plugin.transform.ActivityTransform
import org.apache.commons.codec.binary.StringUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskCollection

/**
 * Nav2Main Gradle Plugin
 *
 * @author y.huang
 * @since 2019-10-21
 */
class Nav2MainPlugin : Plugin<Project> {


    companion object {
        const val TASK_GROUP = "nav2main"


        fun findTransformActivityTransformTask(
                project: Project,
                variantName: String
        ): Task? {
            var transformTasks: TaskCollection<TransformTask> = project.getTasks().withType(TransformTask::class.java)

            transformTasks.forEach {

                if (StringUtils.equals(it.variantName.toLowerCase(), variantName.toLowerCase())) {

                    if (it.getTransform() is ActivityTransform) {
                        return it
                    }

                }
            }
            return null
        }


    }

    override fun apply(project: Project) {

        with(project) {

            if (!plugins.hasPlugin("com.android.application")) {
                return
            }

            extensions.create("nav2main", Nav2MainExtension::class.java)

            var android = extensions.getByType(AppExtension::class.java)
            android?.registerTransform(ActivityTransform(project))

            afterEvaluate {

                android.applicationVariants.all { variant ->

                    var variantName = variant.name.capitalize()
                    var activityTransform = findTransformActivityTransformTask(project, variantName)
                    activityTransform?.group = TASK_GROUP
                    activityTransform?.description = "rewrite activity finish method by nav2main ActivityTransform"
                }
            }

        }
    }

}
