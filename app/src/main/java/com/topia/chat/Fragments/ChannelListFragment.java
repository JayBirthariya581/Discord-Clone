package com.topia.chat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Adapters.ChannelListAdapter;
import com.topia.chat.Models.ModelChannelList;
import com.topia.chat.R;
import com.topia.chat.SessionManager;
import com.topia.chat.ViewModels.PresenceViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChannelListFragment extends Fragment {


    RecyclerView recyclerView;
    LinearLayout upper;
    DatabaseReference DBr ;
   //ProgressDialog dialog;
    SessionManager sessionManager;
    PresenceViewModel presenceViewModel;
    ChannelListAdapter adapter;
    SwipeRefreshLayout mySwipeRefreshLayout;
    //PresenceViewModel presenceViewModel;
    ArrayList<ModelChannelList> mlist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channel_list, container, false);
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        

        upper = view.findViewById(R.id.upper_channel);
        mySwipeRefreshLayout=view.findViewById(R.id.swiperefresh_CL);
        recyclerView = view.findViewById(R.id.rv_channel_list);

        presenceViewModel = new ViewModelProvider(this).get(PresenceViewModel.class);

        sessionManager = new SessionManager(getContext());
        mlist = new ArrayList<>();
        adapter = new ChannelListAdapter(mlist,getContext());

        adapter.setServerOwner(sessionManager.getUsersDetailsFromSessions()
                .get(SessionManager.KEY_UID));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(adapter);










    }


    public void makeList(String serverID){
        DatabaseReference DBrf = FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions()
                .get(SessionManager.KEY_UID)).child("servers").child(serverID).child("channels");

        DBrf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {


                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ModelChannelList mo = dataSnapshot.getValue(ModelChannelList.class);
                    mo.setChannelID(dataSnapshot.getKey());
                    //modelCustomer.setServerUid(dataSnapshot.getKey());
                    mlist.add(mo);
                }
                adapter.notifyDataSetChanged();
                //dialog.dismiss();
                //progressBar.setVisibility(View.GONE);

            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }



}