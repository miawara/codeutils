package mia.modmod.features.impl.internal.permissions;

public enum DFRank {
    DEFAULT("none", Permissions.NONE),

    NOBLE("[\u200CNoble\u200C]", Permissions.NONE),
    EMPEROR("[◦\u200CEmperor\u200C◦]", Permissions.NONE),
    MYTHIC("[◇\u200CMythic\u200C◇]", Permissions.NONE),
    OVERLORD("[◆\u200COverlord\u200C◆]", Permissions.NONE),


    JR_HELPER("[JrHelper]", Permissions.SUPPORT),
    HELPER("[Helper]", Permissions.SUPPORT),
    SR_HELPER("[SrHelper]", Permissions.SUPPORT),

    JR_MOD("[JrMod]", Permissions.MODERATOR),
    MOD("[Mod]", Permissions.MODERATOR),
    SR_MOD("[SrMod]", Permissions.MODERATOR),

    DEV("[Dev]", Permissions.ADMIN),
    ADMIN("[Admin]", Permissions.ADMIN),
    OWNER("[Owner]", Permissions.ADMIN),;


    private final String pattern;
    private final Permissions permissions;

    DFRank(String pattern, Permissions permissions) {
        this.pattern = pattern;
        this.permissions = permissions;
    }

    public String getPattern() { return pattern; }
    public Permissions getPermissions() { return permissions; }
}
