package com.uberhelixx.enchlibathome.datagen;

import com.uberhelixx.enchlibathome.EnchantmentLibraryAtHome;
import com.uberhelixx.enchlibathome.startup.registry.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ModLootTableProvider extends LootTableProvider {
    
    public ModLootTableProvider(PackOutput pOutput) {
        super(pOutput, Set.of(), List.of(new SubProviderEntry(BlockProvider::new, LootContextParamSets.BLOCK)));
    }
    
    public static class BlockProvider extends BlockLootSubProvider {
        protected BlockProvider() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }
        
        @Override
        protected void generate() {
            for (RegistryObject<Block> block : Registry.BLOCKS.getEntries()) {
                //FlatLights.LOGGER.info("Created " + block.getId() + " loot table.");
                this.dropSelf(block.get());
            }
        }
        
        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> EnchantmentLibraryAtHome.MODID.equals(Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getNamespace())).collect(Collectors.toSet());
        }
    }
}
