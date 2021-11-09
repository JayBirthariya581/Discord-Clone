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
import com.topia.chat.Adapters.FriendListAdapter;
import com.topia.chat.Adapters.FriendRequestAdapter;
import com.topia.chat.Helper.FriendRequestHelper;
import com.topia.chat.Models.FriendRequestModel;
import com.topia.chat.Models.RequestSenderModel;
import com.topia.chat.Models.UserTest;
import com.topia.chat.R;
import com.topia.chat.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;


public class FriendListFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<RequestSenderModel> requests;
    FriendListAdapter friendListAdapter;
    ArrayList<String> friendList_uids;
    DatabaseReference DBref;
    SwipeRefreshLayout mySwipeRefreshLayout;
    SessionManager sessionManager;
    ProgressBar progressBar;
    TextView emt;
    HashMap<String,String> UserDetails;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rv_FriendList);
        requests = new ArrayList<>();
        friendList_uids= new ArrayList<>();
        mySwipeRefreshLayout=view.findViewById(R.id.swiperefresh);
        friendListAdapter = new FriendListAdapter(requests,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(friendListAdapter);
        progressBar=view.findViewById(R.id.progress_bar);
        emt = view.findViewById(R.id.emText);
        emt.setVisibility(View.GONE);
        //Variables
        sessionManager = new SessionManager(getContext());
        UserDetails = sessionManager.getUsersDetailsFromSessions();
        DBref = FirebaseDatabase.getInstance().getReference("users");

        progressBar.setVisibility(View.VISIBLE);

        makeList();

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
        friendList_uids.clear();
        requests.clear();
        DBref.child(UserDetails.get(SessionManager.KEY_UID)).child("Friends").child("FriendList")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot request_List) {

                        if(request_List.exists()){
                            for(DataSnapshot single_Request : request_List.getChildren()){
                                friendList_uids.add(single_Request.getValue(String.class));

                            }


                            for(int i=0;i<friendList_uids.size();i++){
                                DBref.child(friendList_uids.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot senderDetail) {
                                        RequestSenderModel Details = senderDetail.getValue(RequestSenderModel.class);

                                        requests.add(Details);
                                        friendListAdapter.notifyDataSetChanged();

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
    }
}