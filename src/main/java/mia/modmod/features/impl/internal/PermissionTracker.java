package mia.modmod.features.impl.internal;

import mia.modmod.features.Categories;
import mia.modmod.features.Feature;

public final class PermissionTracker extends Feature {
    public PermissionTracker(Categories category) {
        super(category, "Permission Tracker", "perm_tracker", "Controls what features should be active based on your ranks.");
    }

}