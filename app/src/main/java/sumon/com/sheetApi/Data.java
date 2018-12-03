package sumon.com.sheetApi;

import io.realm.RealmObject;

public class Data extends RealmObject {
    String name, phnNo, address, issue;
    boolean isSync;

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    public Data() {
    }

    public Data(String name, String phnNo, String address, String issue, boolean isSync) {
        this.name = name;
        this.phnNo = phnNo;
        this.address = address;
        this.issue = issue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhnNo() {
        return phnNo;
    }

    public void setPhnNo(String phnNo) {
        this.phnNo = phnNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }
}
