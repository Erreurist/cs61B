package gitlet;


import java.io.File;
import java.io.IOException;
import java.util.*;
import static gitlet.Blob.getFileId;
import static gitlet.Blob.readBlobContentById;
import static gitlet.Utils.*;

/** Represents a gitlet repository.
 *  It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Xinfeng Fu
 */

public class Repository {
    /**
     * add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    public static final File OBJ_DIR = join(GITLET_DIR, "objects");

    public static final File COMMIT_DIR = join(OBJ_DIR, "commits");

    public static final File BLOB_DIR = join(OBJ_DIR, "blobs");

    public static final File INFO_FILE = join(GITLET_DIR, "info");

    /** java gitlet.Main init */
    public static void init(String[] args) throws IOException {
        validateNumArgs(args, 1);
        String msg = "A Gitlet version-control system already exists in the current directory.";
        if (GITLET_DIR.exists()) {
            System.out.println(msg);
            System.exit(0);
        }
        GITLET_DIR.mkdir();
        OBJ_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BLOB_DIR.mkdir();
        INFO_FILE.createNewFile();
        /** creates the init commit */
        Commit commit = new Commit(null, "initial commit");
        Info info = new Info();
        commit.setTimestamp(new Date(0L));
        info.putInBranches("master", commit);
        info.setCurBranch("master");
        commit.writeCommit();
        info.writeInfo();
    }

    /** java gitlet.Main add [file name] */
    public static void add(String[] args) {
        validateNumArgs(args, 2);
        checkInit();
        String name = args[1];
        File file = new File(name);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        Info info = Info.getInfo();
        Commit commit = Commit.readCurCommit(info);

        if (commit.getBlobId(name) != null && commit.getBlobId(name).equals(getFileId(file))) {
            info.removeFromStagedForAddition(name);
            info.removeFromStagedForRemoval(name);
        } else {
            info.putInStagedForAddition(name, file);
            Blob blob = new Blob(file);
            blob.writeBlob();
        }
        info.writeInfo();
    }

    /** java gitlet.Main commit [message] */
    public static void commit(String[] args) throws IOException {
        validateNumArgs(args, 2);
        Info info = Info.getInfo();
        String msg = args[1];
        if (info.stagedIsEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (msg.equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        checkInit();
        Commit parentCommit = Commit.readCurCommit(info);
        Commit commit = new Commit(parentCommit.getId(), msg);
        
        for (String name : parentCommit.getFileBlobs().keySet()) {
            commit.addBlob(name, parentCommit.getBlobId(name));
        }

        for (String name : info.getStagedForAddition().keySet()) {
            commit.addBlob(name, info.getBlobIdInStagedForAddition(name));
        }

        for (String name : info.getStagedForRemoval().keySet()) {
            commit.rmBlob(name, info.getBlobIdInStagedForRemoval(name));
        }
        
        info.clear();
        info.putInBranches(info.getCurBranch(), commit);
        if (info.getSecondParent() != null) {
            commit.setSecondParent(info.getSecondParent());
            info.setSecondParent(null);
        }
        commit.writeCommit();
        info.writeInfo();
    }


    public static void checkout(String[] args) {
        if (args.length >= 5 || args.length == 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (args.length == 3) {  // checkout -- [file name]
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
        } else if (args.length == 4) {  // checkout [commit id] -- [file name]
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
        }
        checkInit();
        if (args.length == 2) {  // checkout [branch name]
            checkout3(args);
        } else if (args.length == 3) {  // checkout -- [file name]
            checkout1(args);
        } else if (args.length == 4) {  // checkout [commit id] -- [file name]
            checkout2(args);
        }
    }

    /** java gitlet.Main checkout -- [file name] */
    private static void checkout1(String[] args) {
        Info info = Info.getInfo();
        Commit commit = Commit.readCurCommit(info);
        String name = args[2];
        if (commit.getBlobId(name) == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        writeContents(new File(args[2]), readBlobContentById(commit.getBlobId(name)));
    }

    /** java gitlet.Main checkout [commit id] -- [file name] */
    private static void checkout2(String[] args) {
        String id = args[1];
        if (id.length() < 40) {
            List<String> names = plainFilenamesIn(COMMIT_DIR);
            assert names != null;
            for (String name : names) {
                if (id.equals(name.substring(0, id.length()))) {
                    id = name;
                }
            }

        }
        if (id.length() < 40) {
            System.out.println("No commit with that id exists.");
            return;
        }



        List<String> names = plainFilenamesIn(COMMIT_DIR);
        boolean flag = false;
        assert names != null;
        for (String name : names) {
            if (id.equals(name)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            System.out.println("No commit with that id exists.");
            return;
        }
        Commit commit = readObject(join(COMMIT_DIR, id), Commit.class);
        HashMap<String, String> fileBlobs = commit.getFileBlobs();
        if (!fileBlobs.containsKey(args[3])) {
            System.out.println("File does not exist in that commit.");
            return;
        }
        Blob blob = readObject(join(BLOB_DIR, fileBlobs.get(args[3])), Blob.class);
        writeContents(new File(args[3]), blob.getContent());
    }

    /** java gitlet.Main checkout [branch name] */
    private static void checkout3(String[] args) {
        Info info = Info.getInfo();
        String curBranch = info.getCurBranch();
        if (!info.getBranches().containsKey(args[1])) {
            System.out.println("No such branch exists.");
            return;
        }
        if (curBranch.equals(args[1])) {
            System.out.println("No need to checkout the current branch.");
            return;
        }

        Commit checkedCommit = Commit.readCommit(info.getBranches().get(args[1]));
        Commit commit = Commit.readCurCommit(info);

        List<String> names = plainFilenamesIn(CWD);
        String mg = "There is an untracked file in the way; delete it, or add and commit it first.";
        if (names != null) {
            for (String name : names) {
                if (commit.getBlobId(name) == null && checkedCommit.getBlobId(name) != null) {
                    if (!checkedCommit.getBlobId(name).equals(getFileId(name))) {
                        System.out.println(mg);
                        System.exit(0);
                    }
                }
            }
            for (String name : names) {
                join(CWD, name).delete();
            }
            for (Map.Entry<String, String> entry : checkedCommit.getFileBlobs().entrySet()) {
                String fileName = entry.getKey();
                String blobId = entry.getValue();
                Blob blob = readObject(join(BLOB_DIR, blobId), Blob.class);
                writeContents(join(CWD, fileName), blob.getContent());
            }
            info.clear();
            info.setCurBranch(args[1]);
            info.writeInfo();
        } else {
            for (Map.Entry<String, String> entry : checkedCommit.getFileBlobs().entrySet()) {
                String fileName = entry.getKey();
                String blobId = entry.getValue();
                Blob blob = readObject(join(BLOB_DIR, blobId), Blob.class);
                writeContents(join(CWD, fileName), blob.getContent());
            }
            info.clear();
            info.setCurBranch(args[1]);
            info.writeInfo();
        }
    }

    public static void log(String[] args) {
        validateNumArgs(args, 1);
        checkInit();
        Info info = Info.getInfo();
        Commit commit = Commit.readCurCommit(info);
        System.out.println(commit);
        while (commit.getParent() != null) {
            String parent = commit.getParent();
            commit = Commit.readCommit(parent);
            System.out.println(commit);
        }
    }

    public static void rm(String[] args) {
        validateNumArgs(args, 2);
        checkInit();
        String name = args[1];
        File file = new File(name);
        Info info = Info.getInfo();
        Commit commit = Commit.readCurCommit(info);

        if (commit.getBlobId(name) == null && info.getBlobIdInStagedForAddition(name) == null) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        if (info.getBlobIdInStagedForAddition(name) != null) {
            info.removeFromStagedForAddition(name);
        }
        if (commit.getBlobId(name) != null) {
            info.putInStagedForRemoval(name, commit.getBlobId(name));
            if (new File(name).exists()) {
                join(CWD, name).delete();
            }
        }
        info.writeInfo();
    }

    public static void globalLog(String[] args) {
        validateNumArgs(args, 1);
        checkInit();
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        assert commits != null;
        for (String name : commits) {
            Commit commit = readObject(join(COMMIT_DIR, name), Commit.class);
            System.out.println(commit);
        }
    }

    public static void find(String[] args) {
        validateNumArgs(args, 2);
        checkInit();
        Set<String> results = new HashSet<>();
        List<String> commits = plainFilenamesIn(COMMIT_DIR);
        assert commits != null;
        for (String name : commits) {
            Commit commit = readObject(join(COMMIT_DIR, name), Commit.class);
            if (commit.getMsg().equals(args[1])) {
                results.add(commit.getId());
            }
        }
        if (results.isEmpty()) {
            System.out.println("Found no commit with that message.");
        } else {
            for (String result : results) {
                System.out.println(result);
            }
        }
    }

    public static void status(String[] args) {
        validateNumArgs(args, 1);
        checkInit();
        Info info = Info.getInfo();
        String branch = "=== Branches ===\n";
        branch += "*" + info.getCurBranch() + "\n";
        for (String name : info.getBranches().keySet()) {
            if (!name.equals(info.getCurBranch())) {
                branch += name + "\n";
            }
        }
        branch += "\n";
        String stagedFiles = "=== Staged Files ===\n";
        for (String name : info.getStagedForAddition().keySet()) {
            stagedFiles += name + "\n";
        }
        stagedFiles += "\n";
        String removedFiles = "=== Removed Files ===\n";
        for (String name : info.getStagedForRemoval().keySet()) {
            removedFiles += name + "\n";
        }
        removedFiles += "\n";
        String modifications = "=== Modifications Not Staged For Commit ===\n";
        modifications += "\n";
        String untracked = "=== Untracked Files ===\n";
        untracked += "\n";
        System.out.println(branch + stagedFiles + removedFiles + modifications + untracked);
    }

    public static void branch(String[] args) {
        validateNumArgs(args, 2);
        checkInit();
        Info info = Info.getInfo();
        String branchName = args[1];
        if (info.getBranches().containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            return;
        }
        Commit commit = Commit.readCurCommit(info);
        info.putInBranches(branchName, commit.getId());
        info.writeInfo();
    }

    public static void rmBranch(String[] args) {
        validateNumArgs(args, 2);
        checkInit();
        Info info = Info.getInfo();
        String branchName = args[1];
        if (!info.getBranches().containsKey(branchName)) {
            System.out.println("A branch with that name does not exist.");
            return;
        }
        if (info.getCurBranch().equals(branchName)) {
            System.out.println("Cannot remove the current branch.");
            return;
        }
        info.removeFromBranches(branchName);
        info.writeInfo();
    }

    public static void reset(String[] args) {
        validateNumArgs(args, 2);
        checkInit();
        String id = args[1];
        if (id.length() < 40) {
            List<String> names = plainFilenamesIn(COMMIT_DIR);
            assert names != null;
            for (String name : names) {
                if (id.equals(name.substring(0, id.length()))) {
                    id = name;
                }
            }

        }
        if (id.length() < 40) {
            System.out.println("No commit with that id exists.");
            return;
        }

        List<String> commitIds = plainFilenamesIn(COMMIT_DIR);
        boolean flag = false;
        assert commitIds != null;
        for (String commitId : commitIds) {
            if (id.equals(commitId)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            System.out.println("No commit with that id exists.");
            return;
        }

        Info info = Info.getInfo();

        Commit checkedCommit = Commit.readCommit(id);
        Commit commit = Commit.readCurCommit(info);

        /**  current files in the dir */
        List<String> names = plainFilenamesIn(CWD);
        String mg = "There is an untracked file in the way; delete it, or add and commit it first.";
        if (names != null) {
            for (String name : names) {
                if (commit.getBlobId(name) == null && checkedCommit.getBlobId(name) != null) {
                    if (!checkedCommit.getBlobId(name).equals(getFileId(name))) {
                        System.out.println(mg);
                        System.exit(0);
                    }
                }
            }
            for (String name : names) {
                join(CWD, name).delete();
            }
            for (Map.Entry<String, String> entry : checkedCommit.getFileBlobs().entrySet()) {
                String fileName = entry.getKey();
                String blobId = entry.getValue();
                Blob blob = readObject(join(BLOB_DIR, blobId), Blob.class);
                writeContents(join(CWD, fileName), blob.getContent());
            }
            info.clear();
            info.putInBranches(info.getCurBranch(), id);
            info.writeInfo();
        } else {
            for (Map.Entry<String, String> entry : checkedCommit.getFileBlobs().entrySet()) {
                String fileName = entry.getKey();
                String blobId = entry.getValue();
                Blob blob = readObject(join(BLOB_DIR, blobId), Blob.class);
                writeContents(join(CWD, fileName), blob.getContent());
            }
            info.clear();
            info.putInBranches(info.getCurBranch(), id);
            info.writeInfo();
        }
    }

    public static void merge(String[] args) throws IOException {
        mergeErrorHandle(args);
        Info info = Info.getInfo();
        String mergedBranchName = args[1];
        Commit mergeCommit = Commit.readCommit(info.getCommitFromBranch(mergedBranchName));
        Commit curCommit = Commit.readCurCommit(info);
        Commit split = getSplitCommit(mergeCommit, curCommit);
        for (String name : split.getFileBlobs().keySet()) {
            String splitBlob = split.getBlobId(name);
            String curBlob = curCommit.getBlobId(name);
            String mergeBlob = mergeCommit.getBlobId(name);
            if (splitBlob.equals(curBlob) && splitBlob.equals(mergeBlob)) {
                continue;
            } else if (splitBlob.equals(curBlob) && !splitBlob.equals(mergeBlob)) {
                if (mergeBlob == null) {
                    info.putInStagedForRemoval(name, getFileId(name));
                    new File(name).delete();
                } else {
                    info.putInStagedForAddition(name, mergeBlob);
                    writeContents(new File(name), readBlobContentById(mergeBlob));
                }
            } else if (!splitBlob.equals(curBlob) && splitBlob.equals(mergeBlob)) {
                continue;
            } else {
                if (curBlob != null && mergeBlob != null && !curBlob.equals(mergeBlob)) {
                    System.out.println("Encountered a merge conflict.");
                    String newContent = handleConflictResult(curCommit, mergeCommit, name);
                    writeContents(new File(name), newContent);
                    info.putInStagedForAddition(name, getFileId(name));
                    new Blob(new File(name)).writeBlob();
                }
                if (curBlob == null && mergeBlob != null) {
                    System.out.println("Encountered a merge conflict.");
                    String newContent = handleConflictResult(curCommit, mergeCommit, name);
                    writeContents(new File(name), newContent);
                    info.putInStagedForAddition(name, getFileId(name));
                    new Blob(new File(name)).writeBlob();
                } if (curBlob != null && mergeBlob == null) {
                    System.out.println("Encountered a merge conflict.");
                    String newContent = handleConflictResult(curCommit, mergeCommit, name);
                    writeContents(new File(name), newContent);
                    info.putInStagedForAddition(name, getFileId(name));
                    new Blob(new File(name)).writeBlob();
                }
            }
        }
        Set<String> fileNames = new HashSet<>();
        fileNames.addAll(mergeCommit.getFileBlobs().keySet());
        fileNames.addAll(curCommit.getFileBlobs().keySet());
        for (String name : split.getFileBlobs().keySet()) {
            fileNames.remove(name);
        }
        for (String name : fileNames) {
            String curBlob = curCommit.getBlobId(name);
            String mergeBlob = mergeCommit.getBlobId(name);
            if (curBlob == null && mergeBlob == null) {
                continue;
            } else if (curBlob == null && mergeBlob != null) {
                info.putInStagedForAddition(name, mergeBlob);
                writeContents(new File(name), readBlobContentById(mergeBlob));
            } else if (curBlob != null && mergeBlob == null) {
                continue;
            } else if (curBlob.equals(mergeBlob)) {
                continue;
            } else if (!curBlob.equals(mergeBlob)) {
                System.out.println("Encountered a merge conflict.");
                String newContent = handleConflictResult(curCommit, mergeCommit, name);
                writeContents(new File(name), newContent);
                info.putInStagedForAddition(name, getFileId(name));
                new Blob(new File(name)).writeBlob();
            }
        }
        info.setSecondParent(info.getCommitFromBranch(mergedBranchName));
        info.writeInfo();
        String[] commitArgs = new String[2];
        commitArgs[0] = "commit";
        commitArgs[1] = "Merged " + mergedBranchName + " into " + info.getCurBranch() +  ".";
        commit(commitArgs);
    }


    private static void mergeErrorHandle(String[] args) {
        validateNumArgs(args, 2);
        checkInit();
        Info info = Info.getInfo();
        String mergedBranchName = args[1];
        List<String> names = plainFilenamesIn(CWD);
        Commit curCommit = Commit.readCurCommit(info);
        if (!info.stagedIsEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (info.getCommitFromBranch(mergedBranchName) == null) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        Commit mergeCommit = Commit.readCommit(info.getCommitFromBranch(mergedBranchName));
        if (info.getCurBranch().equals(mergedBranchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit commit = Commit.readCurCommit(info);
        String mg = "There is an untracked file in the way; delete it, or add and commit it first.";
        if (names != null) {
            for (String name : names) {
                if (commit.getBlobId(name) == null && mergeCommit.getBlobId(name) != null) {
                    if (!mergeCommit.getBlobId(name).equals(getFileId(name))) {
                        System.out.println(mg);
                        System.exit(0);
                    }
                }
            }
        }
        Commit split = getSplitCommit(mergeCommit, curCommit);
        assert split != null;
        if (split.getId().equals(mergeCommit.getId())) {
            System.out.println("Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (split.getId().equals(curCommit.getId())) {
            String[] newArgs = new String[2];
            newArgs[0] = "checkout";
            newArgs[1] = args[1];
            checkout(newArgs);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
    }

    private static Commit getSplitCommit(Commit mergeCommit, Commit curCommit) {
        HashMap<Integer, String> gToC = new HashMap<>();
        HashMap<String, Integer> cToG = new HashMap<>();
        Commit split = null;
        int splitNum = 0, mergeNum = 0, curNum = 0;
        List<String> names = plainFilenamesIn(COMMIT_DIR);
        assert names != null;
        int i = 0;
        for (String name : names) {
            gToC.put(i, name);
            cToG.put(name, i);
            if (mergeCommit.getId().equals(name)) {
                mergeNum = i;
            }
            if (curCommit.getId().equals(name)) {
                curNum = i;
            }
            i++;
        }
        int tot = i;
        DagBfs.Graph graph = new DagBfs.Graph(tot);
        for (String name : names) {
            Commit tmp = Commit.readCommit(name);
            if (tmp.getParent() != null) {
                graph.addEdge(cToG.get(tmp.getId()), cToG.get(tmp.getParent()));
            }
            if (tmp.getSecondParent() != null) {
                graph.addEdge(cToG.get(tmp.getId()), cToG.get(tmp.getSecondParent()));
            }
        }
        boolean[] visited = graph.bfsTraversal(mergeNum);
        splitNum = graph.findSplit(curNum, visited);
        String splitId = gToC.get(splitNum);
        split = Commit.readCommit(splitId);
        return split;
    }

    private static String handleConflictResult(Commit curCommit, Commit mergeCommit, String name) {
        String res = "<<<<<<< HEAD\n";
        if (curCommit.getBlobId(name) != null) {
            res += readBlobContentById(curCommit.getBlobId(name)) + "\n";
        }
        res += "=======\n";
        if (mergeCommit.getBlobId(name) != null) {
            res += readBlobContentById(mergeCommit.getBlobId(name)) + "\n";
        }
        res += ">>>>>>>";
        return res;
    }


    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static void checkInit() {
        if (!GITLET_DIR.exists()) {
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

}
