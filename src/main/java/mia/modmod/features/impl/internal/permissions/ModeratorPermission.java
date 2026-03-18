package mia.modmod.features.impl.internal.permissions;

import mia.modmod.Mod;

public enum ModeratorPermission {
    NONE,
    JR_MOD,
    FULL_MOD;

    public boolean compare(ModeratorPermission moderatorPermission) {
        return this.ordinal() > moderatorPermission.ordinal();
    }

    public boolean atLeast(ModeratorPermission moderatorPermission) {
        return this.ordinal() >= moderatorPermission.ordinal();
    }
}
