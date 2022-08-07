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
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.Px;

import org.jetbrains.annotations.NotNull;

import coil.ImageLoader;
import coil.decode.DataSource;
import coil.fetch.DrawableResult;
import coil.fetch.FetchResult;
import coil.fetch.Fetcher;
import coil.request.Options;
import kotlin.coroutines.Continuation;
import me.zhanghai.android.appiconloader.AppIconLoader;

public class AppIconFetcher implements Fetcher {
    @NotNull
    private final AppIconLoader mLoader;
    @NotNull
    private final Context mContext;
    @NotNull
    private final PackageInfo mPackageInfo;

    public AppIconFetcher(@Px int iconSize, boolean shrinkNonAdaptiveIcons,
                          @NotNull Context context, @NotNull PackageInfo packageInfo) {
        mContext = context;
        mLoader = new AppIconLoader(iconSize, shrinkNonAdaptiveIcons, context);
        mPackageInfo = packageInfo;
    }

    @NotNull
    @Override
    public Object fetch(@NotNull Continuation<? super FetchResult> continuation) {
        Bitmap icon = mLoader.loadIcon(mPackageInfo.applicationInfo);
        return new DrawableResult(new BitmapDrawable(mContext.getResources(), icon), true,
                DataSource.DISK);
    }

    public static class Factory implements Fetcher.Factory<PackageInfo> {
        @Px
        private final int mIconSize;
        private final boolean mShrinkNonAdaptiveIcons;
        @NotNull
        private final Context mContext;

        public Factory(@Px int iconSize, boolean shrinkNonAdaptiveIcons,
                       @NotNull Context context) {
            mIconSize = iconSize;
            mShrinkNonAdaptiveIcons = shrinkNonAdaptiveIcons;
            mContext = context.getApplicationContext();
        }

        @NotNull
        @Override
        public Fetcher create(@NotNull PackageInfo packageInfo,
                              @NotNull Options options,
                              @NotNull ImageLoader imageLoader) {
            return new AppIconFetcher(mIconSize, mShrinkNonAdaptiveIcons, mContext, packageInfo);
        }
    }
}
