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

import android.content.Context;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;

import androidx.annotation.NonNull;

class UserSerialNumberCache {
    private static final long CACHE_MILLIS = 1000;

    @NonNull
    private static final ArrayMap<UserHandle, long[]> sCache = new ArrayMap<>();

    UserSerialNumberCache() {}

    public static long getSerialNumber(@NonNull UserHandle user, @NonNull Context context) {
        synchronized (sCache) {
            long[] serialNumberAndTime = sCache.get(user);
            if (serialNumberAndTime == null) {
                serialNumberAndTime = new long[2];
                sCache.put(user, serialNumberAndTime);
            }
            long time = System.currentTimeMillis();
            if (serialNumberAndTime[1] + CACHE_MILLIS <= time) {
                UserManager userManager = (UserManager) context.getSystemService(
                        Context.USER_SERVICE);
                serialNumberAndTime[0] = userManager.getSerialNumberForUser(user);
                serialNumberAndTime[1] = time;
            }
            return serialNumberAndTime[0];
        }
    }
}
