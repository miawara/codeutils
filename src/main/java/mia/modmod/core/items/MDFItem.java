package mia.modmod.core.items;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.*;

public class MDFItem {
    private ItemStack item;

    public MDFItem(ItemStack itemStack) {
        this.item = itemStack;
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
