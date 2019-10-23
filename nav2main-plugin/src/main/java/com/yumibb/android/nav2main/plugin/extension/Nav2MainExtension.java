package com.yumibb.android.nav2main.plugin.extension;

import java.util.Set;

/**
 *
 * gradle nav2main extension
 *
 * @author y.huang
 * @since 2019-10-21
 */
//FIXME 这里暂只能用java不能用kotlin，用kotlin存在 groovy dsl 注入无法赋值属性问题
public class Nav2MainExtension {

    private Set<String> excludeActivities;

    private Set<String> packagePres;

    public Set<String> getExcludeActivities() {
        return excludeActivities;
    }

    public void setExcludeActivities(Set<String> excludeActivities) {
        this.excludeActivities = excludeActivities;
    }

    public Set<String> getPackagePres() {
        return packagePres;
    }

    public void setPackagePres(Set<String> packagePres) {
        this.packagePres = packagePres;
    }
}
