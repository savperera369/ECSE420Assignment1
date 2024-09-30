package ca.mcgill.ecse420.a1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class DiningPhilosophers {
	
	public static void main(String[] args) {
		int numberOfPhilosophers = 50;

		PhilosopherDeadlock[] philosophersDeadlock = new PhilosopherDeadlock[numberOfPhilosophers];
		PhilosopherNoDeadlock[] philosophersNoDeadlocks = new PhilosopherNoDeadlock[numberOfPhilosophers];
		PhilosopherNoStarvation[] philosophersNoStarvation = new PhilosopherNoStarvation[numberOfPhilosophers];

		ReentrantLock[] chopsticks = new ReentrantLock[numberOfPhilosophers];
		ReentrantLock[] fairChopsticks = new ReentrantLock[numberOfPhilosophers];
		ExecutorService executor = Executors.newFixedThreadPool(numberOfPhilosophers);

		for (int i = 0; i < numberOfPhilosophers; i++) {
			// create locks that are not necessarily fair
			chopsticks[i] = new ReentrantLock();
			// create fair locks
			fairChopsticks[i] = new ReentrantLock(true);
		}

		for (int i = 0; i < numberOfPhilosophers; i++) {
			philosophersDeadlock[i] = new PhilosopherDeadlock(i, numberOfPhilosophers, chopsticks);
			philosophersNoDeadlocks[i] = new PhilosopherNoDeadlock(i, numberOfPhilosophers, chopsticks);
			philosophersNoStarvation[i] = new PhilosopherNoStarvation(i, numberOfPhilosophers, fairChopsticks);
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

	public static class PhilosopherDeadlock implements Runnable {
		private ReentrantLock rightChopstick;
		private ReentrantLock leftChopstick;
		private int numTimesEaten;

		public PhilosopherDeadlock(int philosopherNumber, int numberOfPhilosophers, ReentrantLock[] chopsticks) {
			this.leftChopstick = chopsticks[philosopherNumber];
			this.rightChopstick = chopsticks[(philosopherNumber + 1) % numberOfPhilosophers];
			this.numTimesEaten = 0;
		}

		public void eat() {
			try {
				this.numTimesEaten += 1;
				Thread.sleep(1000);
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
				this.leftChopstick.lock();
				this.rightChopstick.lock();
				eat();
				this.leftChopstick.unlock();
				this.rightChopstick.unlock();
			}
		}
	}

	public static class PhilosopherNoDeadlock implements Runnable {
		private int philosopherNumber;
		private ReentrantLock rightChopstick;
		private ReentrantLock leftChopstick;
		private int numTimesEaten;

		public PhilosopherNoDeadlock(int philosopherNumber, int numberOfPhilosophers, ReentrantLock[] chopsticks) {
			this.philosopherNumber = philosopherNumber;
			this.leftChopstick = chopsticks[philosopherNumber];
			this.rightChopstick = chopsticks[(philosopherNumber + 1) % numberOfPhilosophers];
			this.numTimesEaten = 0;
		}

		public void eat() {
			try {
				this.numTimesEaten += 1;
				System.out.println("Philosopher " + this.philosopherNumber + " has eaten " + this.numTimesEaten + "\n");
				Thread.sleep(1000);
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
				boolean isLeftLocked = false;
				boolean isRightLocked = false;

				try {
					isLeftLocked = this.leftChopstick.tryLock();
					if (isLeftLocked) {
						isRightLocked = this.rightChopstick.tryLock();
						if (isRightLocked) {
							eat();
						}
					}
				} finally {
					if (isRightLocked) {
						this.rightChopstick.unlock();
					}
					if (isLeftLocked) {
						this.leftChopstick.unlock();
					}
				}
			}
		}
	}

	public static class PhilosopherNoStarvation implements Runnable {
		private int philosopherNumber;
		private ReentrantLock rightChopstick;
		private ReentrantLock leftChopstick;
		private int numTimesEaten;

		public PhilosopherNoStarvation(int philosopherNumber, int numberOfPhilosophers, ReentrantLock[] chopsticks) {
			this.philosopherNumber = philosopherNumber;
			this.leftChopstick = chopsticks[philosopherNumber];
			this.rightChopstick = chopsticks[(philosopherNumber + 1) % numberOfPhilosophers];
			this.numTimesEaten = 0;
		}

		public void eat() {
			try {
				this.numTimesEaten += 1;
				System.out.println("Philosopher " + this.philosopherNumber + " has eaten " + this.numTimesEaten + "\n");
				Thread.sleep(1000);
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
				boolean isLeftLocked = false;
				boolean isRightLocked = false;

				try {
					isLeftLocked = this.leftChopstick.tryLock();
					if (isLeftLocked) {
						isRightLocked = this.rightChopstick.tryLock();
						if (isRightLocked) {
							eat();
						}
					}
				} finally {
					if (isRightLocked) {
						this.rightChopstick.unlock();
					}
					if (isLeftLocked) {
						this.leftChopstick.unlock();
					}
				}
			}
		}
	}
}
