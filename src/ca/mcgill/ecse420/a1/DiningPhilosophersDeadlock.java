package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophersDeadlock {
	
	public static void main(String[] args) {
		int numberOfPhilosophers = 5;
		PhilosopherDeadlock[] philosophersDeadlock = new PhilosopherDeadlock[numberOfPhilosophers];
		ReentrantLock[] chopsticks = new ReentrantLock[numberOfPhilosophers];
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

		for (int i = 0; i < numberOfPhilosophers; i++) {
			// create locks that are not necessarily fair
			chopsticks[i] = new ReentrantLock();
		}

		for (int i = 0; i < numberOfPhilosophers; i++) {
			philosophersDeadlock[i] = new PhilosopherDeadlock(i, numberOfPhilosophers, chopsticks);
		}

		try {
			for (int i = 0; i < numberOfPhilosophers; i++) {
				executor.execute(philosophersDeadlock[i]);
			}

			executor.shutdown();
			executor.awaitTermination(numberOfPhilosophers, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.out.println("exception");
		}
	}

	public static class PhilosopherDeadlock implements Runnable {
		private ReentrantLock rightChopstick;
		private ReentrantLock leftChopstick;
		private int philosopherNumber;
		private int numTimesEaten;

		public PhilosopherDeadlock(int philosopherNumber, int numberOfPhilosophers, ReentrantLock[] chopsticks) {
			this.philosopherNumber = philosopherNumber;
			this.leftChopstick = chopsticks[philosopherNumber];
			this.rightChopstick = chopsticks[(philosopherNumber + 1) % numberOfPhilosophers];
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
				try {
					this.leftChopstick.lock();
					Thread.sleep(100);
					this.rightChopstick.lock();
					eat();
				} catch (Exception e) {
					System.out.println("Exception");
				} finally {
					this.leftChopstick.unlock();
					this.rightChopstick.unlock();
				}
			}
		}
	}
}
