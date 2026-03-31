package mia.modmod.features.impl.internal.permissions;

public record Permissions(boolean supportPermission, boolean moderatorPermission, boolean adminPermission) {
    public Permissions() {
        this(false, false, false);
    }
    public Permissions(boolean supportPermission, boolean moderatorPermission) {
        this(supportPermission, moderatorPermission, false);
    }

    public static final Permissions NONE = new Permissions(false, false, false);
    public static final Permissions MODERATOR = new Permissions(false, true, false);
    public static final Permissions SUPPORT = new Permissions(true, false, false);
    public static final Permissions MOD_SUPPORT = new Permissions(true, true, false);
    public static final Permissions ADMIN = new Permissions(true, true, true);

    public boolean equals(Permissions permissions) {
        return  permissions.supportPermission == this.supportPermission &&
                permissions.moderatorPermission == this.moderatorPermission &&
                permissions.adminPermission == this.adminPermission;
    }
    public Permissions add(Permissions permissions) {
        return new Permissions(
                this.supportPermission || permissions.supportPermission,
                this.moderatorPermission || permissions.moderatorPermission,
                this.adminPermission || permissions.adminPermission
                );
    }

    private int toInt(boolean b) { return b ? 1 : 0; }

    public boolean hasPerm(Permissions checkPerms) {
        return  toInt(supportPermission) >= toInt(checkPerms.supportPermission) &&
                toInt(moderatorPermission) >= toInt(checkPerms.moderatorPermission) &&
                toInt(adminPermission) >= toInt(checkPerms.adminPermission);
    }

}
