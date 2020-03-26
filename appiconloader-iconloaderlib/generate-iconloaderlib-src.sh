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

mkdir -p "${LIBRARY_CLASSES_ROOT}"
rm -rf "${LIBRARY_CLASSES_ROOT}"
cp -r "${LAUNCHER3_CLASSES_ROOT}" "${LIBRARY_CLASSES_ROOT}"
rm -r "${LIBRARY_CLASSES_ROOT}/cache/"

find "${LIBRARY_CLASSES_ROOT}" -iname '*.java' -type f -print0 | xargs -0 sed -Ei \
-e 's/\bcom\.android\.launcher3\.icons\b/me.zhanghai.android.appiconloader.iconloaderlib/g'

mkdir -p "${LIBRARY_RESOURCES_ROOT}"
rm -rf "${LIBRARY_RESOURCES_ROOT}"
cp -r "${LAUNCHER3_RESOURCES_ROOT}" "${LIBRARY_RESOURCES_ROOT}"
rm "${LIBRARY_RESOURCES_ROOT}/values/config.xml"
