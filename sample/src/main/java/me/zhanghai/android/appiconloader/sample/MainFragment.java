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

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import me.zhanghai.android.appiconloader.sample.databinding.MainFragmentBinding;

public class MainFragment extends Fragment {
    private static final Uri GITHUB_URI = Uri.parse("https://github.com/zhanghai/AppIconLoader");

    private MainFragmentBinding mBinding;

    @NonNull
    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = MainFragmentBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        activity.setSupportActionBar(mBinding.toolbar);

        if (savedInstanceState == null) {
            setNavigationCheckedItem(R.id.glide);
        }
        mBinding.navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        FastScrollerLiftOnScrollHack.hack(mBinding.appBarLayout, R.id.recycler);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mBinding.drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_view_on_github:
                viewOnGitHub();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewOnGitHub() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, GITHUB_URI));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (item.getItemId()) {
            case R.id.glide:
            case R.id.coil:
            case R.id.synchronous:
                setNavigationCheckedItem(itemId);
                mBinding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            default:
                return false;
        }
    }

    private void setNavigationCheckedItem(@IdRes int itemId) {
        MenuItem item = mBinding.navigationView.getCheckedItem();
        if (item != null && item.getItemId() == itemId) {
            return;
        }
        Fragment fragment;
        switch (itemId) {
            case R.id.glide:
                fragment = GlideAppListFragment.newInstance();
                break;
            case R.id.coil:
                fragment = CoilAppListFragment.newInstance();
                break;
            case R.id.synchronous:
                fragment = SynchronousAppListFragment.newInstance();
                break;
            default:
                throw new AssertionError(itemId);
        }
        getChildFragmentManager().beginTransaction()
                .replace(R.id.containerLayout, fragment)
                .commit();
        mBinding.navigationView.setCheckedItem(itemId);
    }

    public boolean onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        return false;
    }
}
