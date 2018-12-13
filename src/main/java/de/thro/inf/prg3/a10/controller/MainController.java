package de.thro.inf.prg3.a10.controller;

import de.thro.inf.prg3.a10.KitchenHatchConstants;
import de.thro.inf.prg3.a10.internals.displaying.ProgressReporter;
import de.thro.inf.prg3.a10.kitchen.KitchenHatch;
import de.thro.inf.prg3.a10.kitchen.KitchenHatchImpl;
import de.thro.inf.prg3.a10.model.Order;
import de.thro.inf.prg3.a10.util.NameGenerator;
import de.thro.inf.prg3.a10.workers.Cook;
import de.thro.inf.prg3.a10.workers.Waiter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import org.apache.commons.lang3.RandomUtils;

import java.net.URL;
import java.util.Deque;
import java.util.LinkedList;
import java.util.ResourceBundle;

import static de.thro.inf.prg3.a10.KitchenHatchConstants.*;

public class MainController implements Initializable {

	private final ProgressReporter progressReporter;
	private final KitchenHatch kitchenHatch;

	@FXML
	private ProgressIndicator waitersBusyIndicator;

	@FXML
	private ProgressIndicator cooksBusyIndicator;

	@FXML
	private ProgressBar kitchenHatchProgress;

	@FXML
	private ProgressBar orderQueueProgress;

	public MainController() {
		//TODO assign an instance of your implementation of the KitchenHatch interface
		NameGenerator m = new NameGenerator();
		Deque<Order> d = new LinkedList<>();
		for (int i = 0; i < ORDER_COUNT; i++) {
			d.add(new Order(m.getRandomDish()));
		}
		this.kitchenHatch = new KitchenHatchImpl(KITCHEN_HATCH_SIZE, d);
		this.progressReporter = new ProgressReporter(kitchenHatch, COOKS_COUNT, WAITERS_COUNT, ORDER_COUNT, KITCHEN_HATCH_SIZE);

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		orderQueueProgress.progressProperty().bindBidirectional(this.progressReporter.orderQueueProgressProperty());
		kitchenHatchProgress.progressProperty().bindBidirectional(this.progressReporter.kitchenHatchProgressProperty());
		waitersBusyIndicator.progressProperty().bindBidirectional(this.progressReporter.waitersBusyProperty());
		cooksBusyIndicator.progressProperty().bind(this.progressReporter.cooksBusyProperty());

		/* TODO create the cooks and waiters, pass the kitchen hatch and the reporter instance and start them */
		NameGenerator m = new NameGenerator();
		int cooks = COOKS_COUNT;
		int waiters = WAITERS_COUNT;

		Thread[] threads = new Thread[cooks + waiters];

		for (int i = 0; i < cooks; i++) {
			threads[i] = new Thread(new Cook(m.generateName(), kitchenHatch, progressReporter));
		}
		for (int i = 0; i < waiters; i++) {
			threads[cooks + i] = new Thread(new Waiter(m.generateName(), kitchenHatch, progressReporter));
		}

		for (Thread t : threads) {
			t.start();
		}
	}
}
