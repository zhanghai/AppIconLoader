/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.zhanghai.android.appiconloader.sample;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArrayMap;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

public class AppListLoader {
    private AppListLoader() {}

    @NonNull
    public static List<Pair<PackageInfo, String>> loadAppList(@NonNull Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        Iterator<PackageInfo> iterator = packageInfos.iterator();
        while (iterator.hasNext()) {
            PackageInfo packageInfo = iterator.next();
            if (packageInfo.applicationInfo == null) {
                iterator.remove();
            }
        }

        LauncherApps launcherApps = ContextCompat.getSystemService(context, LauncherApps.class);
        List<UserHandle> profiles = LauncherAppsCompat.getProfiles(launcherApps, context);
        profiles.remove(Process.myUserHandle());
        if (!profiles.isEmpty()) {
            ArrayMap<String, PackageInfo> packageInfoMap = new ArrayMap<>();
            for (PackageInfo packageInfo : packageInfos) {
                packageInfoMap.put(packageInfo.packageName, packageInfo);
            }
            for (UserHandle profile : profiles) {
                List<LauncherActivityInfo> activityList = launcherApps.getActivityList(null,
                        profile);
                ArrayMap<String, ApplicationInfo> applicationInfoMap = new ArrayMap<>();
                for (LauncherActivityInfo launcherActivityInfo : activityList) {
                    ApplicationInfo applicationInfo = launcherActivityInfo.getApplicationInfo();
                    if (!applicationInfoMap.containsKey(applicationInfo.packageName)) {
                        applicationInfoMap.put(applicationInfo.packageName, applicationInfo);
                    }
                }
                for (ApplicationInfo applicationInfo : applicationInfoMap.values()) {
                    PackageInfo packageInfo;
                    String packageName = applicationInfo.packageName;
                    if (packageInfoMap.containsKey(packageName)) {
                        packageInfo = ParcelableCloner.cloneParcelable(packageInfoMap.get(
                                packageName), PackageInfo.class.getClassLoader());
                    } else {
                        try {
                            packageInfo = packageManager.getPackageInfo(packageName,
                                    PackageManagerCompat.MATCH_UNINSTALLED_PACKAGES);
                        } catch (PackageManager.NameNotFoundException e) {
                            continue;
                        }
                    }
                    packageInfo.applicationInfo = applicationInfo;
                    packageInfos.add(packageInfo);
                }
            }
        }

        List<Pair<PackageInfo, String>> apps = new ArrayList<>();
        for (PackageInfo packageInfo : packageInfos) {
            String label = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            apps.add(new Pair<>(packageInfo, label));
        }
        Collator collator = Collator.getInstance();
        Collections.sort(apps, (first, second) -> {
            int result = collator.compare(first.second, second.second);
            if (result == 0) {
                result = first.first.packageName.compareTo(second.first.packageName);
            }
            if (result == 0) {
                result = Integer.compare(first.first.applicationInfo.uid,
                        second.first.applicationInfo.uid);
            }
            return result;
        });
        return apps;
    }

    public static void startAppDetailsActivity(@NonNull PackageInfo packageInfo,
                                               @NonNull Context context) {
        LauncherApps launcherApps = ContextCompat.getSystemService(context, LauncherApps.class);
        ComponentName componentName = new ComponentName(packageInfo.packageName, "");
        UserHandle user = UserHandleCompat.getUserHandleForUid(packageInfo.applicationInfo.uid);
        launcherApps.startAppDetailsActivity(componentName, user, null, null);
    }
}
