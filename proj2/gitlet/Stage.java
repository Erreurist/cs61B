package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Stage implements Serializable {
    /** File name, blob id */
    public HashMap<String, String> stagedForAddition;

    public HashMap<String, String> stagedForRemoval;

    public Stage() {
        stagedForAddition = new HashMap<>();
        stagedForRemoval = new HashMap<>();
    }
    public boolean stagedForAdditionIsEmpty() {
        return stagedForAddition.isEmpty();
    }

    public boolean stagedForRemovalIsEmpty() {
        return stagedForRemoval.isEmpty();
    }

    public void writeStage() {
        writeObject(STAGE_FILE, this);
    }

}
