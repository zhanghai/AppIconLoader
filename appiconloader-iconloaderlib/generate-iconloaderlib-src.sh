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
    "BitmapInfo.java"
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
patch -d "${LIBRARY_CLASSES_ROOT}" -p0 <iconloaderlib-classes.patch

rm -rf "${LIBRARY_RESOURCES_ROOT}"
for file in "${LIBRARY_RESOURCES_FILES[@]}"; do
    mkdir -p "$(dirname "${LIBRARY_RESOURCES_ROOT}/${file}")"
    cp "${LAUNCHER3_RESOURCES_ROOT}/${file}" "${LIBRARY_RESOURCES_ROOT}/${file}"
done

find "${LIBRARY_RESOURCES_ROOT}" -iname '*.xml' -type f -print0 | xargs -0 sed -Ei \
    -e 's/\bcom\.android\.launcher3\.icons\b/me.zhanghai.android.appiconloader.iconloaderlib/g'
patch -d "${LIBRARY_RESOURCES_ROOT}" -p0 <iconloaderlib-resources.patch
