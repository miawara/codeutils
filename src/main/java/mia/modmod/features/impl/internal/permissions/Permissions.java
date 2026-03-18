package mia.modmod.features.impl.internal.permissions;

import mia.modmod.Mod;

public record Permissions(SupportPermission supportPermission, ModeratorPermission moderatorPermission) {
    public static final Permissions NONE = new Permissions(SupportPermission.NONE, ModeratorPermission.NONE);

}
