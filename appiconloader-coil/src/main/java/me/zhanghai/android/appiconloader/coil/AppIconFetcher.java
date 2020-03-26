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
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import coil.bitmappool.BitmapPool;
import coil.decode.DataSource;
import coil.decode.Options;
import coil.fetch.DrawableResult;
import coil.fetch.FetchResult;
import coil.fetch.Fetcher;
import coil.size.Size;
import kotlin.coroutines.Continuation;
import me.zhanghai.android.appiconloader.AppIconLoader;

public class AppIconFetcher implements Fetcher<PackageInfo> {
    @NonNull
    private final AppIconLoader mLoader;
    @NonNull
    private final Context mContext;

    public AppIconFetcher(@Px int iconSize, @NonNull Context context) {
        context = context.getApplicationContext();
        mLoader = new AppIconLoader(iconSize, context);
        mContext = context;
    }

    @Nullable
    @Override
    public Object fetch(@NotNull BitmapPool bitmapPool, @NotNull PackageInfo packageInfo,
                        @NotNull Size size, @NotNull Options options,
                        @NotNull Continuation<? super FetchResult> continuation) {
        Drawable icon = mLoader.loadIcon(packageInfo.applicationInfo);
        return new DrawableResult(icon, true, DataSource.DISK);
    }

    @Override
    public boolean handles(@NotNull PackageInfo packageInfo) {
        return true;
    }

    @Nullable
    @Override
    public String key(@NotNull PackageInfo packageInfo) {
        return AppIconLoader.getIconKey(packageInfo, mContext);
    }
}
