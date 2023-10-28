package gitlet;

import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.Repository.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/** Represents a gitlet commit object.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xinfeng Fu
 */
public class Commit implements Serializable {

    private String parent;

    private Date timestamp;

    private String msg;

    private String secondParent;
    /** file name blob id */
    private HashMap<String, String> fileBlobs;

    public Commit(String parent, String msg) {
        this.parent = parent;
        this.timestamp = new Date();
        this.msg = msg;
        this.fileBlobs = new HashMap<String, String>();
        this.secondParent = null;
    }

    public void setSecondParent(String secondParent) {
        this.secondParent = secondParent;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        if (parent == null) {
            return sha1(timestamp.toString(), msg, fileBlobs.keySet().toString(), fileBlobs.values().toString());
        }
        return sha1(parent, timestamp.toString(), msg, fileBlobs.keySet().toString(), fileBlobs.values().toString());
    }



    private String formatTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return format.format(timestamp);
    }



    @Override
    public String toString() {
        if (secondParent == null) {
            return "===" + "\ncommit " + getId() + "\nDate: " + formatTimestamp() + "\n" + msg + "\n";
        }
        String res = "===" + "\ncommit " + getId();
        res += "\nMerge: " + parent.substring(0, 7) + " " + secondParent.substring(0, 7);
        res +=  "\nDate: " + formatTimestamp() + "\n" + msg + "\n";
        return res;
    }

    public HashMap<String, String> getFileBlobs() {
        return fileBlobs;
    }

    public void addBlob(String name, String blob) {
        fileBlobs.put(name, blob);
    }

    public void rmBlob(String name, String blob) {
        fileBlobs.remove(name, blob);
    }

    public String getParent() {
        return parent;
    }

    public String getMsg() {
        return msg;
    }

    /** serialize the commit obj to the file in COMMIT_DIR*/
    public void writeCommit() {
        String id = getId();
        writeObject((join(COMMIT_DIR, id)), this);
    }

    public static Commit readCurCommit(Info info) {
        return readObject(join(COMMIT_DIR, info.getBranches().get(info.getCurBranch())), Commit.class);
    }

    public static Commit readParentCommit(Info info) {
        return readObject(join(COMMIT_DIR, info.getBranches().get(info.getCurBranch())), Commit.class);
    }

    public static Commit readCommit(String id) {
        return readObject(join(COMMIT_DIR, id), Commit.class);
    }

    /**  get blob id by the file name*/
    public String getBlobId(String name) {
        return fileBlobs.get(name);
    }

}
