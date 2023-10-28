package gitlet;


import java.io.File;
import java.io.Serializable;
import java.util.TreeMap;

import static gitlet.Blob.getFileId;
import static gitlet.Repository.*;
import static gitlet.Utils.*;

public class Info implements Serializable {

    /** File name, blob id */
    private TreeMap<String, String> stagedForAddition;

    private TreeMap<String, String> stagedForRemoval;
    /** branch name   the most recent commit id */
    private TreeMap<String, String> branches;
    /** current branch name */
    private String curBranch;

    public String getSecondParent() {
        return secondParent;
    }

    public void setSecondParent(String secondParent) {
        this.secondParent = secondParent;
    }

    private String secondParent;

    public Info() {
        stagedForAddition = new TreeMap<>();
        stagedForRemoval = new TreeMap<>();
        branches = new TreeMap<>();
        curBranch = "master";
        secondParent = null;
    }

    public TreeMap<String, String> getStagedForAddition() {
        return stagedForAddition;
    }

    public TreeMap<String, String> getStagedForRemoval() {
        return stagedForRemoval;
    }

    public TreeMap<String, String> getBranches() {
        return branches;
    }

    public String getCurBranch() {
        return curBranch;
    }

    public void setCurBranch(String curBranch) {
        this.curBranch = curBranch;
    }

    public boolean stagedForAdditionIsEmpty() {
        return stagedForAddition.isEmpty();
    }

    public boolean stagedForRemovalIsEmpty() {
        return stagedForRemoval.isEmpty();
    }

    public boolean stagedIsEmpty() {
        return stagedForAdditionIsEmpty() && stagedForRemovalIsEmpty();
    }

    public void writeInfo() {
        writeObject(INFO_FILE, this);
    }

    public static Info getInfo() {
        return readObject(INFO_FILE, Info.class);
    }

    public void clear() {
        stagedForAddition.clear();
        stagedForRemoval.clear();
    }

    public void putInStagedForAddition(String name, String id) {
        stagedForAddition.put(name, id);
    }

    public void putInStagedForAddition(String name, File file) {
        stagedForAddition.put(name, getFileId(file));
    }

    public void putInStagedForRemoval(String name, String id) {
        stagedForRemoval.put(name, id);
    }

    public void putInStagedForRemoval(String name, File file) {
        stagedForRemoval.put(name, getFileId(file));
    }

    public void removeFromStagedForAddition(String name) {
        stagedForAddition.remove(name);
    }

    public void removeFromStagedForRemoval(String name) {
        stagedForRemoval.remove(name);
    }

    public void removeFromBranches(String name) {
        branches.remove(name);
    }

    public void putInBranches(String name, String id) {
        branches.put(name, id);
    }

    public void putInBranches(String name, Commit commit) {
        branches.put(name, commit.getId());
    }

    public String getBlobIdInStagedForAddition(String name) {
        return getStagedForAddition().get(name);
    }

    public String getBlobIdInStagedForRemoval(String name) {
        return getStagedForRemoval().get(name);
    }

    public String getCommitFromBranch(String branchName) {
        return branches.get(branchName);
    }


}
