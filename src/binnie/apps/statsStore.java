package binnie.apps;

/**
 * Created with IntelliJ IDEA.
 * User: Binnie
 * Date: 02/04/13
 * Time: 23:11
 * To change this template use File | Settings | File Templates.
 */
public class StatsStore {
    private static int[] successesEver = new int[]{0,0,0};  // Inside two mins, five mins, and at all.
    private static int failuresEver = 0;
    private static int[] successesSinceLastReset = new int[]{0,0,0};  // Inside two mins, five mins, and at all.
    private static int failuresSinceLastReset = 0;

    public static void updateStats(boolean success, long time) {
        if (success) {
            successesEver[2]++;
        }
        else {
            failuresEver++;
        }
    }

    public static int getSuccessesEver() { return successesEver[2]; }

    public static int getFailuresEver() { return failuresEver; }

    public static void setSuccessesEver(int numSuccesses) { successesEver[2] = numSuccesses; }

    public static void setFailuresEver(int numFailures) { failuresEver = numFailures; }

}
