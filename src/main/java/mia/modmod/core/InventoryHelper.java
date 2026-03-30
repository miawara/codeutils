package mia.modmod.core;

import mia.modmod.Mod;
import net.minecraft.network.protocol.game.ServerboundSetCreativeModeSlotPacket;
import net.minecraft.world.item.ItemStack;

public class InventoryHelper {
    public static void setHotbar(int slot, ItemStack item) {
        if(Mod.MC.getConnection() == null || Mod.MC.player == null) return;

        Mod.MC.player.getInventory().setItem(slot, item);
        NetworkManager.sendPacket(new ServerboundSetCreativeModeSlotPacket(36 + slot, item));
    }

    public static void setSlotItem(int slot, ItemStack item) {
        if(Mod.MC.getConnection() == null || Mod.MC.player == null) return;
        NetworkManager.sendPacket(new ServerboundSetCreativeModeSlotPacket(slot < 9 ? slot + 36 : slot, item));
        Mod.MC.player.getInventory().setItem(slot, item);
        //updateClientInventoryScreen(slot, item);
    }
    public static void updateClientInventoryScreen(int slot, ItemStack item) {
        Mod.MC.player.inventoryMenu.setItem(slot, 0, item);
    }
}
