package gitlet;
import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;
import static gitlet.Repository.*;

public class Blob implements Serializable {
    private final String content;

    public Blob(String content) {
        this.content = content;
    }

    public Blob(File file) {
        this.content = readContentsAsString(file);
    }

    public String getContent() {
        return content;
    }

    public void writeBlob() {
        String id = getId();
        writeObject((join(BLOB_DIR, id)), this);
    }

    public static String readBlobContentById(String id) {
        return readObject(join(BLOB_DIR, id), Blob.class).getContent();
    }

    public String getId() {
        return sha1(content);
    }

    public static String getFileId(File file) {
        return sha1(readContentsAsString(file));
    }

    public static String getFileId(String name) {
        return getFileId(new File(name));
    }


}
