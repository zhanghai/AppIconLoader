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
import android.content.pm.PackageInfo;
import android.os.AsyncTask;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.lifecycle.LiveData;

public class AppListLiveData extends LiveData<List<Pair<PackageInfo, String>>> {
    private final Context mContext;

    public AppListLiveData(@NonNull Context context) {
        mContext = context.getApplicationContext();

        loadValue();
    }

    private void loadValue() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> {
            List<Pair<PackageInfo, String>> value = AppListLoader.loadAppList(mContext);
            postValue(value);
        });
    }
}
