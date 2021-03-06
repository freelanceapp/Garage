package com.semicolon.garage.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.semicolon.garage.R;
import com.semicolon.garage.adapters.NotificationAdapter;
import com.semicolon.garage.models.NotificationModel;
import com.semicolon.garage.models.UserModel;
import com.semicolon.garage.remote.Api;
import com.semicolon.garage.singletone.UserSingleTone;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Fragment_Notification extends Fragment{
    private ProgressBar progBar;
    private RecyclerView recView;
    private LinearLayout ll_no_not;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private List<NotificationModel> notificationModelList;
    private UserSingleTone userSingleTone;
    private UserModel userModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification,container,false);
        initView(view);
        return view;
    }

    public static Fragment_Notification getInstance()
    {
        return new Fragment_Notification();
    }

    private void initView(View view) {
        userSingleTone = UserSingleTone.getInstance();
        userModel = userSingleTone.getUserModel();
        notificationModelList = new ArrayList<>();
        progBar = view.findViewById(R.id.progBar);
        ll_no_not = view.findViewById(R.id.ll_no_not);
        progBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getActivity(),R.color.press_color), PorterDuff.Mode.SRC_IN);
        recView = view.findViewById(R.id.recView);
        manager = new LinearLayoutManager(getActivity());
        recView.setLayoutManager(manager);
        adapter = new NotificationAdapter(getActivity(),notificationModelList);
        recView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        getNotification();
    }

    public void getNotification() {

        notificationModelList.clear();
        Api.getService()
                .getMyNotifications(userModel.getUser_id())
                .enqueue(new Callback<List<NotificationModel>>() {
                    @Override
                    public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                        if (response.isSuccessful())
                        {
                            progBar.setVisibility(View.GONE);
                            notificationModelList.addAll(response.body());

                            if (notificationModelList.size()>0)
                            {
                                ll_no_not.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
                            }else
                                {
                                    ll_no_not.setVisibility(View.VISIBLE);
                                }
                        }

                    }

                    @Override
                    public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                        Log.e("Error",t.getMessage());
                        progBar.setVisibility(View.GONE);
                        Toast.makeText(getActivity(),R.string.something, Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
