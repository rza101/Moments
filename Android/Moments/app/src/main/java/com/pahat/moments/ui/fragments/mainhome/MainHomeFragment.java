package com.pahat.moments.ui.fragments.mainhome;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.databinding.FragmentMainHomeBinding;
import com.pahat.moments.ui.activities.detailpost.DetailPostActivity;
import com.pahat.moments.ui.adapters.ItemPostAdapter;
import com.pahat.moments.util.Utilities;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainHomeFragment extends Fragment {

    private FragmentMainHomeBinding binding;
    private ItemPostAdapter itemPostAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemPostAdapter = new ItemPostAdapter((v, data) -> {
            if (isAdded()) {
                startActivity(new Intent(requireContext(), DetailPostActivity.class)
                        .putExtra(DetailPostActivity.POST_INTENT_KEY, data)
                );
            }
        });
        binding.fragmentMainHomeLoadingLottie.setVisibility(View.GONE);
        binding.fragmentMainHomeRvPosts.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        binding.fragmentMainHomeRvPosts.setAdapter(itemPostAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoading();

        APIUtil.getAPIService().getAllPost().enqueue(new Callback<APIResponse<List<Post>>>() {
            @Override
            public void onResponse(Call<APIResponse<List<Post>>> call, Response<APIResponse<List<Post>>> response) {
                hideLoading();
                itemPostAdapter.submitList(response.body().getData());
            }

            @Override
            public void onFailure(Call<APIResponse<List<Post>>> call, Throwable t) {
                hideLoading();
                if (isAdded()) {
                    Utilities.makeToast(requireContext(), "Failed to load posts");
                }
            }
        });
    }

    public void showLoading() {
        binding.fragmentMainHomeLoadingLottie.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        binding.fragmentMainHomeLoadingLottie.setVisibility(View.GONE);
    }
}