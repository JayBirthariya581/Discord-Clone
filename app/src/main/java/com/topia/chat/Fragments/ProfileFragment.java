package com.topia.chat.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Activities.ImageViewerActivity;
import com.topia.chat.Activities.LoginActivity;
import com.topia.chat.Activities.MainContainerActivity;
import com.topia.chat.Activities.UpdateProfileActivity;
import com.topia.chat.R;
import com.topia.chat.SessionManager;
import com.topia.chat.ViewModels.PresenceViewModel;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
TextView userName,userEmail;
CircleImageView profileImage;
ImageView backDP;
Button logout,updateProfile;
PresenceViewModel presenceViewModel;
private View view;

SessionManager sessionManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view==null){
            view =inflater.inflate(R.layout.fragment_profile, container, false);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userName = view.findViewById(R.id.UserNameProfile);
        profileImage = view.findViewById(R.id.profileimage);
        userEmail = view.findViewById(R.id.profile_user_email);
        updateProfile = view.findViewById(R.id.updateProfile);
        logout = view.findViewById(R.id.logout);
        sessionManager = new SessionManager(getContext());
        backDP = view.findViewById(R.id.back_dp);

        FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                .child("profileImage").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {



                Glide.with(getContext()).load(snapshot.getValue(String.class)).placeholder(R.drawable.avatar).into(profileImage);
                Glide.with(getContext()).load(snapshot.getValue(String.class)).placeholder(R.drawable.avatar).into(backDP);


                profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!snapshot.getValue(String.class).equals("No Image")){
                            Intent intent = new Intent(getContext(), ImageViewerActivity.class);
                            intent.putExtra("imageUrl",snapshot.getValue(String.class));

                            startActivity(intent);

                        }else{
                            Toast.makeText(getContext(), "User has no profile image", Toast.LENGTH_SHORT).show();
                        }



                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        userName.setText(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_USERNAME));
        userEmail.setText(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_EMAIL));

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("presence").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                        .setValue("Offline");

                FirebaseDatabase.getInstance().getReference("users").child(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID))
                        .child("token").setValue("-");
               sessionManager.logoutSession();
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                getContext().startActivity(intent);



            }
        });






        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UpdateProfileActivity.class));
            }
        });

    }
}