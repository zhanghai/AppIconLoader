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

package me.zhanghai.android.appiconloader.glide;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import me.zhanghai.android.appiconloader.AppIconLoader;

public class AppIconModelLoader implements ModelLoader<PackageInfo, Bitmap> {
    @NonNull
    private final AppIconLoader mLoader;
    @NonNull
    private final Context mContext;

    private AppIconModelLoader(@Px int iconSize, boolean shrinkNonAdaptiveIcons,
                               @NonNull Context context) {
        mLoader = new AppIconLoader(iconSize, shrinkNonAdaptiveIcons, context);
        mContext = context;
    }

    @Override
    public boolean handles(@NonNull PackageInfo model) {
        return true;
    }

    @Nullable
    @Override
    public LoadData<Bitmap> buildLoadData(@NonNull PackageInfo model, int width, int height,
                                          @NonNull Options options) {
        return new LoadData<>(new ObjectKey(AppIconLoader.getIconKey(model, mContext)), new Fetcher(
                mLoader, model.applicationInfo));
    }

    private static class Fetcher implements DataFetcher<Bitmap> {
        @NonNull
        private final AppIconLoader mLoader;
        @NonNull
        private final ApplicationInfo mApplicationInfo;

        public Fetcher(@NonNull AppIconLoader loader, @NonNull ApplicationInfo applicationInfo) {
            mLoader = loader;
            mApplicationInfo = applicationInfo;
        }

        @Override
        public void loadData(@NonNull Priority priority,
                             @NonNull DataCallback<? super Bitmap> callback) {
            try {
                Bitmap icon = mLoader.loadIcon(mApplicationInfo);
                callback.onDataReady(icon);
            } catch (Exception e) {
                callback.onLoadFailed(e);
            }
        }

        @Override
        public void cleanup() {}

        @Override
        public void cancel() {}

        @NonNull
        @Override
        public Class<Bitmap> getDataClass() {
            return Bitmap.class;
        }

        @NonNull
        @Override
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public static class Factory implements ModelLoaderFactory<PackageInfo, Bitmap> {
        @Px
        private final int mIconSize;
        private final boolean mShrinkNonAdaptiveIcons;
        @NonNull
        private final Context mContext;

        public Factory(@Px int iconSize, boolean shrinkNonAdaptiveIcons, @NonNull Context context) {
            mIconSize = iconSize;
            mShrinkNonAdaptiveIcons = shrinkNonAdaptiveIcons;
            mContext = context.getApplicationContext();
        }

        @NonNull
        @Override
        public ModelLoader<PackageInfo, Bitmap> build(
                @NonNull MultiModelLoaderFactory multiFactory) {
            return new AppIconModelLoader(mIconSize, mShrinkNonAdaptiveIcons, mContext);
        }

        @Override
        public void teardown() {}
    }
}
