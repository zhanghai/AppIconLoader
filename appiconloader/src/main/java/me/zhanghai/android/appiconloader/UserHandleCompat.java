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

import android.os.Build;
import android.os.UserHandle;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class UserHandleCompat {
    private static final int PER_USER_RANGE = 100000;
    private static final int USER_SYSTEM = 0;
    private static final boolean MU_ENABLED = true;

    @Nullable
    private static Constructor<UserHandle> sConstructor;
    @NonNull
    private static final Object sConstructorLock = new Object();

    private UserHandleCompat() {}

    @NonNull
    public static UserHandle getUserHandleForUid(int uid) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return UserHandle.getUserHandleForUid(uid);
        } else {
            int userId = getUserId(uid);
            Constructor<UserHandle> constructor = getConstructor();
            try {
                return constructor.newInstance(userId);
            } catch (IllegalAccessException | InstantiationException
                    | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static int getUserId(int uid) {
        if (MU_ENABLED) {
            return uid / PER_USER_RANGE;
        } else {
            return USER_SYSTEM;
        }
    }

    @NonNull
    private static Constructor<UserHandle> getConstructor() {
        synchronized (sConstructorLock) {
            if (sConstructor == null) {
                try {
                    sConstructor = UserHandle.class.getDeclaredConstructor(int.class);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
            return sConstructor;
        }
    }
}
