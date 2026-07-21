package org.chatterjay.emiextend.mixin;

import appeng.core.sync.packets.FillCraftingGridFromRecipePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.chatterjay.emiextend.util.ModLogger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.List;
import java.util.StringJoiner;

@Mixin(value = FillCraftingGridFromRecipePacket.class, remap = false)
public class FillCraftingGridFromRecipePacketMixin {
    @Unique
    private static Object emilink$getField(Object obj, String... names) {
        for (String name : names) {
            try {
                Field f = obj.getClass().getDeclaredField(name);
                f.setAccessible(true);
                return f.get(obj);
            } catch (Exception ignored) {}
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Unique
    private static String emilink$describeTemplates(Object packet) {
        var templates = emilink$getField(packet, "ingredientTemplates", "templates");
        if (templates instanceof List<?> list) {
            var joiner = new StringJoiner(" | ", "[", "]");
            for (int i = 0; i < list.size(); i++) {
                Object o = list.get(i);
                if (o instanceof ItemStack stack && !stack.isEmpty()) {
                    joiner.add(i + "=" + stack.getDisplayName().getString() + " x" + stack.getCount());
                }
            }
            return joiner.toString();
        }
        return "[]";
    }

    @Inject(method = "handleOnServer", at = @At("HEAD"), remap = false, require = 0)
    private void emilink$logFillCraftingGridStart(ServerPlayer player, CallbackInfo ci) {
        var packet = (Object) this;
        Object recipeId = emilink$getField(packet, "recipeId", "recipe");
        Object craftMissing = emilink$getField(packet, "craftMissing", "craft_missing");
        ModLogger.debug("AE_EMI_CTRL_CRAFT server-fill-start player={} recipeId={} craftMissing={} templates={} menu={}",
                player == null ? "null" : player.getGameProfile().getName(),
                recipeId,
                craftMissing,
                emilink$describeTemplates(packet),
                player == null || player.containerMenu == null ? "null" : player.containerMenu.getClass().getName());
    }

    @Inject(method = "handleOnServer", at = @At("RETURN"), remap = false, require = 0)
    private void emilink$logFillCraftingGridEnd(ServerPlayer player, CallbackInfo ci) {
        var packet = (Object) this;
        Object recipeId = emilink$getField(packet, "recipeId", "recipe");
        Object craftMissing = emilink$getField(packet, "craftMissing", "craft_missing");
        ModLogger.debug("AE_EMI_CTRL_CRAFT server-fill-end player={} recipeId={} craftMissing={} menu={}",
                player == null ? "null" : player.getGameProfile().getName(),
                recipeId,
                craftMissing,
                player == null || player.containerMenu == null ? "null" : player.containerMenu.getClass().getName());
    }
}
