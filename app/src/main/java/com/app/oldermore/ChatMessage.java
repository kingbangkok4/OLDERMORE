package com.app.oldermore;

/**
 * Created by Administrator on 1/8/2017.
 */

public class ChatMessage {
    public boolean left;
    public String message;
    public String image;

    public ChatMessage(boolean left , String message, String image) {
        // TODO Auto-generated constructor stub
        super();
        this.left=left;
        this.message = message;
        this.image = image;
    }
}