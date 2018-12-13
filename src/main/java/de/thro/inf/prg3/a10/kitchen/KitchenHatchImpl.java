package de.thro.inf.prg3.a10.kitchen;

import de.thro.inf.prg3.a10.model.Dish;
import de.thro.inf.prg3.a10.model.Order;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class KitchenHatchImpl implements KitchenHatch {
	private int maxMeals;
	private Queue<Order> orders; // orders waiting to be completed
	private Queue<Dish> dishes; // dishes waiting to be served
	private final Object lockd = new Object();
	private final Object locko = new Object();

	public KitchenHatchImpl(int maxMeals, Deque<Order> orders) {
		this.maxMeals = maxMeals;
		this.orders = orders;
		dishes = new LinkedList<>();
	}

	@Override
	public int getMaxDishes() {
		return maxMeals;
	}

	@Override
	public Order dequeueOrder(long timeout) {
		Order o;
		synchronized (locko) {
			o = orders.remove();
		}
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public int getOrderCount() {
		synchronized (locko) {
			return orders.size();
		}
	}

	@Override
	public Dish dequeueDish(long timeout) {
		Dish dish;
		synchronized (lockd) {
			while (dishes.size() == 0) try {
				lockd.wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			dish = dishes.remove();
			lockd.notifyAll();
		}
		try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return dish;
	}

	@Override
	synchronized public void enqueueDish(Dish m) {
		synchronized (lockd) {
			while (dishes.size() == maxMeals) try {
				lockd.wait(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			dishes.add(m);
			lockd.notifyAll();
		}
	}

	@Override
	public int getDishesCount() {
		synchronized (lockd) {
			return dishes.size();
		}
	}
}
