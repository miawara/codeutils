package mia.modmod.features.impl.internal.permissions;

import mia.modmod.ColorBank;
import mia.modmod.Mod;
import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.FeatureManager;
import mia.modmod.features.listeners.impl.AlwaysEnabled;
import mia.modmod.render.screens.startup_screen.StartScreen;

public final class PermissionTracker extends Feature implements AlwaysEnabled {
    public PermissionTracker(Categories category) {
        super(category, "Permission Tracker", "perm_tracker", "Controls what features should be active based on your ranks.", Permissions.NONE);
    }
}