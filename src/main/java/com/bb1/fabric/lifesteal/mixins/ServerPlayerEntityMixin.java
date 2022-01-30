package com.bb1.fabric.lifesteal.mixins;

import static com.bb1.fabric.lifesteal.LifeStealable.getLifeStealable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bb1.fabric.bfapi.utils.ExceptionWrapper;
import com.bb1.fabric.lifesteal.Config;
import com.bb1.fabric.lifesteal.LifeStealable;
import com.bb1.fabric.lifesteal.Loader;

import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements LifeStealable {
	
	@Unique
	private double _healthLoss = 0;
	
	@Override
	public double getLostHealth() {
		return this._healthLoss;
	}
	
	@Override
	public void setLostHealth(double newLostHealth) {
		this._healthLoss = newLostHealth;
		updateHealthLifeSteal();
	}
	
	@Inject(method = "onDeath", at = @At("TAIL"))
	public void onDeathLowerMaxHealth(DamageSource source, CallbackInfo callbackInfo) {
		final Entity entity = source.getAttacker();
		final Config conf = Loader.getConfig();
		final double transferAmount = this.getHealthToBeStolenOnDeath();
		if (conf.minHealth >= 0 && (20D - (this._healthLoss + transferAmount)) <= conf.minHealth) {
			entity.getCommandSource().sendFeedback(conf.notEnoughHeartsMessage, false);
			return; // too expensive
		}
		if (conf.looseHealthOnlyOnPlayerRelatedDeath) { // lock deaths to be player only
			if (entity instanceof ServerPlayerEntity spe) {
				this.transfer(getLifeStealable(spe), transferAmount);
			}
		} else {
			if (entity instanceof LifeStealable ls) {
				this.transfer(ls, transferAmount);
			} else {
				this.looseHealth(transferAmount);
			}
		}
	}
	
	@Inject(method = "onSpawn", at = @At("TAIL"))
	public void onSpawnCheckToBan(CallbackInfo callbackInfo) {
		final Config conf = Loader.getConfig();
		final double value = updateHealthLifeSteal();
		if (value <= 0 & conf.banWhenHealthReachesZero) {
			((ServerPlayerEntity)(Object)this).networkHandler.disconnect(conf.banReason);
			// player.networkHandler.connection.send(new net.minecraft.network.packet.s2c.play.DisconnectS2CPacket(Loader.getConfig().banReason));
		}
	}
	
	@Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
	public void onSaveAddDataLifeSteal(NbtCompound nbt, CallbackInfo callbackInfo) {
		nbt.putDouble(SAVE_IDENTIFIER, this._healthLoss);
	}
	
	@Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
	public void onLoadGetDataLifeSteal(NbtCompound nbt, CallbackInfo callbackInfo) {
		final Integer oldAmount = Loader.getOldDataFor(((ServerPlayerEntity)(Object)this).getUuid());
		if (oldAmount!=null) {
			this._healthLoss = (double) oldAmount;
			return;
		}
		this._healthLoss = ExceptionWrapper.executeWithReturn((Void) null, 0d, (n)->nbt.getDouble(SAVE_IDENTIFIER.toString()));
	}
	
	@Inject(method = "copyFrom", at = @At("TAIL"))
	public void onCopyFromMoveDataLifeSteal(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo callbackInfo) {
		this._healthLoss = LifeStealable.getLifeStealable(oldPlayer).getLostHealth();
	}
	
	@Unique
	public double updateHealthLifeSteal() {
		final Config config = Loader.getConfig();
		EntityAttributeInstance health = ((ServerPlayerEntity)(Object)this).getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
		double value = 20D - this._healthLoss;
		if (value<config.minHealth) {
			value = config.minHealth;
		} else if (config.maxHealth>0 && value>config.maxHealth) {
			value = config.maxHealth;
		}
		health.setBaseValue(value);
		return value;
	}
	
}
