# nav2main
A repository for android go back to main activity while only one activity last.


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


## test | 测试
 After install sample , input These code in console : adb shell am start -n com.yumibb.android.nav2main.demo/.NormalActivity

## License

nav2main is licensed under the [BSD 3-Clause License](./LICENSE).