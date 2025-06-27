package com.uberhelixx.enchlibathome.startup;

import com.uberhelixx.enchlibathome.EnchantmentLibraryAtHome;
import com.uberhelixx.enchlibathome.client.screen.LibraryScreen;
import com.uberhelixx.enchlibathome.startup.registry.Registry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = EnchantmentLibraryAtHome.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static void doClientSetup(FMLClientSetupEvent event) {
        
        //SCREENS
        MenuScreens.register(Registry.LIBRARY_MENU.get(), LibraryScreen::new);
    }
}
