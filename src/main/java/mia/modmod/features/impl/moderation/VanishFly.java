package mia.modmod.features.impl.moderation;

import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.impl.internal.permissions.ModeratorPermission;
import mia.modmod.features.impl.internal.permissions.Permissions;
import mia.modmod.features.impl.internal.permissions.SupportPermission;

public final class VanishFly extends Feature {
    public VanishFly(Categories category) {
        super(category, "Vanish Fly", "vanishfly", "removed default spectator acceleration", new Permissions(SupportPermission.NONE, ModeratorPermission.JR_MOD));
    }
}
