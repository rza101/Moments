package com.pahat.moments.ui.fragments.mainsearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pahat.moments.data.firebase.model.User;
import com.pahat.moments.data.network.APIUtil;
import com.pahat.moments.data.network.model.APIResponse;
import com.pahat.moments.data.network.model.APIUser;
import com.pahat.moments.data.network.model.Post;
import com.pahat.moments.databinding.FragmentMainSearchBinding;
import com.pahat.moments.ui.OnItemClick;
import com.pahat.moments.ui.activities.createpost.CreatePostActivity;
import com.pahat.moments.ui.activities.detailpost.DetailPostActivity;
import com.pahat.moments.ui.activities.otherprofile.OtherProfileActivity;
import com.pahat.moments.ui.activities.savedpost.SavedPostActivity;
import com.pahat.moments.ui.adapters.ItemPostAdapter;
import com.pahat.moments.ui.adapters.ItemUserAdapter;
import com.pahat.moments.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainSearchFragment extends Fragment {

    private FragmentMainSearchBinding binding;
    private ItemUserAdapter itemUseradapter;
    private DatabaseReference mDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        RecyclerView recyclerView = binding.fragmentMainSearchRvResult;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        binding.fragmentMainSearchSvSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Utilities.makeToast(getActivity(), query);
                APIUtil.getAPIService().getUserByUID(query).enqueue(new Callback<APIResponse<APIUser>>() {
                    @Override
                    public void onResponse(Call<APIResponse<APIUser>> call, Response<APIResponse<APIUser>> response) {

                    }

                    @Override
                    public void onFailure(Call<APIResponse<APIUser>> call, Throwable t) {
                        Utilities.makeToast(getActivity(), "Failed to load data");
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}