package cardgame.messages;

import java.util.List;

public class LoginSuccess {

    public final String action = "loginSuccess";

    public String videoRoomName;
    public WebradioUrl[] radioList;

    public LoginSuccess(String videoRoomName, List<WebradioUrl> radioList) {
        this.videoRoomName = videoRoomName;
        this.radioList = radioList.toArray(new WebradioUrl[radioList.size()]);
    }
}
