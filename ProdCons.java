/**
 * 
 */
package com.src.prodcon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Emad
 *
 */
public class ProdCons {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub

		ModifyResource resource = new ModifyResource();
		Thread producer1 = new Producer(resource);
		Thread producer2 = new Producer(resource);
		Thread producer3 = new Producer(resource);
		Thread consumer1 = new Consumer(resource);
		Thread consumer2 = new Consumer(resource);
		producer1.start();
		producer2.start();
		producer3.start();
		consumer1.start();
		consumer2.start();
		producer1.join();
		producer2.join();
		producer3.join();
		consumer1.join();
		consumer2.join();

	}

}

class Consumer extends Thread {

	FileReader reader = null;
	BufferedReader bufferedReader = null;
	ModifyResource resource = null;

	public Consumer(ModifyResource resource) {
		// TODO Auto-generated constructor stub
		this.resource = resource;
	}

	public void run() {
		try {
			reader = new FileReader("C:\\ProdCon\\prod_con.txt");
			bufferedReader = new BufferedReader(reader);
			String line = null;
			String[] transaction = null;
			int slotNumber = 0;
			int decrement = 0;
			int value = 0;
			int counter = 0;
			while ((line = bufferedReader.readLine()) != null) {
				transaction = line.split("\\s+");
				if (transaction[0].equalsIgnoreCase("c")) {
					slotNumber = Integer.parseInt(transaction[1]) - 1;
					decrement = Integer.parseInt(transaction[2]);
					while (value == 0 && counter == 0) {
						value = resource.get(slotNumber, decrement);
					}
					while (value - decrement > 0) {
						counter++;
						value = resource.get(slotNumber, decrement);
						break;

					}

				} else {
					line = null;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

class Producer extends Thread {

	FileReader reader = null;
	BufferedReader bufferedReader = null;
	ModifyResource resource = null;

	public Producer(ModifyResource resource) {
		// TODO Auto-generated constructor stub
		this.resource = resource;
	}

	public void run() {

		try {
			reader = new FileReader("C:\\ProdCon\\prod_con.txt");
			bufferedReader = new BufferedReader(reader);
			String line = null;
			String[] transaction = null;
			int slotNumber = 0;
			int increment = 0;
			int value = 0;

			while ((line = bufferedReader.readLine()) != null) {
				transaction = line.split("\\s+");

				if (transaction[0].equalsIgnoreCase("p")) {
					slotNumber = Integer.parseInt(transaction[1]) - 1;
					increment = Integer.parseInt(transaction[2]);
					while (value + increment <= 100) {
						value = resource.put(slotNumber, increment);
						break;
					}

				} else {
					line = null;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

class ModifyResource {

	int[] array = { 0, 0, 0, 0, 0 };

	public synchronized int put(int slotNumber, int increment) {

		if (slotNumber > 4) {
			try {
				throw new Exception(
						"The array index specified by the input file is not correct. It should be in the range of 0-4. The given index is "
								+ slotNumber + ". The transaction is supposed to be performed by the producer thread "
								+ Thread.currentThread().getId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}

		while (array[slotNumber] == 100) {
			try {
				wait(4000);
				System.out.println("Producer waits.......");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (slotNumber <= 4) {

			array[slotNumber] = array[slotNumber] + increment;
			System.out.println("Producer adds " + increment + " at position " + (slotNumber + 1) + " with id: "
					+ Thread.currentThread().getId() + ". Current value --> " + array[slotNumber]);
			System.out.print("Current status of array is: [ ");
			for (int i = 0; i < 5; i++) {
				System.out.print(" " + array[i] + " ");
			}
			System.out.println(" ]");
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			notifyAll();

		}

		return array[slotNumber];

	}

	public synchronized int get(int slotNumber, int decrement) {

		if (slotNumber > 4) {
			try {
				throw new Exception(
						"The array index specified by the input file is not correct. It should be in the range of 0-4. The given index is "
								+ slotNumber + ". The transaction is supposed to be performed by the consumer thread "
								+ Thread.currentThread().getId());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
			}
		}

		while (array[slotNumber] == 0) {
			try {
				wait(4000);
				System.out.println("Consumer waits......");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (slotNumber <= 4) {

			if (array[slotNumber] >= 0 && array[slotNumber]>=decrement) {
				array[slotNumber] = array[slotNumber] - decrement;
				System.out.println("Consumer decreases " + decrement + " at position " + (slotNumber + 1) + " with id: "
						+ Thread.currentThread().getId() + ". Current value --> " + array[slotNumber]);
				System.out.print("Current status of array is: [ ");
				for (int i = 0; i < 5; i++) {
					System.out.print(" " + array[i] + " ");
				}
				System.out.println(" ]");
			}

			try {
				Thread.sleep(4000);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			notifyAll();

		}

		return array[slotNumber];

	}
}