package app.listener;

public interface MessageListener {
    public void onMessage(String fromLogin, String msgBody);
}
