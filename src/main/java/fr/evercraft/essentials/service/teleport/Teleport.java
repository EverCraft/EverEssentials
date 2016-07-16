package fr.evercraft.essentials.service.teleport;

public class Teleport {
	
	private final Long time;
	private final Runnable function;
	
	public Teleport(Long delay, Runnable function) {
		this.time = System.currentTimeMillis() + delay;
		this.function = function;
	}
	
	public Long getTime() {
		return this.time;
	}

	public Runnable getFunction() {
		return this.function;
	}

	public void run() {
		this.function.run();
	}
}
