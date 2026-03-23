package mia.modmod.core.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ItemLore;

import java.util.*;

public class MDFItem {
    private final ItemStack item;

    public MDFItem(ItemStack itemStack) {
        this.item = itemStack;
    }

    public ItemContainerContents getItemContainerContents() {
        return this.item.getComponents().getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
    }

    public List<Component> getLore() {
        return this.item.getComponents().getOrDefault(DataComponents.LORE, ItemLore.EMPTY).styledLines();
    }

    public Optional<Tag> getHypercubeValue(String key) {
        Optional<HashMap<String, Tag>> tags = getHypercubeItemTags(false);
        if (tags.isPresent()) {
            if (tags.get().containsKey(key)) return Optional.of(tags.get().get(key));
        }
        return Optional.empty();
    }
    public Optional<HashMap<String, Tag>> getHypercubeItemTags(boolean ignoreInternalTags) {
        HashMap<String, Tag> hypercubeTags = new HashMap<>();
        CustomData data = this.item.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        Set<Map.Entry<String, Tag>> dataSet = data.copyTag().entrySet();

        Tag publicBukkitValues = null;
        for (Map.Entry<String, Tag> entry : dataSet) {
            if (entry.getKey().equals("PublicBukkitValues")) {
                publicBukkitValues = entry.getValue();
                break;
            }
        }

        if (publicBukkitValues != null) {
            Optional<CompoundTag> tag = publicBukkitValues.asCompound();
            if (tag.isPresent()) {
                for (Map.Entry<String, Tag> entry : tag.get().entrySet()) {
                    String key = entry.getKey().substring(10); // chops off "hypercube:";
                    if (List.of("varitem", "item_instance", "codetemplatedata").contains(key) && ignoreInternalTags) continue;
                    hypercubeTags.put(key, entry.getValue());
                }
            }
            return Optional.of(hypercubeTags);
        }
        return Optional.empty();
    }
}
