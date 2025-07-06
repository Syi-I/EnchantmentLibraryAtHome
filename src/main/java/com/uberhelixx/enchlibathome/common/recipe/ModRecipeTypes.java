package com.uberhelixx.enchlibathome.common.recipe;

import com.uberhelixx.enchlibathome.EnchantmentLibraryAtHome;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EnchantmentLibraryAtHome.MODID);
    
    public static final RegistryObject<RecipeSerializer<NbtShapedRecipe>> NBT_SHAPED_SERIALIZER =
            SERIALIZERS.register("nbt_shaped_recipe", NbtShapedRecipe.NbtShapedRecipeSerializer::new);
    
    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
