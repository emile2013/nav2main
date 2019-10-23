package com.yumibb.android.nav2main.plugin.gradle

import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.internal.AbstractTask
import java.io.File


/**
 * See <a href="https://github.com/didi/booster/blob/5465f1d334efcf46067739381ec01a1a1afd9a1f/booster-android-gradle-api/src/main/kotlin/com/didiglobal/booster/gradle/TransformInvocation.kt">
 *     https://github.com/didi/booster/blob/5465f1d334efcf46067739381ec01a1a1afd9a1f/booster-android-gradle-api/src/main/kotlin/com/didiglobal/booster/gradle/TransformInvocation.kt</a>
 */



/**
 * Returns android extension
 *
 * @author johnsonlee
 */
inline fun <reified T : BaseExtension> Project.getAndroid(): T = extensions.getByName("android") as T


/**
 * Represents the booster transform for
 *
 * @author johnsonlee
 */
val TransformInvocation.project: Project
    get() = (this.context as AbstractTask).project


val TransformInvocation.bootClasspath: Collection<File>
    get() = project.getAndroid<BaseExtension>().bootClasspath

/**
 * Returns the compile classpath of this transform invocation
 *
 * @author johnsonlee
 */
val TransformInvocation.compileClasspath: Collection<File>
    get() = listOf(inputs, referencedInputs).flatten().map {
        it.jarInputs + it.directoryInputs
    }.flatten().map {
        it.file
    }

/**
 * Returns the runtime classpath of this transform invocation
 *
 * @author johnsonlee
 */
val TransformInvocation.runtimeClasspath: Collection<File>
    get() = compileClasspath + bootClasspath