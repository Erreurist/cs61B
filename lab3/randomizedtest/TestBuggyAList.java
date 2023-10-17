package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {


  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        AListNoResizing<Integer> A = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();
        A.addLast(4);
        B.addLast(4);
        A.addLast(5);
        B.addLast(5);
        A.addLast(6);
        B.addLast(6);
        assertEquals(A.size(), B.size());
        assertEquals(A.removeLast(), B.removeLast());
        assertEquals(A.removeLast(), B.removeLast());
        assertEquals(A.removeLast(), B.removeLast());
    }

    @Test
    public void randomizeTest() {
        AListNoResizing<Integer> correct = new AListNoResizing<>();
        BuggyAList<Integer> broken = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                // size
                int size = correct.size();
                int size2 = broken.size();
                System.out.println("size: " + size);
                assertEquals(size, size2);
            } else if (operationNumber == 2) {
                // getLast
                if (correct.size() > 0) {
                    int last = correct.getLast();
                    int last2 = broken.getLast();
                    System.out.println("getLast: " + last);
                    assertEquals(last, last2);
                } else {
                    System.out.println("Oops! can't getLast");
                }
            } else if (operationNumber == 3) {
                // removeLast
                if (correct.size() > 0) {
                    int last = correct.removeLast();
                    int last2 = broken.removeLast();
                    System.out.println("removeLast: " + last);
                    assertEquals(last, last2);
                } else {
                    System.out.println("Oops! can't removeLast");
                }
            }
        }
    }
}

