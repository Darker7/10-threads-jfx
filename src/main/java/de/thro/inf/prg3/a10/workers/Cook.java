package de.thro.inf.prg3.a10.workers;

import de.thro.inf.prg3.a10.internals.displaying.ProgressReporter;
import de.thro.inf.prg3.a10.kitchen.KitchenHatch;
import de.thro.inf.prg3.a10.model.Dish;
import de.thro.inf.prg3.a10.model.Order;

public class Cook implements Runnable {
	private String name;
	private ProgressReporter progressReporter;
	private KitchenHatch kitchenHatch;

	public Cook(String name, KitchenHatch kitchenHatch, ProgressReporter progressReporter) {
		this.name = name;
		this.kitchenHatch = kitchenHatch;
		this.progressReporter = progressReporter;
	}

	@Override
	public void run() {
		while (kitchenHatch.getOrderCount() > 0) {
			Order order = kitchenHatch.dequeueOrder();

			Dish dish = new Dish(order.getMealName());
			try {
				Thread.sleep(dish.getCookingTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			kitchenHatch.enqueueDish(dish);
			progressReporter.updateProgress();
		}
		progressReporter.notifyCookLeaving();
	}
}
