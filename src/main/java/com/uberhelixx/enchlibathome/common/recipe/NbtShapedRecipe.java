package com.uberhelixx.enchlibathome.common.recipe;

import com.google.gson.JsonObject;
import com.uberhelixx.enchlibathome.startup.registry.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.common.crafting.CraftingHelper;

public class NbtShapedRecipe extends ShapedRecipe {
    public NbtShapedRecipe(ShapedRecipe pRecipe, ItemStack pResult) {
        super(pRecipe.getId(), pRecipe.getGroup(), pRecipe.category(), pRecipe.getRecipeWidth(), pRecipe.getRecipeHeight(), pRecipe.getIngredients(), pResult, pRecipe.showNotification());
    }
    
    @Override
    public ItemStack assemble(CraftingContainer pContainer, RegistryAccess pRegistryAccess) {
        ItemStack output = super.assemble(pContainer, pRegistryAccess);
        
        //check crafting ingredients in the container
        for (int i = 0; i < pContainer.getContainerSize(); i++) {
            ItemStack ingredient = pContainer.getItem(i);
            if (!ingredient.isEmpty() && ingredient.hasTag()) {
                //is this ingredient the library block and is the output item the upgraded ender library block since we only want to put the enchantment info tag on a library block
                if (ingredient.getItem() == Registry.BASIC_LIBRARY_BLOCK.get().asItem() && output.getItem() == Registry.ENDER_LIBRARY_BLOCK.get().asItem()) {
                    //copy the compound tag to the output item if it's not null
                    if(ingredient.getTag() != null) {
                        CompoundTag tagCopy = ingredient.getTag().copy();
                        output.setTag(tagCopy);
                    }
                }
            }
        }
        
        return output;
    }
    
    public static class NbtShapedRecipeSerializer implements RecipeSerializer<NbtShapedRecipe> {
        private final RecipeSerializer<ShapedRecipe> parent = RecipeSerializer.SHAPED_RECIPE;
        
        @Override
        public NbtShapedRecipe fromJson(final ResourceLocation recipeID, final JsonObject json) {
            ShapedRecipe recipe = parent.fromJson(recipeID, json);
            
            //need to get result item of crafting recipe through the json as this way doesn't require registry access
            final ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            
            return new NbtShapedRecipe(recipe, result);
        }
        
        @Override
        public NbtShapedRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            ShapedRecipe recipe = parent.fromNetwork(id, buffer);
            assert recipe != null;
            return new NbtShapedRecipe(recipe, buffer.readItem());
        }
        
        @Override
        public void toNetwork(FriendlyByteBuf buffer, NbtShapedRecipe recipe) {
            buffer.writeItem(recipe.getResultItem(null));
            parent.toNetwork(buffer, recipe);
        }
    }
}
