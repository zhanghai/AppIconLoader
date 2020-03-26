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

package me.zhanghai.android.appiconloader;

import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class PackageItemInfoCompat {
    private PackageItemInfoCompat() {}

    public static Drawable loadUnbadgedIcon(@NonNull PackageItemInfo packageItemInfo,
                                            @NonNull PackageManager packageManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return packageItemInfo.loadUnbadgedIcon(packageManager);
        } else {
            ApplicationInfo applicationInfo = getApplicationInfo(packageItemInfo);
            Drawable drawable = null;
            if (packageItemInfo.packageName != null) {
                drawable = packageManager.getDrawable(packageItemInfo.packageName,
                        packageItemInfo.icon, applicationInfo);
            }
            if (drawable == null && packageItemInfo != applicationInfo && applicationInfo != null) {
                drawable = loadUnbadgedIcon(applicationInfo, packageManager);
            }
            if (drawable == null) {
                drawable = loadDefaultIcon(packageItemInfo, packageManager);
            }
            return drawable;
        }
    }

    @Nullable
    private static ApplicationInfo getApplicationInfo(@NonNull PackageItemInfo packageItemInfo) {
        if (packageItemInfo instanceof ApplicationInfo) {
            return (ApplicationInfo) packageItemInfo;
        } else if (packageItemInfo instanceof ComponentInfo) {
            return ((ComponentInfo) packageItemInfo).applicationInfo;
        } else {
            return null;
        }
    }

    @NonNull
    private static Drawable loadDefaultIcon(@NonNull PackageItemInfo packageItemInfo,
                                            @NonNull PackageManager packageManager) {
        return packageManager.getDefaultActivityIcon();
    }
}
