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
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import me.zhanghai.android.appiconloader.iconloaderlib.BaseIconFactory;
import me.zhanghai.android.appiconloader.iconloaderlib.BitmapInfo;

public class AppIconLoader {
    @Px
    private final int mIconSize;
    private final boolean mShrinkNonAdaptiveIcons;
    @NonNull
    private final Context mContext;

    @NonNull
    private final ConcurrentLinkedQueue<IconFactory> mIconFactoryPool =
            new ConcurrentLinkedQueue<>();

    static {
        try {
            Class<?> adaptiveIconDrawableInjectorClass = Class.forName("android.graphics.drawable.AdaptiveIconDrawableInjector");
            Field maskPaintField = adaptiveIconDrawableInjectorClass.getDeclaredField("MASK_PAINT");
            maskPaintField.setAccessible(true);
            Paint maskPaint = (Paint)maskPaintField.get(null);
            maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST));
        } catch (Exception e) {
        }
    }

    public AppIconLoader(@Px int iconSize, boolean shrinkNonAdaptiveIcons,
                         @NonNull Context context) {
        mIconSize = iconSize;
        mShrinkNonAdaptiveIcons = shrinkNonAdaptiveIcons;
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
    public Bitmap loadIcon(@NonNull ApplicationInfo applicationInfo, boolean isInstantApp) {
        Drawable unbadgedIcon = PackageItemInfoCompat.loadUnbadgedIcon(applicationInfo,
                mContext.getPackageManager());
        UserHandle user = UserHandleCompat.getUserHandleForUid(applicationInfo.uid);
        IconFactory iconFactory = mIconFactoryPool.poll();
        if (iconFactory == null) {
            iconFactory = new IconFactory(mIconSize, mContext);
        }
        try {
            return iconFactory.createBadgedIconBitmap(unbadgedIcon, user, mShrinkNonAdaptiveIcons,
                    isInstantApp).icon;
        } finally {
            mIconFactoryPool.offer(iconFactory);
        }
    }

    @NonNull
    public Bitmap loadIcon(@NonNull ApplicationInfo applicationInfo) {
        return loadIcon(applicationInfo, false);
    }

    private static class IconFactory extends BaseIconFactory {
        private final float[] mTempScale = new float[1];

        public IconFactory(@Px int iconBitmapSize, @NonNull Context context) {
            super(context, context.getResources().getConfiguration().densityDpi, iconBitmapSize,
                    true);
        }

        @NonNull
        public BitmapInfo createBadgedIconBitmap(@NonNull Drawable icon, @Nullable UserHandle user,
                                                 boolean shrinkNonAdaptiveIcons,
                                                 boolean isInstantApp) {
            return super.createBadgedIconBitmap(icon, user, shrinkNonAdaptiveIcons, isInstantApp,
                    mTempScale);
        }
    }
}
