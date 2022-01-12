package me.modernadventurer.lifesteal.mixin;

import me.modernadventurer.lifesteal.Loader;
import me.modernadventurer.lifesteal.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

//NOTICE: file was modified to use gamerules instead
//of the configuration implementation and to use attributes, it also fixes the max health attribute
//being lost on death.

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "eatFood", at = @At("HEAD"))
	public void eatFood(World world, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
		LivingEntity entity = (LivingEntity)(Object)this;
		if (stack.isFood()) {
			if (!(entity instanceof PlayerEntity) || !((PlayerEntity)entity).getAbilities().creativeMode) {
				assert entity instanceof PlayerEntity;
				if(stack.getItem().equals(ModItems.HEART)) {
					System.out.println(world.toString());
					if(!Objects.equals(world.toString(), "ClientLevel")) {
						updateValueOf((PlayerEntity) entity, 2f);
					}
				}
			}
		}
	}

	private static void updateValueOf(PlayerEntity of, float by) {
		//of.sendMessage(Text.of("updateValueOf by: " + by), false);
		EntityAttributeInstance health = of.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH);
		assert health != null;
		float oldHealth = (float) health.getValue();
		float newHealth = oldHealth + by;
		int maxHealth = of.getWorld().getGameRules().getInt(Loader.MAXPLAYERHEALTH);
		//of.sendMessage(Text.of("Old: " + oldHealth + " New: " + oldHealth + " Max: " + maxHealth), false);
		if(maxHealth > 0 && newHealth > maxHealth) {
			newHealth = (float) maxHealth;
			//of.sendMessage(Text.of("newHealth set to max"), false);
		}
		health.setBaseValue(newHealth);
		//of.sendMessage(Text.of("Setting max health to " + newHealth), false);
		if(oldHealth == (float) maxHealth) {
			of.giveItemStack(new ItemStack(ModItems.HEART, 1));
			of.getInventory().updateItems();
			of.sendMessage(Text.of("You are already at the maximum amount of health!"), true);
		} else {
			of.setHealth(of.getHealth() + by);
			if(of.getHealth() > maxHealth) of.setHealth(maxHealth);
		}
	}
}
