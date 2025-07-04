package com.uberhelixx.enchlibathome.client.screen;

import com.uberhelixx.enchlibathome.common.blockentity.LibraryBE;
import com.uberhelixx.enchlibathome.common.network.ButtonClickMessage.IButtonContainer;
import com.uberhelixx.enchlibathome.startup.registry.Registry;
import com.uberhelixx.enchlibathome.util.menu.BlockEntityMenu;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

import java.util.List;

public class LibraryMenu extends BlockEntityMenu<LibraryBE> implements IButtonContainer {
    protected SimpleContainer ioInv = new SimpleContainer(3);
    protected Runnable notifier = null;
    
    public LibraryMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()).getBlockPos());
    }
    
    public LibraryMenu(int id, Inventory inv, BlockPos pos) {
        super(Registry.LIBRARY_MENU.get(), id, inv, pos);
        this.tile.addListener(this);
        this.initCommon(inv);
    }
    
    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!this.level.isClientSide) this.tile.removeListener(this);
        this.clearContainer(player, this.ioInv);
    }
    
    void initCommon(Inventory inv) {
        this.addSlot(new Slot(this.ioInv, 0, 142, 77){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.ENCHANTED_BOOK;
            }
            
            @Override
            public int getMaxStackSize() {
                return 1;
            }
            
            @Override
            public void setChanged() {
                super.setChanged();
                if (!LibraryMenu.this.level.isClientSide && !this.getItem().isEmpty()) {
                    LibraryMenu.this.tile.depositBook(this.getItem());
                }
                if (!this.getItem().isEmpty() && LibraryMenu.this.level.isClientSide)
                    inv.player.level().playSound(inv.player, LibraryMenu.this.pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL, 0.5F, 0.7F);
                LibraryMenu.this.ioInv.setItem(0, ItemStack.EMPTY);
            }
        });
        this.addSlot(new Slot(this.ioInv, 1, 142, 106){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() == Items.ENCHANTED_BOOK;
            }
            
            @Override
            public int getMaxStackSize() {
                return 1;
            }
        });
        this.addSlot(new Slot(this.ioInv, 2, 142, 18){
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true;
            }
            
            @Override
            public int getMaxStackSize() {
                return 1;
            }
            
            @Override
            public void setChanged() {
                LibraryMenu.this.onChanged();
            }
        });
        this.addPlayerSlots(inv, 8, 148);
        this.mover.registerRule((stack, slot) -> slot == 0, 3, 39);
        this.mover.registerRule((stack, slot) -> slot == 1, 3, 39);
        this.mover.registerRule((stack, slot) -> slot == 2, 3, 39);
        this.mover.registerRule((stack, slot) -> stack.is(Items.ENCHANTED_BOOK), 0, 1);
        this.mover.registerRule((stack, slot) -> true, 2, 3);
        this.registerInvShuffleRules();
    }
    
    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(this.pos.getX(), this.pos.getY(), this.pos.getZ()) < 16 * 16 && this.tile != null && !this.tile.isRemoved();
    }
    
    public int getNumStoredEnchants() {
        return (int) this.tile.getPointsMap().values().intStream().filter(s -> s > 0).count();
    }
    
    public List<Object2IntMap.Entry<Enchantment>> getPointsForDisplay() {
        return this.tile.getPointsMap().object2IntEntrySet().stream().filter(s -> s.getIntValue() > 0).toList();
    }
    
    public int getMaxLevel(Enchantment enchant) {
        return this.tile.getMax(enchant);
    }
    
    public int getPointCap() {
        return this.tile.maxPoints;
    }
    
    public void setNotifier(Runnable r) {
        this.notifier = r;
    }
    
    public void onChanged() {
        if (this.notifier != null) this.notifier.run();
    }
    
    @Override
    public void onButtonClick(int id) {
        boolean shift = (id & 0x80000000) == 0x80000000;
        if (shift) id = id & 0x7FFFFFFF; // Look, if this ever breaks, it's not my fault someone has 2 billion enchantments.
        Enchantment ench = ((ForgeRegistry<Enchantment>) ForgeRegistries.ENCHANTMENTS).getValue(id);
        ItemStack outSlot = this.ioInv.getItem(1);
        int curLvl = EnchantmentHelper.getEnchantments(outSlot).getOrDefault(ench, 0);
        int targetLevel = shift ? Math.min(this.tile.getMax(ench), 1 + (int) (Math.log(this.tile.getPointsMap().getInt(ench) + LibraryBE.levelToPoints(curLvl)) / Math.log(2))) : curLvl + 1;
        if (!this.tile.canExtract(ench, targetLevel, curLvl)) return;
        if (outSlot.isEmpty()) outSlot = new ItemStack(Items.ENCHANTED_BOOK);
        this.tile.extractEnchant(outSlot, ench, targetLevel);
        this.ioInv.setItem(1, outSlot);
    }
}
