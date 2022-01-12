package me.modernadventurer.lifesteal.mixin;

import me.modernadventurer.lifesteal.Loader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//NOTICE: file was modified to use gamerules instead
//of the configuration implementation and to use attributes, it also fixes the max health attribute
//being lost on death

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

	@Inject(method = "copyFrom", at = @At("TAIL"))
	public void preserveMaxHealth(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo callbackInfo) {
		EntityAttributeInstance oldHealth = oldPlayer.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
		assert oldHealth != null;
		EntityAttributeInstance health = ((ServerPlayerEntity) (Object) this).getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
		assert health != null;
		health.setBaseValue(oldHealth.getBaseValue());
	}

	@Inject(method = "onDeath", at = @At("TAIL"))
	public void onDeathLowerMaxHealth(DamageSource source, CallbackInfo callbackInfo) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		ServerWorld world = player.getWorld();
		Entity entity = source.getAttacker();
		int stealAmount = world.getGameRules().getInt(Loader.STEALAMOUNT);
		if(entity instanceof ServerPlayerEntity) {
			updateValueOf((ServerPlayerEntity)entity, stealAmount);
			updateValueOf((ServerPlayerEntity)(Object)this, -stealAmount);
		} else if(!world.getGameRules().getBoolean(Loader.PLAYERRELATEDONLY)) {
			updateValueOf((ServerPlayerEntity)(Object)this, -stealAmount);
		}
	}
	
	@Inject(method = "onSpawn", at = @At("TAIL"))
	public void onSpawnCheckToBan(CallbackInfo callbackInfo) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		ServerWorld world = player.getWorld();
		int minHealth = player.getWorld().getGameRules().getInt(Loader.MINPLAYERHEALTH);
		if(minHealth < 1) minHealth = 1;
		EntityAttributeInstance health = player.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
		assert health != null;
		if (world.getGameRules().getBoolean(Loader.BANWHENMINHEALTH) && health.getBaseValue() < minHealth) {
			player.networkHandler.connection.send(new net.minecraft.network.packet.s2c.play.DisconnectS2CPacket(new LiteralText("You lost your last life")));
		}
	}
	
	private void updateValueOf(ServerPlayerEntity of, int by) {
		//System.out.println("update values triggers");
		EntityAttributeInstance health = of.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
		assert health != null;
		double oldHealth = health.getValue();
		//System.out.println(oldHealth);
		float newHealth = (float) (oldHealth + by);
		//System.out.println(newHealth);
		int maxHealth = of.getWorld().getGameRules().getInt(Loader.MAXPLAYERHEALTH);
		if(maxHealth > 0 && newHealth > maxHealth) newHealth = maxHealth;
		//System.out.println(newHealth);
		health.setBaseValue(newHealth);
	}
}
