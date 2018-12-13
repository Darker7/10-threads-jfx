package de.thro.inf.prg3.a10.workers;

import de.thro.inf.prg3.a10.internals.displaying.ProgressReporter;
import de.thro.inf.prg3.a10.kitchen.KitchenHatch;
import org.apache.commons.lang3.RandomUtils;

public class Waiter implements Runnable {
	private String name;
	private ProgressReporter progressReporter;
	private KitchenHatch kitchenHatch;

	public Waiter(String name, KitchenHatch kitchenHatch, ProgressReporter progressReporter) {
		this.name = name;
		this.progressReporter = progressReporter;
		this.kitchenHatch = kitchenHatch;
	}

	@Override
	public void run() {
		while (kitchenHatch.getOrderCount() > 0 || kitchenHatch.getDishesCount() > 0) {
			kitchenHatch.dequeueDish();

			try {
				Thread.sleep(RandomUtils.nextLong(0, 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			progressReporter.updateProgress();
		}
		progressReporter.notifyWaiterLeaving();
	}
}
