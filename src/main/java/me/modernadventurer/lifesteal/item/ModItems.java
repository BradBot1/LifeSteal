package me.modernadventurer.lifesteal.item;

import me.modernadventurer.lifesteal.Loader;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModItems {

    public static final Item HEARTDUST = registerItem("heart_dust",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item HEARTCRYSTAL = registerItem("heart_crystal",
            new Item(new FabricItemSettings().group(ItemGroup.MISC)));

    public static final Item HEART = registerItem("heart",
            new Item(new FabricItemSettings()
                    .group(ItemGroup.FOOD)
                    .food(new FoodComponent.Builder().hunger(0).saturationModifier(0).alwaysEdible().build())));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(Loader.MOD_ID, name), item);
    }

    public static void registerModItems() {
        System.out.println("Registering Mod Items for " + Loader.MOD_ID);
    }
}
