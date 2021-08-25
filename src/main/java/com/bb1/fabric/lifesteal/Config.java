package com.bb1.fabric.lifesteal;

import com.bb1.api.config.Storable;
import com.google.gson.JsonObject;

/**
 * Copyright 2021 BradBot_1
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class Config extends com.bb1.api.config.Config {

	public Config() { super("lifesteal"); }
	/** 
	 * The max health a player can have
	 * 
	 * @apiNote if the maxHealth is set to <=0 then this constraint will be ignored
	 */
	@Storable public double maxHealth = -1D;
	/** 
	 * The minimum health a player can have in order to loose hearts
	 * 
	 * @apiNote if a players health is less than or equal to this value the player is unable to loose health
	 */
	@Storable public double minHealth = 0D;
	/** When the player looses their last heart they get banned from the server */
	@Storable public boolean banWhenHealthReachesZero = true;
	/** The amount of health to loose on death */
	@Storable public double healthToLooseOnDeath = 2D;
	/** If players should loose health only if the players death was caused by an other player */
	@Storable public boolean looseHealthOnlyOnPlayerRelatedDeath = true;
	
	@Storable public String banReason = "You have been banned due to your health reaching 0!";
	
	@Storable public JsonObject storage = new JsonObject();
	
}
