package me.modernadventurer.lifesteal.block;

import me.modernadventurer.lifesteal.Loader;
import me.modernadventurer.lifesteal.item.ModItems;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ModBlocks {

    public static final Block HEARTDUSTORE = registerBlock("heart_dust_ore",
            new Block(FabricBlockSettings
                    .of(Material.STONE)
                    .breakByTool(FabricToolTags.PICKAXES, 3)
                    .requiresTool()
                    .strength(6.0f, 6.0f)
                    .sounds(BlockSoundGroup.STONE)));

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registry.BLOCK, new Identifier(Loader.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(Registry.ITEM, new Identifier(Loader.MOD_ID, name),
        new BlockItem(block, new FabricItemSettings().group(ItemGroup.BUILDING_BLOCKS)));
    }

    public static void registerModBlocks() {
        System.out.println("Registering Mod Blocks for " + Loader.MOD_ID);
    }
}
