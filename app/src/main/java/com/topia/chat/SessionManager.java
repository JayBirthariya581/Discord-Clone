package com.topia.chat;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    public static  final String IS_LOGIN = "IsLoggedIn";
    public static  final String KEY_USERNAME = "userName";
    public static  final String KEY_PROFILE_IMAGE = "profileImage";
    public static  final String KEY_UID = "keyUID";

    public static  final String KEY_EMAIL = "email";
    public static  final String KEY_TOKEN = "token";

    public static  final String KEY_PASSWORD = "password";



    public SessionManager(Context _context){
        context = _context;
        usersSession = _context.getSharedPreferences("usersloginSession",Context.MODE_PRIVATE);
        editor = usersSession.edit();

    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }



    public void createLoginSession(String UID, String username, String profileImage, String email, String password,String token){
        editor.putBoolean(IS_LOGIN,true);

        /* Personal*/


        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_USERNAME,username);
        editor.putString(KEY_PROFILE_IMAGE,profileImage);
        editor.putString(KEY_UID,UID);
        editor.putString(KEY_TOKEN,token);

        editor.putString(KEY_PASSWORD,password);





        editor.commit();
    }

    public HashMap<String,String> getUsersDetailsFromSessions(){
        HashMap<String,String> userData = new HashMap<String,String>();


        userData.put(KEY_EMAIL,usersSession.getString(KEY_EMAIL,null));

        userData.put(KEY_PASSWORD,usersSession.getString(KEY_PASSWORD,null));

        userData.put(KEY_UID,usersSession.getString(KEY_UID,null));


        userData.put(KEY_USERNAME,usersSession.getString(KEY_USERNAME,null));

        userData.put(KEY_TOKEN,usersSession.getString(KEY_TOKEN,null));

        userData.put(KEY_PROFILE_IMAGE,usersSession.getString(KEY_PROFILE_IMAGE,null));






        return  userData;
    }






    public Boolean checkLogin(){
        if(usersSession.getBoolean(IS_LOGIN,false)){
                return true;
        }else {
            return false;
        }

    }

    public void logoutSession(){
        editor.clear();
        editor.apply();
        editor.commit();
    }


}
