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

package me.zhanghai.android.appiconloader.sample;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.zhanghai.android.appiconloader.AppIconLoader;

public class SynchronousAppListFragment extends AppListFragment {
    private AppIconLoader mAppIconLoader;

    @NonNull
    public static SynchronousAppListFragment newInstance() {
        return new SynchronousAppListFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Context context = requireContext();
        mAppIconLoader = new AppIconLoader(context.getResources().getDimensionPixelSize(
                R.dimen.app_icon_size), context.getApplicationContext());
    }

    @NonNull
    @Override
    protected AppListAdapter.IconLoader onCreateIconLoader() {
        return (imageView, packageInfo) -> imageView.setImageBitmap(mAppIconLoader.loadIcon(
                packageInfo.applicationInfo, false));
    }
}
