package com.yumibb.android.nav2main.plugin.transform

import VariantScopeCompat
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.collect.ImmutableSet
import com.yumibb.android.nav2main.plugin.extension.Nav2MainExtension
import com.yumibb.android.nav2main.plugin.gradle.runtimeClasspath
import com.yumibb.android.nav2main.plugin.util.ManifestReader
import javassist.ClassPool
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import org.gradle.util.GFileUtils
import java.io.File

/**
 * a transformer to rewrite activity for manager activity go back action
 *
 * @author y.huang
 * @since 2019-10-21
 */
class ActivityTransform : Transform {

    private val project: Project

    constructor(project: Project) {
        this.project = project
    }

    override fun getName(): String {
        return "nav2mainHook"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        if (project.plugins.hasPlugin("com.android.application")) {
            return TransformManager.SCOPE_FULL_PROJECT
        } else {
            return ImmutableSet.of(QualifiedContent.Scope.PROJECT)
        }
    }

    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)

        with(transformInvocation) {

            //不是增量，删除所有
            if (!isIncremental) {
                outputProvider.deleteAll()
            }

            //获取配置项
            project.extensions.findByType(Nav2MainExtension::class.java).let { extension ->


                //初始化classpool
                val classPool = initClassPool(transformInvocation)

                val excludeActivities = extension?.excludeActivities
                var packagePres = extension?.packagePres
                project.logger.info("the packagePres is:${packagePres}")


                var mergedManifest = VariantScopeCompat.getMergedManifestFile(transformInvocation.getContext().variantName, project)
                var activities = ManifestReader.activities(mergedManifest, excludeActivities, packagePres)

                project.logger.info("the activities is:${activities}")

                inputs.parallelStream().forEach {
                    /**
                     * jar遍历
                     */
                    it.jarInputs.parallelStream().forEach {
                        var dest: File = outputProvider.getContentLocation(it.getName(), it.contentTypes, it.scopes, Format.JAR)
                        if (transformInvocation.isIncremental()) {
                            when (it.getStatus()) {


                                Status.CHANGED, Status.ADDED -> {
                                    project.logger.info("${it.getStatus()} file is: ${it.file}")
                                    HookActivityProcessor.processJar(project, classPool, it.file, activities)
                                    GFileUtils.copyFile(it.file, dest)
                                }


                                Status.REMOVED -> {
                                    project.logger.info("REMOVED file is:" + it.file)
                                    GFileUtils.deleteQuietly(dest)
                                }

                            }
                        } else {
                            project.logger.info("no incremental :" + it.file)
                            HookActivityProcessor.processJar(project, classPool, it.file, activities)
                            GFileUtils.copyFile(it.file, dest)
                        }
                    }
                    /**
                     * 文件夹遍历
                     */
                    it.directoryInputs.parallelStream().forEach {
                        var inputDir: File = it.getFile()
                        var outputDir: File = outputProvider.getContentLocation(it.getName(), it.contentTypes, it.scopes, Format.DIRECTORY)
                        if (transformInvocation.isIncremental()) {

                            for ((inputFile, status) in it.getChangedFiles()) {

                                var outputFile: File = File(outputDir, com.android.utils.FileUtils.relativePossiblyNonExistingPath(inputFile, inputDir))

                                when (status) {
                                    Status.REMOVED -> {
                                        project.logger.info("REMOVED file is:" + inputFile.absolutePath)
                                        GFileUtils.deleteQuietly(outputFile)
                                    }


                                    Status.ADDED, Status.CHANGED -> {
                                        project.logger.info("${status} file is: ${inputFile.absolutePath}")
                                        HookActivityProcessor.processClass(project, classPool, inputDir, inputFile, activities)

                                        if (inputFile.isFile() && !inputFile.isDirectory()) {
                                            GFileUtils.deleteQuietly(outputFile)
                                            FileUtils.copyFile(inputFile, outputFile)
                                        }
                                    }
                                }
                            }
                        } else {

                            project.logger.info("no incremental :" + inputDir.absolutePath)
                            HookActivityProcessor.processFolderClass(project, classPool, inputDir, activities)
                            GFileUtils.deleteQuietly(outputDir)
                            FileUtils.copyDirectory(inputDir, outputDir)
                        }
                    }
                }

            }

        }


    }


    private fun initClassPool(transformInvocation: TransformInvocation): ClassPool {

        val pool = ClassPool(true)

        transformInvocation.runtimeClasspath?.forEach {
            try {
                pool.appendClassPath(it.absolutePath)
                println("appendClassPath:${it.absolutePath}")
            } catch (e: Exception) {
                println("initClassPool:${e}")
            }
        }

        return pool
    }
}