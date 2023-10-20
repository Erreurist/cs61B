package capers;

import java.io.File;
import java.io.IOException;

import static capers.Utils.*;

/** A repository for Capers 
 * @author fuxinfeng
 * The structure of a Capers Repository is as follows:
 *
 * .capers/ -- top level folder for all persistent data in your lab12 folder
 *    - dogs/ -- folder containing all the persistent data for dogs
 *    - story -- file containing the current story
 *
 *
 */
public class CapersRepository {
    /** Current Working Directory. */
    static final File CWD = new File(System.getProperty("user.dir"));

    /** Main metadata folder. */
    static final File CAPERS_FOLDER = join(CWD, ".capers");

    /**
     * Does require filesystem operations to allow for persistence.
     * (creates any necessary folders or files)
     * Remember: recommended structure (you do not have to follow):
     *
     * .capers/ -- top level folder for all persistent data in your lab12 folder
     *    - dogs/ -- folder containing all the persistent data for dogs
     *    - story -- file containing the current story
     */
    public static void setupPersistence() throws IOException {
        File dogs = Utils.join(CAPERS_FOLDER, "dogs");
        File story = Utils.join(CAPERS_FOLDER, "story");
        CAPERS_FOLDER.mkdir();
        dogs.mkdir();
        story.createNewFile();
    }

    /**
     * Appends the first non-command argument in args
     * to a file called `story` in the .capers directory.
     * @param text String of the text to be appended to the story
     */
    public static void writeStory(String text) {
        String tmp = readContentsAsString(join(CAPERS_FOLDER, "story"));
        if (tmp.equals("")) {
            writeContents(join(CAPERS_FOLDER, "story"), text);
        } else {
            writeContents(join(CAPERS_FOLDER, "story"), tmp + '\n' + text);
        }
        tmp = readContentsAsString(join(CAPERS_FOLDER, "story"));
        System.out.println(tmp);
    }

    /**
     * Creates and persistently saves a dog using the first
     * three non-command arguments of args (name, breed, age).
     * Also prints out the dog's information using toString().
     */
    public static void makeDog(String name, String breed, int age) throws IOException {
        Dog dog = new Dog(name, breed, age);
        dog.saveDog();
        System.out.println(dog);
    }

    /**
     * Advances a dog's age persistently and prints out a celebratory message.
     * Also prints out the dog's information using toString().
     * Chooses dog to advance based on the first non-command argument of args.
     * @param name String name of the Dog whose birthday we're celebrating.
     */
    public static void celebrateBirthday(String name) {
        Dog dog = readObject(join(Dog.DOG_FOLDER, name), Dog.class);
        dog.haveBirthday();
        writeObject(join(Dog.DOG_FOLDER, name), dog);
    }
}
