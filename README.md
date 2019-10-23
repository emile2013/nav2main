# nav2main
[![license](http://img.shields.io/badge/license-BSD3-brightgreen.svg?style=flat)](https://github.com/emile2013/nav2main/tree/master/LICENSE)
[![Release Version](https://jitpack.io/v/emile2013/nav2main.svg)](https://jitpack.io/#emile2013/nav2main)

A repository for android go back to main activity while only one activity last.

>移动领域，用户留存率和活跃用户数是两个重要指标。为达业务目标，运营和产品同学会通过PUSH，短信等渠道唤醒用户。  
>在电商行业中，一个典型的应用场景是，发送店铺上新PUSH，让用户打开上新页。这种场景在APP冷启动下，用户在看完页面内容后，按返回键，就退出APP了，用户使用时长就是上新页的停留时长。这是团队不想看到的流程，通常我们会让用户退到首页继续浏览。  
>类似上述使用场景，最后一个Activity页退出时，让用户重置跳转至首页，让APP继续使用，这就是回退首页功能。需求明确了，这里列举几个可行方案：  
>1、继承方式：书写Activity基类，重写基类finish方法完成逻辑判断，所有业务Activity页继承基
类，实现回退首页功能。这种方式适用于小型APP；   
>2、Hook方式：监听ActivityLifecycle以及Hook AMS，完成回退首页功能。这种方式在android 10中会失效，10系统中不再有startactivity等回调； 
>3、字节码注入方式：利用Android Gradle Transform API,在Activity中注入字节码，完成回退首页功能。本库实现方式，性能消耗在编译阶段，但可以在开发阶段屏蔽，上线时开启。    

## Getting Started 

> Edit root project build.gradle file, append nav2main plugin in  `buildscript`  classpath ，and do not forget add maven { url 'https://jitpack.io' } too.

```groovy
buildscript {
    ext.nav2main_version='0.1.0'
    repositories {
        maven { url 'https://jitpack.io' } //add this line
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath "com.github.emile2013.nav2main:nav2main-plugin:$nav2main_version" //add this line
    }
}

allprojects {
    repositories {
      maven { url 'https://jitpack.io' }  //add this line
      google()
      mavenCentral()
      jcenter()
    }
}
```

>  App module build.gradle file  add nav2main dependency

```groovy
apply plugin: 'com.android.application'
apply plugin: 'com.yumibb.android.nav2main' // add this line

dependencies {
    implementation "com.github.emile2013.nav2main:nav2main-lib:${rootProject.ext.nav2main_version}" //add this line
}

//excludeActivities  for nave2main plugin that not inject some codes for list items.
//packagePres  means nav2main plugin just handle classes with package list  in items.
nav2main { //add this  content block
    excludeActivities = ["com.yumibb.android.nav2main.demo.SkipActivity",
                         "com.yumibb.android.nav2main.demo.SplashActivity",
                         "com.yumibb.android.nav2main.demo.MainActivity"]
    packagePres = ["com.yumibb"]
}

```

> Init in application oncreate method.

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Nav2Main.getInstance().main(MainActivity::class.java).init(this) { _: Context, _: Intent ->
            Log.i("Nav2Main"," now back to  main activity")
        }
    }
}
```

## Samples 
- [sample](https://github.com/emile2013/nav2main/tree/master/sample)


## Test
 After install sample , input These code in console : adb shell am start -n com.yumibb.android.nav2main.demo/.NormalActivity


## Thanks
- [booster](https://github.com/didi/booster)
- [Sunzxyong](https://github.com/Sunzxyong)
- [lizhangqu](https://github.com/lizhangqu)

## License

nav2main is licensed under the [BSD 3-Clause License](./LICENSE).