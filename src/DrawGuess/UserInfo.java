package DrawGuess;

public class UserInfo {
    private String name;
    private int uid;

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isReady = false;
}
