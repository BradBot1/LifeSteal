package com.bb1.fabric.lifesteal;

import org.jetbrains.annotations.ApiStatus.Internal;

import com.bb1.fabric.bfapi.Constants;
import com.bb1.fabric.bfapi.config.ConfigComment;
import com.bb1.fabric.bfapi.config.ConfigSub;
import com.bb1.fabric.bfapi.permissions.Permission;
import com.bb1.fabric.bfapi.permissions.PermissionLevel;
import com.google.gson.JsonObject;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

/**
 * Copyright 2022 BradBot_1
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
public class Config extends com.bb1.fabric.bfapi.config.Config {

	public Config() { super(new Identifier(Constants.ID, "lifesteal")); }
	
	@ConfigComment("The maximum *total* health that can be gained, if less than 0 it will be ignored")
	public double maxHealth = -1D;
	
	@ConfigComment("The minimum *total* health that someone can be at, when this value is reached they cannot be used to gain hearts")
	public double minHealth = 0D;
	
	@ConfigComment("The amount of health to be lost when a player dies")
	public double healthToLooseOnDeath = 2D;
	
	@ConfigComment("This toggle dictates if a player can loose health from deaths that are not caused by a player, if true then they cannot")
	public boolean looseHealthOnlyOnPlayerRelatedDeath = false;
	
	/** When the player looses their last heart they get banned from the server */
	@ConfigSub("punishment")
	public boolean banWhenHealthReachesZero = true;
	@ConfigSub("punishment")
	public Text banReason = new LiteralText("You have been banned due to your health reaching 0!");
	
	@ConfigComment("The message that is displayed when you kill a player that doesn't have enough hearts to kill you")
	public Text notEnoughHeartsMessage = new LiteralText("The player did not have any hearts you could steal");
	
	@ConfigComment("Do not modify this! You can/should use the ingame command to modify a players health, this is used to store changes about a player while they are offline")
	@Internal
	public JsonObject storage = null;
	
	@ConfigComment("The aliases that the command will register under")
	@ConfigSub("command")
	public String[] aliases = { "lifesteal",  "ls" };
	
	@ConfigComment("The permission required to modify a players health *if you dont have a permission db the level can be set to the wanted operator level*")
	@ConfigSub("command")
	public Permission permission = new Permission("lifesteal.modify", PermissionLevel.OP_3);
	
	@ConfigComment("The text sent to the player when they set another players health")
	@ConfigSub("command")
	public Text successMessage = new LiteralText("Done!").formatted(Formatting.GREEN);
	
	@ConfigComment("The text sent to the player when they set another players health but they are not online so it stored it to be applied when the player joins")
	@ConfigSub("command")
	public Text successMessageFile = new LiteralText("Done! (to file)").formatted(Formatting.GREEN);
	
	@ConfigComment("If setting a players health should be broadcasted to all operators")
	@ConfigSub("command")
	public boolean broadcastToOps = true;
	
}
