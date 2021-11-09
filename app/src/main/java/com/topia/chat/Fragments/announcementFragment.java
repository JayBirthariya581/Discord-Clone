package com.topia.chat.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Activities.CreateAnnouncementActivity;
import com.topia.chat.Adapters.AnnAdapter;
import com.topia.chat.Models.ModelAnn;
import com.topia.chat.R;
import com.topia.chat.SessionManager;
import com.topia.chat.databinding.FragmentAnnouncementBinding;

import java.util.ArrayList;


public class announcementFragment extends Fragment {
    DatabaseReference DBref;
    SessionManager sessionManager;
    ProgressDialog progressDialog;
    FragmentAnnouncementBinding binding;
    ArrayList<ModelAnn> anns;
    AnnAdapter annAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAnnouncementBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        DBref = FirebaseDatabase.getInstance().getReference("owners");
        sessionManager = new SessionManager(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait");
        anns = new ArrayList<>();
        annAdapter = new AnnAdapter(getContext(),anns);

        binding.rvAnn.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.rvAnn.setLayoutManager(linearLayoutManager);
        binding.rvAnn.setAdapter(annAdapter);
        Query checkOwner = DBref.child("ownerList").orderByValue().equalTo(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL));

        checkOwner.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    annAdapter.setStatus("Owner");
                    binding.makeAnn.setVisibility(View.VISIBLE);
                    binding.makeAnn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(getContext(), CreateAnnouncementActivity.class));
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        makeList();



        binding.srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeList();
                binding.srl.setRefreshing(false);
            }
        });





    }

    private void makeList() {
        anns.clear();
        progressDialog.show();

        DBref.child("announcements").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){


                    for(DataSnapshot a : snapshot.getChildren()){
                        ModelAnn m = a.getValue(ModelAnn.class);

                        anns.add(m);

                    }

                    annAdapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    binding.emText.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}