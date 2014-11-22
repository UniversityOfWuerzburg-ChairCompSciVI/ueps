package de.uniwue.info6;

import java.awt.Point;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockExample {
	final static Lock lock = new ReentrantLock();
	final static Point p = new Point();

	static Runnable r = new Runnable() {
		@Override
		public void run() {
			int x = (int) (Math.random() * 1000), y = x;

			while (true) {
				lock.lock();
				p.x = x;
				p.y = y; // *
				int xc = p.x, yc = p.y; // *
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.unlock();
				if (xc != yc)
					System.out.println("Aha: x=" + xc + ", y=" + yc);

				System.out.println(Thread.currentThread().getId());
			}
		}
	};



	static Runnable k = new Runnable() {
		@Override
		public void run() {
			int x = (int) (Math.random() * 1000), y = x;

			while (true) {
				lock.lock();
				p.x = x;
				p.y = y; // *
				int xc = p.x, yc = p.y; // *
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lock.unlock();
				if (xc != yc)
					System.out.println("Aha: x=" + xc + ", y=" + yc);

				System.out.println(Thread.currentThread().getId());
			}
		}
	};

	public static void main(String... args) {
		new Thread(r).start();
		new Thread(k).start();
	}
}
