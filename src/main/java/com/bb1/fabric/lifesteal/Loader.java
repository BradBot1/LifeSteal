package com.bb1.fabric.lifesteal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.bb1.fabric.bfapi.GameObjects;
import com.bb1.fabric.bfapi.nbt.mark.INbtMarkListener;
import com.bb1.fabric.bfapi.nbt.mark.Markable;
import com.bb1.fabric.bfapi.permissions.PermissionUtils;
import com.bb1.fabric.bfapi.recipe.AbstractRecipe;
import com.bb1.fabric.bfapi.recipe.IRecipeRequirement;
import com.bb1.fabric.bfapi.recipe.IRecipeResult;
import com.bb1.fabric.bfapi.recipe.RecipeLoader;
import com.bb1.fabric.bfapi.recipe.ShapelessCraftingRecipe;
import com.bb1.fabric.bfapi.utils.Field;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.fabricmc.api.ModInitializer;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.UuidArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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
	
	@Deprecated
	private static final Map<UUID, Integer> DEATH_MAP = new HashMap<UUID, Integer>(); // ONLY TO BE USED TO LOAD OLD DATA
	
	@Internal
	public static final Integer getOldDataFor(@NotNull UUID uuid) {
		Integer value = DEATH_MAP.containsKey(uuid) ? DEATH_MAP.get(uuid) : null;
		DEATH_MAP.remove(uuid);
		return value;
	}
	
	@Override
	public void onInitialize() {
		CONFIG.load();
		CONFIG.save();
		if (CONFIG.enableCommands) {
			CONFIG.permission.register();
			GameObjects.GameEvents.COMMAND_REGISTRATION.addHandler((i)->{
				LiteralArgumentBuilder<ServerCommandSource> set = CommandManager.literal("set").then(CommandManager.argument("uuid", UuidArgumentType.uuid()).then(CommandManager.argument("amount", DoubleArgumentType.doubleArg()).executes((s)->{
					final UUID uuid = UuidArgumentType.getUuid(s, "uuid");
					final double amount = DoubleArgumentType.getDouble(s, "amount");
					@Nullable ServerPlayerEntity player = GameObjects.getMinecraftServer().getPlayerManager().getPlayer(uuid);
					if (player!=null) { // they are online
						LifeStealable.getLifeStealable(player).setLostHealth(amount);
						final Config conf = Loader.getConfig();
						s.getSource().sendFeedback(conf.successMessage, conf.broadcastToOps);
						return 1;
					}
					DEATH_MAP.put(uuid, (int) amount);
					final Config conf = Loader.getConfig();
					s.getSource().sendFeedback(conf.successMessageFile, conf.broadcastToOps);
					return 1;
				}))).then(CommandManager.argument("player", EntityArgumentType.player()).then(CommandManager.argument("amount", DoubleArgumentType.doubleArg()).executes((s)->{
					LifeStealable.getLifeStealable(EntityArgumentType.getPlayer(s, "player")).setLostHealth(DoubleArgumentType.getDouble(s, "amount"));
					final Config conf = Loader.getConfig();
					s.getSource().sendFeedback(conf.successMessage, conf.broadcastToOps);
					return 1;
				})));
				LiteralArgumentBuilder<ServerCommandSource> get = CommandManager.literal("get").then(CommandManager.argument("player", EntityArgumentType.player()).executes((s)->{
					final ServerPlayerEntity player = EntityArgumentType.getPlayer(s, "player");
					s.getSource().sendFeedback(((MutableText)player.getDisplayName()).append(new LiteralText(" has lost ").formatted(Formatting.RESET).append(new LiteralText("" + LifeStealable.getLifeStealable(player).getLostHealth()).formatted(Formatting.RED).append(new LiteralText(" health").formatted(Formatting.RESET)))), Loader.getConfig().broadcastToOps);
					return 1;
				}));
				for (String aliases : CONFIG.aliases) {
					i.get().register(CommandManager.literal(aliases).requires((s)->s.getEntity()==null||PermissionUtils.hasPermission(Field.of(s.getEntity()), CONFIG.permission.node())).then(set).then(get));
				}
			});
		}
		if (CONFIG.storage!=null) {
			for (Entry<String, JsonElement> entry : CONFIG.storage.entrySet()) {
				try {
					DEATH_MAP.put(UUID.fromString(entry.getKey()), entry.getValue().getAsInt());
				} catch (Throwable t) { }
			}
		}
		GameObjects.GameEvents.SERVER_STOP.addHandler((server)->{
			if (!DEATH_MAP.isEmpty()) {
				JsonObject jsonObject = new JsonObject();
				for (Entry<UUID, Integer> entry : DEATH_MAP.entrySet()) {
					jsonObject.addProperty(entry.getKey().toString(), entry.getValue());
				}
				CONFIG.storage = jsonObject;
				CONFIG.save();
			}
		});
		if (CONFIG.registerRequirements) {
			AbstractRecipe.addRequirementBuilder("losthealth>", (js)->{
				final double amount = js.getAsDouble();
				return new IRecipeRequirement() {
					
					@Override
					public boolean canCraft(Field<Entity> arg0) {
						return arg0.getObject() instanceof LifeStealable ls ? ls.getLostHealth() >= amount: false;
					}
					
					@Override
					public JsonObject addToObject(JsonObject arg0) {
						arg0.addProperty("losthealth>", amount);
						return arg0;
					}
				};
			});
			AbstractRecipe.addRequirementBuilder("losthealth<", (js)->{
				final double amount = js.getAsDouble();
				return new IRecipeRequirement() {
					
					@Override
					public boolean canCraft(Field<Entity> arg0) {
						return arg0.getObject() instanceof LifeStealable ls ? ls.getLostHealth() <= amount: false;
					}
					
					@Override
					public JsonObject addToObject(JsonObject arg0) {
						arg0.addProperty("losthealth<", amount);
						return arg0;
					}
				};
			});
			AbstractRecipe.addRequirementBuilder("losthealth=", (js)->{
				final double amount = js.getAsDouble();
				return new IRecipeRequirement() {
					
					@Override
					public boolean canCraft(Field<Entity> arg0) {
						return arg0.getObject() instanceof LifeStealable ls ? ls.getLostHealth() == amount: false;
					}
					
					@Override
					public JsonObject addToObject(JsonObject arg0) {
						arg0.addProperty("losthealth=", amount);
						return arg0;
					}
				};
			});
		}
		if (CONFIG.registerResults) {
			AbstractRecipe.addResultBuilder("gainhealth", (js)->{
				final double amount = js.getAsDouble();
				return new IRecipeResult() {
					
					@Override
					public void onCraft(Field<Entity> arg0) {
						if (arg0.getObject() instanceof LifeStealable ls) {
							ls.gainHealth(amount);
						}
					}
					
					@Override
					public JsonObject addToObject(JsonObject arg0) {
						arg0.addProperty("gainhealth", amount);
						return arg0;
					}
					
				};
			});
			AbstractRecipe.addResultBuilder("loosehealth", (js)->{
				final double amount = js.getAsDouble();
				return new IRecipeResult() {
					
					@Override
					public void onCraft(Field<Entity> arg0) {
						if (arg0.getObject() instanceof LifeStealable ls) {
							ls.looseHealth(amount);
						}
					}
					
					@Override
					public JsonObject addToObject(JsonObject arg0) {
						arg0.addProperty("loosehealth", amount);
						return arg0;
					}
					
				};
			});
			AbstractRecipe.addResultBuilder("sethealth", (js)->{
				final double amount = js.getAsDouble();
				return new IRecipeResult() {
					
					@Override
					public void onCraft(Field<Entity> arg0) {
						if (arg0.getObject() instanceof LifeStealable ls) {
							ls.setLostHealth(amount);
						}
					}
					
					@Override
					public JsonObject addToObject(JsonObject arg0) {
						arg0.addProperty("sethealth", amount);
						return arg0;
					}
					
				};
			});
		}
		if (CONFIG.allowCraftingOfHealth) { // set up recipes
			ItemStack is = Items.APPLE.getDefaultStack().setCustomName(new LiteralText("Health").formatted(Formatting.RED));
			Markable.getMarkable(is).applyMark("lifesteal");
			AbstractRecipe recipe = new ShapelessCraftingRecipe(is, null, Items.TOTEM_OF_UNDYING, Items.POISONOUS_POTATO, Items.BLAZE_ROD, Items.DRAGON_BREATH);
			recipe.addRequirement(AbstractRecipe.buildRequirement("xp", new JsonPrimitive(5)));
			recipe.addResult(AbstractRecipe.buildResult("xp", new JsonPrimitive(-5)));
			RecipeLoader.addDefaultRecipe(new Identifier("lifesteal", "health"), recipe);
		}
		if (CONFIG.enableMarks) {
			int counter = 0;
			for (String mark : CONFIG.marks) {
				new INbtMarkListener() {

					@Override
					public @NotNull String getMark() {
						return mark;
					}

					@Override
					public boolean onArmourUsed(ItemStack arg0, World arg1, @Nullable BlockPos arg2, Field<Entity> arg3,boolean arg4) {
						return false;
					}

					@Override
					public boolean onEntityHit(Field<Entity> arg0, @Nullable World arg1, @Nullable Field<Entity> arg2, @Nullable ItemStack arg3, boolean arg4) {
						return false;
					}

					@Override
					public boolean onItemUse(ItemStack arg0, World arg1, @Nullable BlockPos arg2, Field<Entity> arg3, boolean arg4) {
						if (arg3.getObject() instanceof LifeStealable ls) {
							if (CONFIG.limiter < 1 || ls.getLostHealth() < -(ls.getHealthToBeStolenOnDeath() * CONFIG.limiter)) {
								arg0.decrement(1);
								ls.gainHealth(ls.getHealthToBeStolenOnDeath());
								arg3.getObject().getCommandSource().sendFeedback(CONFIG.markOutputSuccess, CONFIG.broadcastToOpsMarks);
							} else {
								arg3.getObject().getCommandSource().sendFeedback(CONFIG.markOutputFailed, CONFIG.broadcastToOpsMarks);
							}
						}
						return true;
					}
					
				}.register(new Identifier("lifesteal", "health"+Integer.toString(counter++)));
			}
		}
	}

}
