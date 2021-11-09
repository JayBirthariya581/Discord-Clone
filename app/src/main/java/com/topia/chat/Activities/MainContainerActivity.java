package com.topia.chat.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.topia.chat.Fragments.FriendsFragment;
import com.topia.chat.Fragments.ProfileFragment;
import com.topia.chat.Fragments.ServerListFragment;
import com.topia.chat.Fragments.announcementFragment;
import com.topia.chat.R;
import com.topia.chat.SessionManager;
import com.topia.chat.ViewModels.PresenceViewModel;

import java.util.HashMap;
import java.util.List;

public class MainContainerActivity extends AppCompatActivity {

    SessionManager sessionManager;
    BottomNavigationView bottomNavigationView;
    Fragment selectorFragment;
    PresenceViewModel presenceViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);
        sessionManager = new SessionManager(MainContainerActivity.this);
        presenceViewModel = new ViewModelProvider(MainContainerActivity.this).get(PresenceViewModel.class);

        presenceViewModel.setUid(sessionManager.getUsersDetailsFromSessions().get(SessionManager.KEY_UID));
        presenceViewModel.setPresence("Online");

        getWindow().setStatusBarColor(ContextCompat.getColor(MainContainerActivity.this,R.color.status2));


        Fragment f1 = new ServerListFragment();
        Fragment f2 = new FriendsFragment();
        Fragment f3 = new announcementFragment();
        Fragment f4 = new ProfileFragment();


        FragmentManager fm = getSupportFragmentManager();

        fm.beginTransaction().add(R.id.fragmentContainerView,f4,"4").hide(f4).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView,f3,"3").hide(f3).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView,f2,"2").hide(f2).commit();
        fm.beginTransaction().add(R.id.fragmentContainerView,f1,"1").commit();
        selectorFragment = f1;

        bottomNavigationView = findViewById(R.id.bnv);






        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()){

                    case R.id.serverListFragment:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f1).commit();
                        selectorFragment=  f1;
                        break;


                    case R.id.friendsFragment:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f2).commit();
                        selectorFragment=  f2;
                        break;
                    case R.id.announcementFragment:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f3).commit();
                        selectorFragment=  f3;
                        break;


                        case R.id.profileFragment:
                        getSupportFragmentManager().beginTransaction().hide(selectorFragment).show(f4).commit();
                        selectorFragment=  f4;
                        break;




                }




                return true;
            }
        });





    }


}