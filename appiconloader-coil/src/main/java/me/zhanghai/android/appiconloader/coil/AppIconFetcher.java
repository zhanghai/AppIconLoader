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

package me.zhanghai.android.appiconloader.coil;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import coil.ImageLoader;
import coil.decode.DataSource;
import coil.fetch.DrawableResult;
import coil.fetch.FetchResult;
import coil.fetch.Fetcher;
import coil.request.Options;
import kotlin.coroutines.Continuation;
import me.zhanghai.android.appiconloader.AppIconLoader;

public class AppIconFetcher implements Fetcher {
    @NonNull
    private final Options mOptions;
    @NonNull
    private final AppIconLoader mLoader;
    @NonNull
    private final ApplicationInfo mApplicationInfo;

    public AppIconFetcher(@NonNull Options options, @NonNull AppIconLoader loader,
                          @NonNull ApplicationInfo applicationInfo) {
        mOptions = options;
        mLoader = loader;
        mApplicationInfo = applicationInfo;
    }

    @Nullable
    @Override
    public FetchResult fetch(@NonNull Continuation<? super FetchResult> continuation) {
        Bitmap icon = mLoader.loadIcon(mApplicationInfo);
        return new DrawableResult(new BitmapDrawable(mOptions.getContext().getResources(), icon),
                true, DataSource.DISK);
    }

    public static class Factory implements Fetcher.Factory<PackageInfo> {
        @NonNull
        private final AppIconLoader mLoader;

        public Factory(@Px int iconSize, boolean shrinkNonAdaptiveIcons, @NonNull Context context) {
            context = context.getApplicationContext();
            mLoader = new AppIconLoader(iconSize, shrinkNonAdaptiveIcons, context);
        }

        @Nullable
        @Override
        public Fetcher create(@NonNull PackageInfo packageInfo, @NonNull Options options,
                              @NonNull ImageLoader imageLoader) {
            return new AppIconFetcher(options, mLoader, packageInfo.applicationInfo);
        }
    }
}
