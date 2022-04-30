#!/bin/bash

# Copyright 2020 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -eu

LAUNCHER3_CLASSES_ROOT="$1/src/com/android/launcher3/icons"
LAUNCHER3_RESOURCES_ROOT="$1/res"
LIBRARY_CLASSES_ROOT="$2/java/me/zhanghai/android/appiconloader/iconloaderlib"
LIBRARY_RESOURCES_ROOT="$2/res"
LIBRARY_CLASSES_FILES=(
    "BaseIconFactory.java"
    #"BitmapInfo.java"
    "BitmapRenderer.java"
    "ColorExtractor.java"
    "FixedScaleDrawable.java"
    "GraphicsUtils.java"
    "IconNormalizer.java"
    "ShadowGenerator.java"
)
LIBRARY_RESOURCES_FILES=(
    "drawable/ic_instant_app_badge.xml"
    "drawable-v26/adaptive_icon_drawable_wrapper.xml"
    "values/colors.xml"
    "values/dimens.xml"
)

rm -rf "${LIBRARY_CLASSES_ROOT}"
for file in "${LIBRARY_CLASSES_FILES[@]}"; do
    mkdir -p "$(dirname "${LIBRARY_CLASSES_ROOT}/${file}")"
    cp "${LAUNCHER3_CLASSES_ROOT}/${file}" "${LIBRARY_CLASSES_ROOT}/${file}"
done

find "${LIBRARY_CLASSES_ROOT}" -iname '*.java' -type f -print0 | xargs -0 sed -Ei \
    -e 's/\bcom\.android\.launcher3\.icons\b/me.zhanghai.android.appiconloader.iconloaderlib/g'
sed -Ei \
    -e '/^\s*public class FixedScaleDrawable extends DrawableWrapper \{\s*$/i@androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.O)' \
    "${LIBRARY_CLASSES_ROOT}/FixedScaleDrawable.java"
cat >"${LIBRARY_CLASSES_ROOT}/BitmapInfo.java" <<EOF
/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.zhanghai.android.appiconloader.iconloaderlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.UserHandle;

import androidx.annotation.NonNull;

public class BitmapInfo {

    public final Bitmap icon;
    public final int color;

    public BitmapInfo(Bitmap icon, int color) {
        this.icon = icon;
        this.color = color;
    }

    public static BitmapInfo fromBitmap(@NonNull Bitmap bitmap) {
        return of(bitmap, 0);
    }

    public static BitmapInfo of(@NonNull Bitmap bitmap, int color) {
        return new BitmapInfo(bitmap, color);
    }

    /**
     * Interface to be implemented by drawables to provide a custom BitmapInfo
     */
    public interface Extender {

        /**
         * Called for creating a custom BitmapInfo
         */
        BitmapInfo getExtendedInfo(Bitmap bitmap, int color,
                BaseIconFactory iconFactory, float normalizationScale, UserHandle user);

        /**
         * Called to draw the UI independent of any runtime configurations like time or theme
         */
        void drawForPersistence(Canvas canvas);

        /**
         * Returns a new icon with theme applied
         */
        Drawable getThemedDrawable(Context context);
    }
}
EOF

rm -rf "${LIBRARY_RESOURCES_ROOT}"
for file in "${LIBRARY_RESOURCES_FILES[@]}"; do
    mkdir -p "$(dirname "${LIBRARY_RESOURCES_ROOT}/${file}")"
    cp "${LAUNCHER3_RESOURCES_ROOT}/${file}" "${LIBRARY_RESOURCES_ROOT}/${file}"
done

find "${LIBRARY_RESOURCES_ROOT}" -iname '*.xml' -type f -print0 | xargs -0 sed -Ei \
    -e 's/\bcom\.android\.launcher3\.icons\b/me.zhanghai.android.appiconloader.iconloaderlib/g'
sed -Ei \
    -e '/^\s*<!-- Yellow 600, used for highlighting "important" conversations in settings & notifications -->\s*$/d' \
    -e '/^\s*<color name="important_conversation">#f9ab00<\/color>\s*$/d' \
    "${LIBRARY_RESOURCES_ROOT}/values/colors.xml"
