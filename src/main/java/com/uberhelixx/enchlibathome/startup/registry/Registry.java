package com.uberhelixx.enchlibathome.startup.registry;

import com.uberhelixx.enchlibathome.EnchantmentLibraryAtHome;
import com.uberhelixx.enchlibathome.client.screen.LibraryMenu;
import com.uberhelixx.enchlibathome.common.block.LibraryBlock;
import com.uberhelixx.enchlibathome.common.blockentity.LibraryBE;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.Locale;
import java.util.function.Supplier;

public class Registry {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EnchantmentLibraryAtHome.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EnchantmentLibraryAtHome.MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, EnchantmentLibraryAtHome.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EnchantmentLibraryAtHome.MODID);
    
    private static final int BASIC_LIB_LEVEL = 16;
    private static final int ENDER_LIB_LEVEL = 31;
    
    //blocks
    //public static final RegistryObject<Block> BASIC_LIBRARY_BLOCK = create("basic_library_block", ForgeRegistries.BLOCKS);
    //public static final RegistryObject<Block> ENDER_LIBRARY_BLOCK = create("ender_library_block", ForgeRegistries.BLOCKS);
    public static final RegistryObject<Block> BASIC_LIBRARY_BLOCK = registerBlock("library", () -> new LibraryBlock(BasicLibraryBE::new, BASIC_LIB_LEVEL));
    public static final RegistryObject<Block> ENDER_LIBRARY_BLOCK = registerBlock("ender_library", () -> new LibraryBlock(EnderLibraryBE::new, ENDER_LIB_LEVEL));

    //items
    //public static final RegistryObject<Item> BASIC_LIBRARY_ITEM = create("basic_library_item", ForgeRegistries.ITEMS);
    //public static final RegistryObject<Item> ENDER_LIBRARY_ITEM = create("ender_library_item", ForgeRegistries.ITEMS);
    
    //block entities
    //public static final RegistryObject<BlockEntityType<LibraryBE>> BASIC_LIBRARY_BE = create("library_be", ForgeRegistries.BLOCK_ENTITY_TYPES);
    //public static final RegistryObject<BlockEntityType<LibraryBE>> ENDER_LIBRARY_BE = create("ender_library_be", ForgeRegistries.BLOCK_ENTITY_TYPES);
    public static final RegistryObject<BlockEntityType<BasicLibraryBE>> BASIC_LIBRARY_BE =
            BLOCK_ENTITIES.register("basic_library_be", () ->
                    BlockEntityType.Builder.of(BasicLibraryBE::new, BASIC_LIBRARY_BLOCK.get()).build(null));
    
    public static final RegistryObject<BlockEntityType<EnderLibraryBE>> ENDER_LIBRARY_BE =
            BLOCK_ENTITIES.register("ender_library_be", () ->
                    BlockEntityType.Builder.of(EnderLibraryBE::new, ENDER_LIBRARY_BLOCK.get()).build(null));
    
    //menus
    //public static final RegistryObject<MenuType<LibraryMenu>> LIBRARY_MENU = create("library_menu", ForgeRegistries.MENU_TYPES);
    public static final RegistryObject<MenuType<LibraryMenu>> LIBRARY_MENU = registerMenuType("library_menu", LibraryMenu::new);

    
    public static class BasicLibraryBE extends LibraryBE {
        public BasicLibraryBE(BlockPos pos, BlockState state) {
            super(BASIC_LIBRARY_BE.get(), pos, state, BASIC_LIB_LEVEL);
        }
    }
    
    public static class EnderLibraryBE extends LibraryBE {
        public EnderLibraryBE(BlockPos pos, BlockState state) {
            super(ENDER_LIBRARY_BE.get(), pos, state, ENDER_LIB_LEVEL);
        }
    }
    
    public static <T> RegistryObject<T> create(String path, IForgeRegistry<? super T> registry) {
        return RegistryObject.create(new ResourceLocation(EnchantmentLibraryAtHome.MODID, path.toLowerCase(Locale.ROOT)), registry);
    }
    
    //register each block to the registry, adds block item automatically with each block
    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    
    //function to make a block item from each registered block
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }
    
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }
    
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        MENUS.register(eventBus);
        BLOCK_ENTITIES.register(eventBus);
    }
}
