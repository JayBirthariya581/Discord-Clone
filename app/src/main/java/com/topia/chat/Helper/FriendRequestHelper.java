package com.topia.chat.Helper;

public class FriendRequestHelper {
    String senderUID,receiverUID;

    public FriendRequestHelper(String senderUID, String receiverUID) {
        this.senderUID = senderUID;
        this.receiverUID = receiverUID;
    }

    public String getSenderUID() {
        return senderUID;
    }

    public void setSenderUID(String senderUID) {
        this.senderUID = senderUID;
    }

    public String getReceiverUID() {
        return receiverUID;
    }

    public void setReceiverUID(String receiverUID) {
        this.receiverUID = receiverUID;
    }
}
