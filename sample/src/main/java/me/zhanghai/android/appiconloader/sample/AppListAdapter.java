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

import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import me.zhanghai.android.appiconloader.sample.databinding.AppItemBinding;
import me.zhanghai.android.fastscroll.PopupTextProvider;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder>
        implements PopupTextProvider {
    @NonNull
    private final IconLoader mIconLoader;

    @NonNull
    private final List<Pair<PackageInfo, String>> mApps = new ArrayList<>();

    public AppListAdapter(@NonNull IconLoader iconLoader) {
        mIconLoader = iconLoader;

        setHasStableIds(true);
    }

    public void replace(@NonNull List<Pair<PackageInfo, String>> apps) {
        mApps.clear();
        mApps.addAll(apps);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mApps.size();
    }

    @NonNull
    private Pair<PackageInfo, String> getItem(int position) {
        return mApps.get(position);
    }

    @Override
    public long getItemId(int position) {
        PackageInfo packageInfo = getItem(position).first;
        return Objects.hash(packageInfo.packageName, PackageInfoCompat.getLongVersionCode(
                packageInfo), packageInfo.applicationInfo.uid);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(AppItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<PackageInfo, String> app = getItem(position);
        PackageInfo packageInfo = app.first;
        String label = app.second;
        mIconLoader.loadIcon(holder.binding.iconImage, packageInfo);
        holder.binding.labelText.setText(label);
        holder.binding.descriptionText.setText(
                holder.binding.descriptionText.getContext().getString(
                        R.string.app_description_format, packageInfo.packageName,
                        packageInfo.applicationInfo.uid));
        holder.binding.getRoot().setOnClickListener(view -> AppListLoader.startAppDetailsActivity(
                packageInfo, view.getContext()));
    }

    @NonNull
    @Override
    public String getPopupText(int position) {
        String label = getItem(position).second;
        return label.substring(0, label.offsetByCodePoints(0, 1)).toUpperCase(Locale.getDefault());
    }

    public interface IconLoader {
        void loadIcon(@NonNull ImageView imageView, @NonNull PackageInfo packageInfo);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public final AppItemBinding binding;

        public ViewHolder(@NonNull AppItemBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
