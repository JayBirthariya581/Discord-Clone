package com.topia.chat.Helper;

public class ServerShareHelper {
    String serverID,serverImage,serverOwner;

    public ServerShareHelper(String serverID, String serverImage, String serverOwner) {
        this.serverID = serverID;
        this.serverImage = serverImage;
        this.serverOwner = serverOwner;
    }


    public String getserverID() {
        return serverID;
    }

    public void setserverID(String serverID) {
        this.serverID = serverID;
    }

    public String getServerImage() {
        return serverImage;
    }

    public void setServerImage(String serverImage) {
        this.serverImage = serverImage;
    }

    public String getServerOwner() {
        return serverOwner;
    }

    public void setServerOwner(String serverOwner) {
        this.serverOwner = serverOwner;
    }
}
