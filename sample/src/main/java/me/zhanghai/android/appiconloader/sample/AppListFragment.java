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

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import me.zhanghai.android.appiconloader.sample.databinding.AppListFragmentBinding;
import me.zhanghai.android.fastscroll.FastScrollerBuilder;

public abstract class AppListFragment extends Fragment {
    private AppListFragmentBinding mBinding;

    private AppListAdapter mAdapter;

    private AppListViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = AppListFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = requireActivity();
        mBinding.recycler.setLayoutManager(new LinearLayoutManager(activity));
        mAdapter = new AppListAdapter(onCreateIconLoader());
        mBinding.recycler.setAdapter(mAdapter);
        new FastScrollerBuilder(mBinding.recycler).useMd2Style().build();

        mViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(
                activity.getApplication())).get(AppListViewModel.class);
        mViewModel.getAppListLiveData().observe(getViewLifecycleOwner(), apps -> {
            mBinding.progress.setVisibility(View.GONE);
            mAdapter.replace(apps);
        });
    }

    @NonNull
    protected abstract AppListAdapter.IconLoader onCreateIconLoader();
}
