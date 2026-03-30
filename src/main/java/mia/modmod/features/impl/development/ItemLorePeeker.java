package mia.modmod.features.impl.development;

import mia.modmod.Mod;
import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.FeatureManager;
import mia.modmod.features.impl.internal.mode.LocationAPI;
import mia.modmod.features.listeners.impl.RenderHUD;
import mia.modmod.render.util.DrawContextHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

public final class ItemLorePeeker extends Feature implements RenderHUD {
    public ItemLorePeeker(Categories category) {
        super(category, "Item Lore HUD", "lorehud", "Shows the lore of an item on your HUD.");
    }

    @Override
    public void renderHUD(GuiGraphics context, DeltaTracker tickCounter) {
        if (!LocationAPI.getMode().isDev()) return;
        if (Mod.MC.player == null) return;
        Inventory inventory = Mod.MC.player.getInventory();
        ItemStack selectedItem  = inventory.getSelectedItem();


        List<Component> lore = new ArrayList<>();
        ItemLore itemLore = selectedItem.getComponents().get(DataComponents.LORE);
        if (itemLore != null) lore.addAll(itemLore.lines());
        lore.addAll(ItemTagViewer.getItemLoreWithTags(selectedItem));

        if (!lore.isEmpty()) {
            Component name = selectedItem.getItemName();
            if (selectedItem.getComponents().has(DataComponents.CUSTOM_NAME)) name = selectedItem.getComponents().get(DataComponents.CUSTOM_NAME);
            lore.addFirst(name);

            try {
                DrawContextHelper.drawTooltip(context, lore, FeatureManager.getFeature(CPUDisplay.class).cpuText.x1() - 7, FeatureManager.getFeature(CPUDisplay.class).isDisplayed() ? FeatureManager.getFeature(CPUDisplay.class).container.y2() + 2 : FeatureManager.getFeature(CPUDisplay.class).cpuText.y1(), 0f);
            } catch (Exception e) {
                Mod.error(e.getMessage());
            }
        }
    }
}
