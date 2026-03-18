package mia.modmod.features.impl.internal.permissions;

public enum SupportPermission {
    NONE,
    HELPER,
    SENIOR;

    public boolean compare(SupportPermission supportPermission) {
        return this.ordinal() > supportPermission.ordinal();
    }
    public boolean atLeast(SupportPermission supportPermission) {
        return this.ordinal() >= supportPermission.ordinal();
    }
}
