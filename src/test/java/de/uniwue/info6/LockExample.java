package de.uniwue.info6;

/*
 * #%L
 * ************************************************************************
 * ORGANIZATION  :  Institute of Computer Science, University of Wuerzburg
 * PROJECT       :  UEPS - Uebungs-Programm fuer SQL
 * FILENAME      :  LockExample.java
 * ************************************************************************
 * %%
 * Copyright (C) 2014 - 2015 Institute of Computer Science, University of Wuerzburg
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
