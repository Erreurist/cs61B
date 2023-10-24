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
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        try {
            switch(firstArg) {
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
                    break;
                case "log":
                    Repository.log(args);
                    break;
                case "global-log":
                    break;
                case "find":
                    break;
                case "status":
                    break;
                case "checkout":
                    Repository.checkout(args);
                    break;
                case "branch":
                    break;
                case "rm-branch":
                    break;
                case "reset":
                    break;
                case "merge":
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    System.exit(0);

            }
        }  catch (IOException excp) {
            throw error("Error");
        }
    }
}

