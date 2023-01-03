package com.pahat.moments.ui.fragments.mainsearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.databinding.FragmentMainSearchBinding;
import com.pahat.moments.ui.activities.otherprofile.OtherProfileActivity;
import com.pahat.moments.ui.adapters.ItemUserAdapter;
import com.pahat.moments.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainSearchFragment extends Fragment {

    private FragmentMainSearchBinding binding;
    private ItemUserAdapter itemUserAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.fragmentMainSearchLoadingLottie.setVisibility(View.GONE);
        RecyclerView recyclerView = binding.fragmentMainSearchRvResult;
        itemUserAdapter = new ItemUserAdapter((v, data) -> {
            Intent intent = new Intent(requireContext(), OtherProfileActivity.class);
            intent.putExtra(OtherProfileActivity.USER_INTENT_KEY, data);
            startActivity(intent);

        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(itemUserAdapter);

        binding.fragmentMainSearchSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showLoading();
                Utilities.makeToast(getActivity(), query);
                APIUtil.getAPIService().getUserByUsernameOrFullName(query).enqueue(new Callback<APIResponse<List<APIUser>>>() {
                    @Override
                    public void onResponse(Call<APIResponse<List<APIUser>>> call, Response<APIResponse<List<APIUser>>> response) {
                        hideLoading();
                        if (response.isSuccessful()) {
                            List<User> userList = new ArrayList<>();

                            for (APIUser apiUser : response.body().getData()) {
                                userList.add(new User(
                                        apiUser.getUserId(),
                                        apiUser.getUsername(),
                                        apiUser.getFullName(),
                                        apiUser.getImageUrl()
                                ));
                            }

                            itemUserAdapter.submitList(userList);
                        } else {
                            Utilities.makeToast(getActivity(), "Failed to show search result");
                        }
                    }

                    @Override
                    public void onFailure(Call<APIResponse<List<APIUser>>> call, Throwable t) {
                        hideLoading();
                        Utilities.makeToast(getActivity(), "Failed to show search result");
                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void showLoading() {
        binding.fragmentMainSearchLoadingLottie.setVisibility(View.VISIBLE);
    }

    public void hideLoading() {
        binding.fragmentMainSearchLoadingLottie.setVisibility(View.GONE);
    }
}