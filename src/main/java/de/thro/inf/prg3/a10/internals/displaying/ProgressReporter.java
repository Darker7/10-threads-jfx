package de.thro.inf.prg3.a10.internals.displaying;

import de.thro.inf.prg3.a10.kitchen.KitchenHatch;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Utility report progress of producers and consumers to the UI
 *
 * @author Peter Kurfer
 */
public final class ProgressReporter {

	private final DoubleProperty orderQueueProgress;
	private final DoubleProperty kitchenHatchProgress;
	private final DoubleProperty waitersBusy;
	private final DoubleProperty cooksBusy;

	private final KitchenHatch kitchenHatch;
	private final AtomicInteger cookCount;
	private final AtomicInteger waiterCount;
	private final int totalOrderCount;
	private final int kitchenHatchCapacity;

	public ProgressReporter(KitchenHatch kitchenHatch, int cookCount, int waiterCount, int totalOrderCount, int kitchenHatchCapacity) {
		this.orderQueueProgress = new SimpleDoubleProperty(1.0);
		this.kitchenHatchProgress = new SimpleDoubleProperty(0.0d);
		this.waitersBusy = new SimpleDoubleProperty(-1.0d);
		this.cooksBusy = new SimpleDoubleProperty(-1.0d);
		this.cookCount = new AtomicInteger(cookCount);
		this.waiterCount = new AtomicInteger(waiterCount);

		this.kitchenHatch = kitchenHatch;
		this.totalOrderCount = totalOrderCount;
		this.kitchenHatchCapacity = kitchenHatchCapacity;
	}

	public double getOrderQueueProgress() {
		return orderQueueProgress.get();
	}

	public DoubleProperty orderQueueProgressProperty() {
		return orderQueueProgress;
	}

	public synchronized void setOrderQueueProgress(double orderQueueProgress) {
		this.orderQueueProgress.set(orderQueueProgress);
	}

	public double getKitchenHatchProgress() {
		return kitchenHatchProgress.get();
	}

	public DoubleProperty kitchenHatchProgressProperty() {
		return kitchenHatchProgress;
	}

	private synchronized void setKitchenHatchProgress(double kitchenHatchProgress) {
		this.kitchenHatchProgress.set(kitchenHatchProgress);
	}

	public double getWaitersBusy() {
		return waitersBusy.get();
	}

	public DoubleProperty waitersBusyProperty() {
		return waitersBusy;
	}

	public double getCooksBusy() {
		return cooksBusy.get();
	}

	public DoubleProperty cooksBusyProperty() {
		return cooksBusy;
	}

	/**
	 * Notifies the reporter that a waiter left
	 * if no waiters are working anymore the busy indicator is removed from the view
	 */
	public void notifyWaiterLeaving() {
		waiterCount.getAndDecrement();
		updateProgress();
	}

	/**
	 * Notifies the reporter that a cook left
	 * if no cooks are working anymore the busy indicator is removed from the view
	 */
	public void notifyCookLeaving() {
		cookCount.getAndDecrement();
		updateProgress();
	}

	/**
	 * Updates the queue depth visualizations of the order queue and kitchen hatch queue
	 */
	public void updateProgress() {
		setKitchenHatchProgress(((double) this.kitchenHatch.getDishesCount()) / this.kitchenHatchCapacity);
		setOrderQueueProgress(((double) this.kitchenHatch.getOrderCount()) / this.totalOrderCount);

		if (waiterCount.get() == 0) {
			Platform.runLater(() -> waitersBusy.set(1.0));
		}

		if (cookCount.get() == 0) {
			Platform.runLater(() -> cooksBusy.set(1.0));
		}
	}
}
