# nav2main
a repository for android go back to main activity while only one activity last.


## Getting Started | 快速上手

> 在 `buildscript` 的 classpath 中引入 Booster 插件，然后启用该插件：

```groovy
buildscript {
    ext.booster_version = '0.22.0'
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url 'https://oss.sonatype.org/content/repositories/public' }
    }
    dependencies {
        classpath "com.didiglobal.booster:booster-gradle-plugin:$booster_version"
        classpath "com.didiglobal.booster:booster-task-all:$booster_version"
        classpath "com.didiglobal.booster:booster-transform-all:$booster_version"
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        jcenter()
        maven { url 'https://oss.sonatype.org/content/repositories/public' }
    }
}

apply plugin: 'com.android.application'
apply plugin: 'com.didiglobal.booster'
```