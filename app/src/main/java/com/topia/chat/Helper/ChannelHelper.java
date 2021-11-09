package com.topia.chat.Helper;

public class ChannelHelper {
    String channelName,memberCount;

    public ChannelHelper(String channelName, String memberCount) {
        this.channelName = channelName;
        this.memberCount = memberCount;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

}
