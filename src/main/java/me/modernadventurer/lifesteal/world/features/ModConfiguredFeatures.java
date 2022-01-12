package me.modernadventurer.lifesteal.world.features;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.SquarePlacementModifier;
import net.minecraft.world.gen.feature.*;
import me.modernadventurer.lifesteal.Loader;
import me.modernadventurer.lifesteal.block.ModBlocks;

public class ModConfiguredFeatures {
    public static final ConfiguredFeature<?, ?> HEART_DUST_ORE_CONFIGURED_FEATURE = Feature.ORE
            .configure(new OreFeatureConfig(
                    OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
                    ModBlocks.HEARTDUSTORE.getDefaultState(),
                    3)); // vein size

    public static PlacedFeature HEART_DUST_ORE_PLACED_FEATURE = HEART_DUST_ORE_CONFIGURED_FEATURE.withPlacement(
            CountPlacementModifier.of(9), // number of veins per chunk
            SquarePlacementModifier.of(), // spreading horizontally
            HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(50))); // height

    public static void registerOres() {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                new Identifier(Loader.MOD_ID, "heart_dust_ore"), HEART_DUST_ORE_CONFIGURED_FEATURE);

        Registry.register(BuiltinRegistries.PLACED_FEATURE, new Identifier(Loader.MOD_ID, "heart_dust_ore"),
                HEART_DUST_ORE_PLACED_FEATURE);

        BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
                RegistryKey.of(Registry.PLACED_FEATURE_KEY,
                        new Identifier(Loader.MOD_ID, "heart_dust_ore")));
    }
}
