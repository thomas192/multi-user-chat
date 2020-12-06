package com.muc;

public interface UserStatusListener {
    /** Notifies when a user comes online */
    public void online(String login);

    /** Notifies when a user goes offline */
    public void offline(String login);
}
