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


    /** file name blob id */
    private HashMap<String, String> fileBlobs;

    public Commit(String parent, String msg) {
        this.parent = parent;
        this.timestamp = new Date();
        this.msg = msg;
        this.fileBlobs = new HashMap<String, String>();
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

    /** serialize the commit obj to the file in OBJ_DIR*/
    public void writeCommit() {
        String id = getId();
        writeObject((join(OBJ_DIR, id)), this);
    }

    private String formatTimestamp() {
        SimpleDateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.ENGLISH);
        return format.format(timestamp);
    }



    @Override
    public String toString() {
        return "===" + "\ncommit " + getId() + "\nDate: " + formatTimestamp() + "\n" + msg + "\n";
    }

    public HashMap<String, String> getFileBlobs() {
        return fileBlobs;
    }

    public void addBlob(String name, String blob) {
        fileBlobs.put(name, blob);
    }

    public String getParent() {
        return parent;
    }

}
