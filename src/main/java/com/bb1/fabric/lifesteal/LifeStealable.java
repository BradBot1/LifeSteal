package com.bb1.fabric.lifesteal;

import com.bb1.fabric.bfapi.utils.ExceptionWrapper;

public interface LifeStealable {
	
	public static LifeStealable getLifeStealable(Object input) {
		return ExceptionWrapper.executeWithReturn(input, (i)->(LifeStealable)i);
	}
	
	public double getLostHealth();
	
	public void setLostHealth(double newLostHealth);
	
	public default void looseHealth(double amountToLose) {
		this.setLostHealth(this.getLostHealth() + amountToLose);
	}
	
	public default void gainHealth(double amountToGain) {
		this.setLostHealth(this.getLostHealth() - amountToGain);
	}
	
	public default void transfer(LifeStealable ls, double amount) {
		ls.gainHealth(amount);
		this.looseHealth(amount);
	}
	
	public default double getHealthToBeStolenOnDeath() {
		return Loader.getConfig().healthToLooseOnDeath;
	}
	
}
