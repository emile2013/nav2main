package com.yumibb.android.nav2main.plugin.transform

import com.android.build.gradle.BaseExtension
import javassist.ClassPool
import javassist.CtClass
import javassist.CtNewMethod
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * inject class for activity
 *
 * @author y.huang
 * @since 2019-10-15
 */
class HookActivityProcessor {

    companion object {

        /**
         * finish about
         */
        const val METHOD_CONTENT_DETECT_APP_TASK = "com.yumibb.android.lib.nav2main.ActivityStore.detectAppTask(this);"
        const val METHOD_BLOCK_FINISH = "public void finish(){super.finish();$METHOD_CONTENT_DETECT_APP_TASK}"
        const val METHOD_NAME_FINISH = "finish"

        /**
         * start activity about
         */
        const val METHOD_CONTENT_START_TIME = "com.yumibb.android.lib.nav2main.ActivityStore.recordStartNext(this);"
        const val METHOD_BLOCK_START_ACTIVITY_FOR_RESULT = "public void startActivityForResult(android.content.Intent intent, int requestCode,android.os.Bundle options) {super.startActivityForResult(intent, requestCode, options);$METHOD_CONTENT_START_TIME}"
        const val METHOD_NAME_START_ACTIVITY_FOR_RESULT = "startActivityForResult"


        const val METHOD_BLOCK_START_ACTIVITY_IF_NEED = "public boolean startActivityIfNeeded(android.content.Intent intent, int requestCode,android.os.Bundle options) {$METHOD_CONTENT_START_TIME return super.startActivityIfNeeded(intent, requestCode, options);}"
        const val METHOD_NAME_START_ACTIVITY_IF_NEED = "startActivityIfNeeded"

        const val METHOD_BLOCK_START_ACTIVITIES = "public void startActivities(android.content.Intent[] intents, android.os.Bundle options){super.startActivities(intents, options);$METHOD_CONTENT_START_TIME}"
        const val METHOD_NAME_START_ACTIVITIES = "startActivities"

        fun processJar(project: Project, classPool: ClassPool, jarFile: File, activities: Set<String>?) {

            var optJar: File = File(jarFile.getParent(), "${jarFile.name}.opt")

            var file = JarFile(jarFile)
            var enumeration = file.entries()

            var jarOutputStream = JarOutputStream(FileOutputStream(optJar))

            while (enumeration.hasMoreElements()) {

                var jarEntry = enumeration.nextElement()
                var entryName = jarEntry.name

                var baseExtension = project.extensions.findByType(BaseExtension::class.java)
                var excludes = baseExtension?.packagingOptions?.excludes
                if (excludes != null && excludes.contains(entryName)) {
                    continue
                }

                var zipEntry = ZipEntry(entryName)
                var inputStream = file.getInputStream(jarEntry)
                jarOutputStream.putNextEntry(zipEntry)

                if (shouldProcessClass(entryName, activities)) {

                    project.logger.info("now handle activity :${entryName}")

                    var ctClass = classPool.makeClass(inputStream, false)
                    if (ctClass.isFrozen()) ctClass.defrost()

                    addContentOrMethod(project, ctClass, METHOD_NAME_FINISH, METHOD_CONTENT_DETECT_APP_TASK, METHOD_BLOCK_FINISH)
                    addContentOrMethod(project, ctClass, METHOD_NAME_START_ACTIVITY_FOR_RESULT, METHOD_CONTENT_START_TIME, METHOD_BLOCK_START_ACTIVITY_FOR_RESULT)
                    addContentOrMethod(project, ctClass, METHOD_NAME_START_ACTIVITY_IF_NEED, METHOD_CONTENT_START_TIME, METHOD_BLOCK_START_ACTIVITY_IF_NEED)
                    addContentOrMethod(project, ctClass, METHOD_NAME_START_ACTIVITIES, METHOD_CONTENT_START_TIME, METHOD_BLOCK_START_ACTIVITIES, true)

                    var bytes = ctClass.toBytecode()
                    jarOutputStream.write(bytes)

                } else {
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
                inputStream.close()
            }

            jarOutputStream.close()
            file.close()

            if (jarFile.exists()) {
                jarFile.delete()
            }
            optJar.renameTo(jarFile)


        }


        fun processClass(project: Project, classPool: ClassPool, rootDir: File, inputFile: File, activities: Set<String>?) {


            if (shouldProcessClass(rootDir.toURI().relativize(inputFile.toURI()).getPath(), activities)) {

                project.logger.info("now handle activity :${rootDir.toURI().relativize(inputFile.toURI()).getPath()}")

                var inputStream = FileInputStream(inputFile)
                var ctClass = classPool.makeClass(inputStream, false)
                if (ctClass.isFrozen()) ctClass.defrost()

                addContentOrMethod(project, ctClass, METHOD_NAME_FINISH, METHOD_CONTENT_DETECT_APP_TASK, METHOD_BLOCK_FINISH)
                addContentOrMethod(project, ctClass, METHOD_NAME_START_ACTIVITY_FOR_RESULT, METHOD_CONTENT_START_TIME, METHOD_BLOCK_START_ACTIVITY_FOR_RESULT)
                addContentOrMethod(project, ctClass, METHOD_NAME_START_ACTIVITY_IF_NEED, METHOD_CONTENT_START_TIME, METHOD_BLOCK_START_ACTIVITY_IF_NEED)
                addContentOrMethod(project, ctClass, METHOD_NAME_START_ACTIVITIES, METHOD_CONTENT_START_TIME, METHOD_BLOCK_START_ACTIVITIES, true)

                var optFile = File(inputFile.parent, "${inputFile.name}.opt")
                var outputStream = FileOutputStream(optFile)
                var bytes = ctClass.toBytecode()
                outputStream.write(bytes)
                outputStream.close()
                if (inputFile.exists()) {
                    inputFile.delete()
                }
                optFile.renameTo(inputFile)
                inputStream.close()
            }
        }

        fun processFolderClass(project: Project, classPool: ClassPool, rootDir: File, excludeActivities: Set<String>?) {

            rootDir.walk().forEach {
                if (!it.isDirectory()) {
                    processClass(project, classPool, rootDir, it, excludeActivities)
                }
            }
        }


        private fun shouldProcessClass(entryName: String, activities: Set<String>?): Boolean {

            if (!entryName.endsWith(".class")
                    || entryName.contains("\$")
                    || entryName.endsWith("/R.class")
                    || entryName.endsWith("/BuildConfig.class")
                    || entryName.contains("android/support/")
                    || entryName.contains("android/arch/")
                    || entryName.contains("android/app/")) {
                return false
            }


            activities?.forEach {
                if (entryName.endsWith("${it.replace(".", "/").trim()}.class")) {
                    return true
                }
            }
            return false
        }


        private fun addContentOrMethod(project: Project, targetClass: CtClass, methodName: String, methodContent: String, methodBlock: String, insertBefore: Boolean = false) {
            try {
                var ctMethod = targetClass.getDeclaredMethod(methodName)
                if (insertBefore) {
                    ctMethod.insertBefore(methodContent)
                } else {
                    ctMethod.insertAfter(methodContent)
                }
            } catch (e: Exception) {
                try {
                    var newMethod = CtNewMethod.make(methodBlock, targetClass)
                    targetClass.addMethod(newMethod)
                } catch (ex: Exception) {
                    project.logger.info("${ex.message} : addContentOrMethod failed")
                }
            }

        }
    }
}