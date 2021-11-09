package com.topia.chat.Helper;

public class ServerHelper {
    String serverName,serverImage,serverOwner;

    public ServerHelper(String serverName, String serverImage, String serverOwner) {
        this.serverName = serverName;
        this.serverImage = serverImage;
        this.serverOwner = serverOwner;
    }


    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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
