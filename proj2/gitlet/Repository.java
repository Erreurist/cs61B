package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

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
    /** the latest commit in the branch you're currently on */
    public static final File HEAD_FILE = join(GITLET_DIR, "HEAD");

    public static final File HEADS_DIR = join(GITLET_DIR, "heads");

    public static final File MASTER_FILE = join(HEADS_DIR, "master");



    public static final File STAGE_FILE = join(GITLET_DIR, "stage");

    public static void init(String[] args) throws IOException {
        validateNumArgs(args, 1);
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
        } else {
            GITLET_DIR.mkdir();
            OBJ_DIR.mkdir();
            HEAD_FILE.createNewFile();
            HEADS_DIR.mkdir();
            MASTER_FILE.createNewFile();
            STAGE_FILE.createNewFile();

            writeContents(HEAD_FILE, "refs/heads/master");

            /** creates the init commit */
            Commit commit = new Commit(null, "initial commit");
            commit.setTimestamp(new Date(0L));
            commit.writeCommit();


            String id = commit.getId();
            writeContents(MASTER_FILE, id);


            Stage stage = new Stage();
            stage.writeStage();
        }
    }

    public static void add(String[] args) {
        validateNumArgs(args, 2);
        if (checkInit()) {
            String name = args[1];
            File file = new File(args[1]); // file to be dealt
            if (!file.exists()) {
                System.out.println("File does not exist.");
                System.exit(0);
            }

            Stage stage = readObject(STAGE_FILE, Stage.class);
            Commit commit = readObject(join(OBJ_DIR, readContentsAsString(MASTER_FILE)), Commit.class);
            HashMap<String, String> fileBlobs = commit.getFileBlobs();

            if (fileBlobs.containsKey(name) && fileBlobs.get(name).equals(sha1(readContentsAsString(file)))) {
                stage.stagedForAddition.remove(name);
                stage.writeStage();
                return;
            }
            stage.stagedForAddition.put(name, sha1(readContentsAsString(file)));
            stage.writeStage();
            Blob blob = new Blob(readContentsAsString(file));
            blob.writeBlob();
        }
    }


    public static void commit(String[] args) throws IOException {
        /** Load stage obj */
        Stage stage = readObject(STAGE_FILE, Stage.class);
        /** If no files have been staged, abort. Print the message No changes added to the commit. */
        if (stage.stagedForAdditionIsEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        if (args.length == 1) {
            System.out.println("Please enter a commit message.");
            return;
        }

        Commit commit = new Commit(readContentsAsString(MASTER_FILE), args[1]);
        Commit parentCommit = readObject(join(OBJ_DIR, readContentsAsString(MASTER_FILE)), Commit.class);
        HashMap<String, String> fileBlobs = parentCommit.getFileBlobs();
        for(String file : fileBlobs.keySet()) {
            commit.addBlob(file, fileBlobs.get(file));
        }

        for (String file : stage.stagedForAddition.keySet()) {
            commit.addBlob(file, stage.stagedForAddition.get(file));
        }

        commit.writeCommit();
        stage.stagedForAddition.clear();
        stage.writeStage();
        writeContents(MASTER_FILE, commit.getId());
    }

    public static void checkout(String[] args) {
        if (args.length >= 5 || args.length == 1) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
        if (checkInit()) {
            if (args.length == 2) {  // checkout [branch name]

            } else if (args.length == 3) {  // checkout -- [file name]
                Commit commit = readObject(join(OBJ_DIR, readContentsAsString(MASTER_FILE)), Commit.class);
                HashMap<String, String> fileBlobs = commit.getFileBlobs();
                if(!fileBlobs.containsKey(args[2])) {
                    System.out.println("No commit with that id exists.");
                    return;
                }
                Blob blob = readObject(join(OBJ_DIR, fileBlobs.get(args[2])), Blob.class);
                writeContents(new File(args[2]), blob.getContent());
            } else if (args.length == 4){  // checkout [commit id] -- [file name]
                Commit commit = readObject(join(OBJ_DIR, args[1]), Commit.class);
                HashMap<String, String> fileBlobs = commit.getFileBlobs();
                if(!fileBlobs.containsKey(args[3])) {
                    System.out.println("No commit with that id exists.");
                    return;
                }
                Blob blob = readObject(join(OBJ_DIR, fileBlobs.get(args[3])), Blob.class);
                writeContents(new File(args[3]), blob.getContent());
            }
        }

    }

    public static void log(String[] args) {
        validateNumArgs(args, 1);
        if (checkInit()) {
            Commit commit = readObject(join(OBJ_DIR, readContentsAsString(MASTER_FILE)), Commit.class);
            System.out.println(commit);
            while(true) {
                if(commit.getParent() == null) {
                    break;
                }
                String parent = commit.getParent();
                commit = readObject(join(OBJ_DIR, parent), Commit.class);
                System.out.println(commit);
            }
        }
    }



    private static void validateNumArgs(String[] args, int n) {
        if (args.length != n) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    private static boolean checkInit() {
        boolean res = GITLET_DIR.exists();
        if (!res) {
            System.out.println("Not in an initialized Gitlet directory.");
        }
        return res;
    }

}
