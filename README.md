# AppIconLoader

[![Android CI status](https://github.com/zhanghai/AppIconLoader/workflows/Android%20CI/badge.svg)](https://github.com/zhanghai/AppIconLoader/actions)

Android app icon loader from AOSP Launcher3 [iconloaderlib](https://android.googlesource.com/platform/packages/apps/Launcher3/+/refs/heads/master/iconloaderlib/), with optional Glide and Coil integration.

This is not an officially supported Google product.

## Why AppIconLoader?

Because [`PackageManager.getApplicationIcon()`](https://developer.android.com/reference/android/content/pm/PackageManager#getApplicationIcon(android.content.pm.ApplicationInfo)) (or [`PackageItemInfo.loadIcon()`](https://developer.android.com/reference/android/content/pm/PackageItemInfo#loadIcon(android.content.pm.PackageManager))) just doesn't work well with [adaptive icons](https://developer.android.com/guide/practices/ui_guidelines/icon_design_adaptive). Non-adaptive icons usually have some shadow baked in (it's the recommended behavior), however adaptive icons never contain a shadow themselves, so we'll need to manually add the shadow or icons with a white background will just blend into our app's own background.

This library packaged the AOSP Launcher3 implementation for loading app icons, which has proper shadow and badging logic, and added easy integration with Glide and Coil.

Meanwhile, by passing `true` for the `shrinkNonAdaptiveIcons` parameter, this library can also synthesize adaptive icons for apps that don't have it.

## Preview

<a href="https://play.google.com/store/apps/details?id=me.zhanghai.android.appiconloader.sample" target="_blank"><img alt="Google Play" height="90" src="https://play.google.com/intl/en_US/badges/images/generic/en_badge_web_generic.png"/></a>

[Sample APK](https://github.com/zhanghai/AppIconLoader/releases/latest/download/sample-release.apk)

<img src="fastlane/metadata/android/en-US/images/phoneScreenshots/2.png" width="49%" />

## Integration

Gradle:

```gradle
// For using with Glide.
implementation 'me.zhanghai.android.appiconloader:appiconloader-glide:1.3.0'
// For using with Coil.
implementation 'me.zhanghai.android.appiconloader:appiconloader-coil:1.3.0'
// For using AppIconLoader directly.
implementation 'me.zhanghai.android.appiconloader:appiconloader:1.3.0'
// For using Launcher3 iconloaderlib directly.
implementation 'me.zhanghai.android.appiconloader:appiconloader-iconloaderlib:1.3.0'
```

## Usage

### Glide integration

See [Glide's documentation on registering a `ModuleLoader`](https://bumptech.github.io/glide/tut/custom-modelloader.html#registering-our-modelloader-with-glide).

Inside your implementation of `AppGlideModule.registerComponents()`, you can have something like the following code fragment:

```java
int iconSize = context.getResources().getDimensionPixelSize(R.dimen.app_icon_size);
registry.prepend(PackageInfo.class, Bitmap.class, new AppIconModelLoader.Factory(iconSize,
        false, context));
```

See also the sample app's [`AppGlideModule` implementation](sample/src/main/java/me/zhanghai/android/appiconloader/sample/AppGlideModule.java).

After the setup above, Glide will support loading app icons with the app's `PackageInfo`.

```java
GlideApp.with(imageView)
        .load(packageInfo)
        .into(imageView);
```

### Coil integration

```kotlin
val iconSize = context.resources.getDimensionPixelSize(R.dimen.app_icon_size)
Coil.setDefaultImageLoader {
    ImageLoader(context) {
        componentRegistry {
            add(AppIconFetcher(iconSize, false, context))
        }
    }
}
```

After the setup above, Coil will support loading app icons with the app's `PackageInfo`.

```kotlin
imageView.loadAny(packageInfo)
```

### AppIconLoader

[`AppIconLoader`](appiconloader/src/main/java/me/zhanghai/android/appiconloader/AppIconLoader.java) is the API exposed by this library, and you can simply call `AppIconLoader.loadIcon()` to load an app icon. You can also use `AppIconLoader.getIconKey()` to generate a cache key for your loaded icon.

### Launcher3 iconloaderlib

Please refer to [its source code](https://android.googlesource.com/platform/packages/apps/Launcher3/+/refs/heads/master/iconloaderlib/).

## License

    Copyright 2020 Google LLC

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
