package com.bb1.fabric.lifesteal.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bb1.fabric.lifesteal.Config;
import com.bb1.fabric.lifesteal.Loader;

import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
	
	@Inject(method = "onDeath", at = @At("TAIL"))
	public void onDeathLowerMaxHealth(DamageSource source, CallbackInfo callbackInfo) {
		Entity entity = source.getAttacker();
		if (Loader.getConfig().looseHealthOnlyOnPlayerRelatedDeath) {
			if (entity instanceof ServerPlayerEntity) {
				updateValueOf((ServerPlayerEntity)(Object)this, 1);
				updateValueOf((ServerPlayerEntity)entity, -1);
			}
		} else {
			updateValueOf((ServerPlayerEntity)(Object)this, 1);
			if (entity instanceof ServerPlayerEntity) {
				updateValueOf((ServerPlayerEntity)entity, -1);
			}
		}
	}
	
	@Inject(method = "onSpawn", at = @At("TAIL"))
	public void onSpawnCheckToBan(CallbackInfo callbackInfo) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
		if (Loader.getConfig().banWhenHealthReachesZero && player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getBaseValue()<=0) {
			com.bb1.api.Loader.getMinecraftServer().getPlayerManager().getUserBanList().add(new BannedPlayerEntry(player.getGameProfile(), null, "LifeSteal", null, Loader.getConfig().banReason));
			player.networkHandler.connection.send(new net.minecraft.network.packet.s2c.play.DisconnectS2CPacket(new LiteralText(Loader.getConfig().banReason)));
		}
		updateValueOf(player, 0);
	}
	
	private void updateValueOf(ServerPlayerEntity of, int by) {
		final Config config = Loader.getConfig();
		Loader.incrementDeathsBy(of.getUuid(), by);
		EntityAttributeInstance health = of.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH); // of.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
		double value = 20D-(config.healthToLooseOnDeath*Loader.getDeathsOf(of.getUuid()));
		if (value<config.minHealth) {
			value = config.minHealth;
		} else if (config.maxHealth>0 && value>=config.maxHealth) {
			value = config.maxHealth;
		}
		health.setBaseValue(value);
	}
	
}
