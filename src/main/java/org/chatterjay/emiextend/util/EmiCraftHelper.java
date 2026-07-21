package org.chatterjay.emiextend.util;

import net.minecraft.world.item.ItemStack;

/**
 * ThreadLocal flag for passing single-craft signal from InventoryActionPacket.extraId
 * to CraftingTermSlot.doClick within the same server thread.
 */
public class EmiCraftHelper {

    /**
     * 1.20.1 compatibility: ItemStack.copyWithCount(int) does not exist in 1.20.1.
     * Use this helper instead.
     */
    public static ItemStack copyWithCount(ItemStack stack, int count) {
        ItemStack copy = stack.copy();
        copy.setCount(count);
        return copy;
    }
    private static final ThreadLocal<Boolean> singleCraftToInventory = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> aeAutocraftFromQuickCraft = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Boolean> aeAutocraftHandoff = ThreadLocal.withInitial(() -> false);
    private static final ThreadLocal<Long> aeAutocraftRequestedAmount = ThreadLocal.withInitial(() -> 0L);

    public static void markSingleCraft() {
        singleCraftToInventory.set(true);
    }

    public static boolean checkSingleCraft() {
        return singleCraftToInventory.get();
    }

    public static void markAeAutocraftFromQuickCraft() {
        aeAutocraftFromQuickCraft.set(true);
    }

    public static void markAeAutocraftFromQuickCraft(long requestedAmount) {
        aeAutocraftFromQuickCraft.set(true);
        aeAutocraftRequestedAmount.set(Math.max(0L, requestedAmount));
    }

    public static boolean checkAeAutocraftFromQuickCraft() {
        return aeAutocraftFromQuickCraft.get();
    }

    public static long getAeAutocraftRequestedAmount() {
        return aeAutocraftRequestedAmount.get();
    }

    public static void markAeAutocraftHandoff() {
        aeAutocraftHandoff.set(true);
    }

    public static boolean checkAeAutocraftHandoff() {
        return aeAutocraftHandoff.get();
    }

    public static void clear() {
        singleCraftToInventory.remove();
        aeAutocraftFromQuickCraft.remove();
        aeAutocraftHandoff.remove();
        aeAutocraftRequestedAmount.remove();
    }
}
