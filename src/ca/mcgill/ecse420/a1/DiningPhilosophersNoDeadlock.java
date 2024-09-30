package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersNoDeadlock {
    static int numberOfPhilosophers = 5;
    static int maxEaters = numberOfPhilosophers - 1;

    public static void main(String[] args) {
		PhilosopherNoDeadlock[] philosophersNoDeadlocks = new PhilosopherNoDeadlock[numberOfPhilosophers];
		ReentrantLock[] chopsticks = new ReentrantLock[numberOfPhilosophers];
        ReentrantLock globalLock = new ReentrantLock();
        int[] eatingCount = {0};
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

		for (int i = 0; i < numberOfPhilosophers; i++) {
			// create locks that are not necessarily fair
			chopsticks[i] = new ReentrantLock();
		}

		for (int i = 0; i < numberOfPhilosophers; i++) {
			philosophersNoDeadlocks[i] = new PhilosopherNoDeadlock(i, numberOfPhilosophers, chopsticks, globalLock, eatingCount);
		}

		try {
			for (int i = 0; i < numberOfPhilosophers; i++) {
				executor.execute(philosophersNoDeadlocks[i]);
			}

			executor.shutdown();
			executor.awaitTermination(numberOfPhilosophers, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.out.println("exception");
		}
	}

	public static class PhilosopherNoDeadlock implements Runnable {
		private int philosopherNumber;
		private ReentrantLock rightChopstick;
		private ReentrantLock leftChopstick;
        private ReentrantLock globalLock;
        private int[] eatingCount;
		private int numTimesEaten;

		public PhilosopherNoDeadlock(int philosopherNumber, int numberOfPhilosophers, ReentrantLock[] chopsticks, ReentrantLock globalLock, int[] eatingCount) {
			this.philosopherNumber = philosopherNumber;
			this.leftChopstick = chopsticks[philosopherNumber];
			this.rightChopstick = chopsticks[(philosopherNumber + 1) % numberOfPhilosophers];
            this.globalLock = globalLock;
            this.eatingCount = eatingCount;
			this.numTimesEaten = 0;
		}

		public void eat() {
			try {
				this.numTimesEaten += 1;
				System.out.println("Philosopher " + this.philosopherNumber + " has eaten " + this.numTimesEaten + "\n");
				Thread.sleep(100);
			} catch (Exception e) {
                e.printStackTrace(System.out);
            }
		}

		public int get() {
			return this.numTimesEaten;
		}

		@Override
		public void run() {
			while (true) {
                globalLock.lock();
                try {
                    if (eatingCount[0] < maxEaters) {
                        eatingCount[0]++;
                    } else {
                        continue;
                    }
                } finally {
                    globalLock.unlock();
                }

				boolean leftLocked = false;
				boolean rightLocked = false;
                
                try {
                    leftLocked = leftChopstick.tryLock();
                    Thread.sleep(100);
                    rightLocked = rightChopstick.tryLock();

                    if (leftLocked && rightLocked) {
                        eat();
                    }
                } catch (Exception e) {
                    System.out.println("Exception");
                } finally {
                    if (rightLocked) {
                        rightChopstick.unlock();
                    }
                    if (leftLocked) {
                        leftChopstick.unlock();
                    }

                    globalLock.lock();
                    try {
                        eatingCount[0]--;
                    } finally {
                        globalLock.unlock();
                    }
                }
			}
		}
	}
}
