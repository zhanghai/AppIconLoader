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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;

import java.util.concurrent.ConcurrentLinkedQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import me.zhanghai.android.appiconloader.iconloaderlib.BaseIconFactory;

public class AppIconLoader {
    @Px
    private final int mIconSize;
    @NonNull
    private final Context mContext;

    @NonNull
    private final ConcurrentLinkedQueue<IconFactory> mIconFactoryPool =
            new ConcurrentLinkedQueue<>();

    public AppIconLoader(@Px int iconSize, @NonNull Context context) {
        mIconSize = iconSize;
        mContext = context;
    }

    @NonNull
    public static String getIconKey(@NonNull ApplicationInfo applicationInfo, long versionCode,
                                    @NonNull Context context) {
        UserHandle user = UserHandleCompat.getUserHandleForUid(applicationInfo.uid);
        return applicationInfo.packageName + ":" + versionCode + ":"
                + UserSerialNumberCache.getSerialNumber(user, context);
    }

    @NonNull
    public static String getIconKey(@NonNull PackageInfo packageInfo, @NonNull Context context) {
        return getIconKey(packageInfo.applicationInfo, PackageInfoCompat.getLongVersionCode(
                packageInfo), context);
    }

    @NonNull
    public Drawable loadIcon(@NonNull ApplicationInfo applicationInfo,
                             boolean shrinkNonAdaptiveIcons) {
        Drawable unbadgedIcon = PackageItemInfoCompat.loadUnbadgedIcon(applicationInfo,
                mContext.getPackageManager());
        UserHandle user = UserHandleCompat.getUserHandleForUid(applicationInfo.uid);
        IconFactory iconFactory = mIconFactoryPool.poll();
        if (iconFactory == null) {
            iconFactory = new IconFactory(mIconSize, mContext);
        }
        Bitmap iconBitmap;
        try {
            iconBitmap = iconFactory.createBadgedIconBitmap(unbadgedIcon, user,
                    shrinkNonAdaptiveIcons).icon;
        } finally {
            mIconFactoryPool.offer(iconFactory);
        }
        return new BitmapDrawable(mContext.getResources(), iconBitmap);
    }

    private static class IconFactory extends BaseIconFactory {
        public IconFactory(@Px int iconBitmapSize, @NonNull Context context) {
            super(context, context.getResources().getConfiguration().densityDpi, iconBitmapSize);
        }
    }
}
