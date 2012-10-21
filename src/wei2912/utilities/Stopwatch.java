package wei2912.utilities;

public class Stopwatch implements Runnable {
    static boolean stop         = false;    // Change this to true when to stop.
    double         milliseconds = 0.0;

    @Override
    public void run() {
        while (Stopwatch.stop == false) {    // If not to stop, continue.
            try {
                Thread.sleep(1);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }                                // Sleeps for one millisecond.

            this.milliseconds += 1;          // Adds one to milliseconds.
        }

        System.out.println("Time taken is " + (this.milliseconds / 1000) + " seconds");
    }

    public void stopStopwatch() {
        Stopwatch.stop = true;
        System.out.println("Stopwatch stops now.");
    }

    public void startStopwatch() {
        Stopwatch.stop = false;
        new Thread(new Stopwatch()).start();
        System.out.println("Stopwatch starts now.");
    }
}

// ~ Formatted by Jindent --- http://www.jindent.com


//~ Formatted by Jindent --- http://www.jindent.com
