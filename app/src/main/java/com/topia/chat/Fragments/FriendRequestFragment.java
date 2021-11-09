package com.topia.chat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Activities.SearchFriendActivity;
import com.topia.chat.Adapters.FriendRequestAdapter;
import com.topia.chat.Helper.FriendRequestHelper;
import com.topia.chat.Models.FriendRequestModel;
import com.topia.chat.Models.RequestSenderModel;
import com.topia.chat.Models.UserTest;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;

public class FriendRequestFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<RequestSenderModel> requests;
    FriendRequestAdapter friendRequestAdapter;
    ArrayList<FriendRequestModel> requestList;
    DatabaseReference DBref;
    SwipeRefreshLayout mySwipeRefreshLayout;
    SessionManager sessionManager;
    ProgressBar progressBar;
    TextView emt;
    HashMap<String,String> UserDetails;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friend_request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.FriendRequestList);
        requests = new ArrayList<>();
        requestList= new ArrayList<>();
        mySwipeRefreshLayout=view.findViewById(R.id.swiperefresh);
        friendRequestAdapter = new FriendRequestAdapter(requests,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(friendRequestAdapter);
        progressBar=view.findViewById(R.id.progress_bar);
        emt = view.findViewById(R.id.emText);
        emt.setVisibility(View.GONE);
        //Variables
        sessionManager = new SessionManager(getContext());
        UserDetails = sessionManager.getUsersDetailsFromSessions();
        DBref = FirebaseDatabase.getInstance().getReference("users");

        progressBar.setVisibility(View.VISIBLE);


        DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("Friends").child("FriendRequests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot request_List) {

                        if(request_List.exists()){
                            for(DataSnapshot single_Request : request_List.getChildren()){
                                requestList.add(single_Request.getValue(FriendRequestModel.class));

                            }


                            for(int i=0;i<requestList.size();i++){
                                DBref.child(requestList.get(i).getSenderUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot senderDetail) {
                                        RequestSenderModel Details = senderDetail.getValue(RequestSenderModel.class);

                                        requests.add(Details);
                                        friendRequestAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });




                            }

                            progressBar.setVisibility(View.GONE);
                        }else{
                            progressBar.setVisibility(View.GONE);
                            emt.setVisibility(View.VISIBLE);

                        }











                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        makeList();
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );


    }

    private void makeList(){
        emt.setVisibility(View.GONE);
        requests.clear();
        DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("Friends").child("FriendRequests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot request_List) {

                        if(request_List.exists()){
                            for(DataSnapshot single_Request : request_List.getChildren()){
                                requestList.add(single_Request.getValue(FriendRequestModel.class));

                            }


                            for(int i=0;i<requestList.size();i++){
                                DBref.child(requestList.get(i).getSenderUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot senderDetail) {
                                        RequestSenderModel Details = senderDetail.getValue(RequestSenderModel.class);

                                        requests.add(Details);
                                        friendRequestAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });




                            }




                            progressBar.setVisibility(View.GONE);
                        }else {
                            progressBar.setVisibility(View.GONE);
                            emt.setVisibility(View.VISIBLE);


                        }






                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}

