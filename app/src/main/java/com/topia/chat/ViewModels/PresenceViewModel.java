package com.topia.chat.ViewModels;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.topia.chat.Adapters.ChannelListAdapter;
import com.topia.chat.Adapters.ServerListAdapter;
import com.topia.chat.Helper.ServerInfoDisplay;
import com.topia.chat.Models.ModelChannelList;
import com.topia.chat.Models.ModelServerList;
import com.topia.chat.Models.ModelUserServer;
import com.topia.chat.SessionManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;



public class PresenceViewModel extends ViewModel {

    String presence;
    String uid;



    public PresenceViewModel() {

    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPresence() {
        return presence;
    }

    public void setPresence(String presence) {
        this.presence = presence;

        FirebaseDatabase.getInstance().getReference("presence").child(uid).setValue(this.presence);

    }




    @Override
    protected void onCleared() {
        super.onCleared();
        this.presence = "offline";
        FirebaseDatabase.getInstance().getReference("presence").child(uid).setValue(this.presence);
    }
}
