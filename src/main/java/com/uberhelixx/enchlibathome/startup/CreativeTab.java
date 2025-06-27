package com.uberhelixx.enchlibathome.startup;

import com.uberhelixx.enchlibathome.EnchantmentLibraryAtHome;
import com.uberhelixx.enchlibathome.startup.registry.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EnchantmentLibraryAtHome.MODID);
    
    public static final RegistryObject<CreativeModeTab> TAB_ENCHLIBATHOME = CREATIVE_MODE_TAB.register(EnchantmentLibraryAtHome.MODID, () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.enchlibathome"))
            .icon(() -> new ItemStack(Registry.BASIC_LIBRARY_BLOCK.get()))
            .displayItems((itemDisplayParameters, output) -> {
                Registry.ITEMS.getEntries().forEach(e -> {
                    Item item = e.get();
                    output.accept(item);
                });
                
            })
            .build()
    );
    
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
