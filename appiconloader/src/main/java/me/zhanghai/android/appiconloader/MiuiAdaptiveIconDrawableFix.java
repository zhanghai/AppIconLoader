/*
 * Copyright 2021 Google LLC
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

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.lang.reflect.Field;

class MiuiAdaptiveIconDrawableFix {
    private MiuiAdaptiveIconDrawableFix() {}

    public static void apply() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> adaptiveIconDrawableInjectorClass = Class.forName(
                    "android.graphics.drawable.AdaptiveIconDrawableInjector");
            Field maskPaintField = adaptiveIconDrawableInjectorClass.getDeclaredField("MASK_PAINT");
            maskPaintField.setAccessible(true);
            Paint maskPaint = (Paint) maskPaintField.get(null);
            maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST));
        } catch (Exception e) {
            // Ignored.
        }
    }
}
