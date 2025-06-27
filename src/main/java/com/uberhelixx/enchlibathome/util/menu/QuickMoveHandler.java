package com.uberhelixx.enchlibathome.util.menu;

import com.uberhelixx.enchlibathome.mixin.AbstractContainerMenuInvoker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

/**
 * The QuickMoveHandler is a registration helper for setting up {@link AbstractContainerMenu#quickMoveStack(Player, int)}
 */
public class QuickMoveHandler {

    protected List<QuickMoveRule> rules = new ArrayList<>();

    /**
     * Moves the stack in the specified slot index according to the quick move rules.
     *
     * @param container The parent container
     * @param player    The player who initiated the move
     * @param index     The index of the slot to move
     * @return An empty itemstack, indicating the move is completed (or impossible), or a copy of the original slot stack, indicating the move is partially
     *         incomplete.
     */
    public ItemStack quickMoveStack(IExposedContainer container, Player player, int index) {
        if (this.rules.isEmpty()) throw new RuntimeException("Quick Move requires at least one rule to be registered");
        ItemStack slotStackCopy = ItemStack.EMPTY;
        Slot slot = container.getMenuSlot(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            slotStackCopy = slotStack.copy();
            boolean matched = false;
            for (QuickMoveRule rule : this.rules) {
                if (rule.req.test(slotStack, index) && slot.mayPickup(player)) {
                    // moveMenuItemStackTo returns true if it successfully moved any amount of the item.
                    if (!container.moveMenuItemStackTo(slotStack, rule.startIdx, rule.endIdx, rule.reversed)) {
                        return ItemStack.EMPTY; // Aborting here means the move is impossible, as no transfer was accomplished with the matched rule.
                    }
                    slot.onTake(player, slotStack);
                    container.onQuickMove(slotStackCopy, slotStack, slot);
                    matched = true;
                    break;
                }
            }

            // If none of the rules match, we have no work to do. Abort early.
            if (!matched) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY); // Calls setChanged
            }
            else {
                slot.setChanged();
            }
        }

        return slotStackCopy;
    }

    /**
     * Registers a new {@link QuickMoveRule}.
     *
     * @param req      The check to determine if this rule applies to a given slot.
     * @param startIdx The (inclusive) start index of the target slot range to move to.
     * @param endIdx   The (exclusive) end index of the target slot range to move to.
     * @param reversed If true, the operation will attempt to place into endIdx and approach startIdx instead of start->end.
     */
    public void registerRule(BiPredicate<ItemStack, Integer> req, int startIdx, int endIdx, boolean reversed) {
        this.rules.add(new QuickMoveRule(req, startIdx, endIdx, reversed));
    }

    public void registerRule(BiPredicate<ItemStack, Integer> req, int startIdx, int endIdx) {
        this.registerRule(req, startIdx, endIdx, false);
    }

    protected record QuickMoveRule(BiPredicate<ItemStack, Integer> req, int startIdx, int endIdx, boolean reversed) {

    }

    public interface IExposedContainer {
        public default boolean moveMenuItemStackTo(ItemStack pStack, int pStartIndex, int pEndIndex, boolean pReverseDirection) {
            return ((AbstractContainerMenuInvoker) this)._moveItemStackTo(pStack, pStartIndex, pEndIndex, pReverseDirection);
        }

        public default Slot getMenuSlot(int index) {
            return ((AbstractContainerMenu) this).getSlot(index);
        }

        public default void onQuickMove(ItemStack original, ItemStack remaining, Slot slot) {
            slot.setChanged();
        }
    }

}
