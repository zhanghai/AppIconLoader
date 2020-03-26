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

import android.content.Context;
import android.content.pm.LauncherApps;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

public class LauncherAppsCompat {
    private LauncherAppsCompat() {}

    @NonNull
    public static List<UserHandle> getProfiles(@NonNull LauncherApps launcherApps,
                                               @NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return launcherApps.getProfiles();
        } else {
            UserManager userManager = ContextCompat.getSystemService(context, UserManager.class);
            return userManager.getUserProfiles();
        }
    }
}
