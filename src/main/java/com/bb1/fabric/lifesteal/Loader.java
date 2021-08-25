package com.bb1.fabric.lifesteal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import com.bb1.api.events.Events;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fabricmc.api.ModInitializer;

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
public class Loader implements ModInitializer {
	
	private static final Config CONFIG = new Config();
	
	public static final @NotNull Config getConfig() { return CONFIG; }
	
	private static final Map<UUID, Integer> DEATH_MAP = new HashMap<UUID, Integer>();
	
	public static final int getDeathsOf(UUID of) { return DEATH_MAP.getOrDefault(of, 0); }
	
	public static final void incrementDeathsBy(UUID of, int amount) { DEATH_MAP.put(of, (DEATH_MAP.getOrDefault(of, 0)+amount)); }
	
	@Override
	public void onInitialize() {
		CONFIG.load();
		CONFIG.save();
		for (Entry<String, JsonElement> entry : CONFIG.storage.entrySet()) {
			try {
				DEATH_MAP.put(UUID.fromString(entry.getKey()), entry.getValue().getAsInt());
			} catch (Throwable t) { }
		}
		Events.GameEvents.STOP_EVENT.register((server)->{
			JsonObject jsonObject = new JsonObject();
			for (Entry<UUID, Integer> entry : DEATH_MAP.entrySet()) {
				jsonObject.addProperty(entry.getKey().toString(), entry.getValue());
			}
			CONFIG.storage = jsonObject;
			CONFIG.save();
		});
	}

}
