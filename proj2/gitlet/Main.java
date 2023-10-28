package gitlet;

import java.io.IOException;

import static gitlet.Utils.error;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Xinfeng Fu
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.println("Please enter a command.");
                System.exit(0);
            }
            String firstArg = args[0];
            switch (firstArg) {
                case "init":
                    Repository.init(args);
                    break;
                case "add":
                    Repository.add(args);
                    break;
                case "commit":
                    Repository.commit(args);
                    break;
                case "rm":
                    Repository.rm(args);
                    break;
                case "log":
                    Repository.log(args);
                    break;
                case "global-log":
                    Repository.globalLog(args);
                    break;
                case "find":
                    Repository.find(args);
                    break;
                case "status":
                    Repository.status(args);
                    break;
                case "checkout":
                    Repository.checkout(args);
                    break;
                case "branch":
                    Repository.branch(args);
                    break;
                case "rm-branch":
                    Repository.rmBranch(args);
                    break;
                case "reset":
                    Repository.reset(args);
                    break;
                case "merge":
                    Repository.merge(args);
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    System.exit(0);
            }
        } catch (IOException e) {
            throw error("An I/O error occurred: " + e.getMessage());
        }
    }
}

