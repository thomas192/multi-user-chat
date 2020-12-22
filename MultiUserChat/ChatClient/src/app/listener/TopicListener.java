package app.listener;

public interface TopicListener {
    public void onJoin(String topic);

    public void onLeave(String topic);
}
