package scc.utils;

public class Session {

    private String uid;
    private String user;

    public Session(){
    }

    public Session(String uid, String user){
        this.uid = uid;
        this.user = user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
