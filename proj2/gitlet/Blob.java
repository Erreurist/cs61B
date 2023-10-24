package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static gitlet.Utils.*;
import static gitlet.Repository.*;



/**
 * Blob represents a file
 *
 * */
public class Blob implements Serializable {


    public String getContent() {
        return content;
    }

    private final String content;

    public Blob(String content) {
        this.content = content;
    }

    public void writeBlob() {
        String id = getId();
        writeObject((join(OBJ_DIR, id)), this);
    }

    public String getId() {
        return sha1(content);
    }


}
