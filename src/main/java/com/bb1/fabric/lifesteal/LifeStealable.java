package com.bb1.fabric.lifesteal;

import com.bb1.fabric.bfapi.utils.ExceptionWrapper;

public interface LifeStealable {
	
	public static final String SAVE_IDENTIFIER = "LostHealth";
	
	public static LifeStealable getLifeStealable(Object input) {
		return ExceptionWrapper.executeWithReturn(input, (i)->(LifeStealable)i);
	}
	
	public double getLostHealth();
	
	public void setLostHealth(double newLostHealth);
	
	public default void looseHealth(double amountToLose) {
		this.setLostHealth(this.getLostHealth() + amountToLose);
	}
	
	public default void gainHealth(double amountToLose) {
		this.setLostHealth(this.getLostHealth() - amountToLose);
	}
	
	public default void transfer(LifeStealable ls, double amount) {
		ls.gainHealth(amount);
		this.looseHealth(amount);
	}
	
	public default double getHealthToBeStolenOnDeath() {
		return Loader.getConfig().healthToLooseOnDeath;
	}
	
}