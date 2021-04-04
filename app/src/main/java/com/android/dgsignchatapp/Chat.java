package com.android.dgsignchatapp;

import java.security.PublicKey;

public class Chat {
    public String name;
    public String message;
    public String signedMessage;
    public boolean alert = false;
    public String pubkey;

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public Chat(String name, String message, String signedMessage, boolean alert, String pubkey) {
        this.name = name;
        this.message = message;
        this.signedMessage = signedMessage;
        this.alert = alert;
        this.pubkey = pubkey;
    }

    public boolean isAlert() {
        return alert;
    }

    public void setAlert(boolean alert) {
        this.alert = alert;
    }

    public Chat(String name, String message, String signedMessage, boolean alert) {
        this.name = name;
        this.message = message;
        this.signedMessage = signedMessage;
        this.alert = alert;
    }

    public Chat(String name, String message, String signedMessage) {
        this.name = name;
        this.message = message;
        this.signedMessage = signedMessage;
    }

    public String getSignedMessage() {
        return signedMessage;
    }

    public void setSignedMessage(String signedMessage) {
        this.signedMessage = signedMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Chat() {
    }

    public Chat(String name, String message) {
        this.name = name;
        this.message = message;
    }
}
