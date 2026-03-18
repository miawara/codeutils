package mia.modmod.features.impl.internal.permissions;

import java.util.List;

public enum DFRank {
    DEFAULT("none", SupportPermission.NONE, ModeratorPermission.NONE),

    NOBLE("[\u200CNoble\u200C]", SupportPermission.NONE, ModeratorPermission.NONE),
    EMPEROR("[◦\u200CEmperor\u200C◦]", SupportPermission.NONE, ModeratorPermission.NONE),
    MYTHIC("[◇\u200CMythic\u200C◇]", SupportPermission.NONE, ModeratorPermission.NONE),
    OVERLORD("[◆\u200COverlord\u200C◆]", SupportPermission.NONE, ModeratorPermission.NONE),


    JR_HELPER("[JrHelper]", SupportPermission.HELPER, ModeratorPermission.NONE),
    HELPER("[Helper]", SupportPermission.HELPER, ModeratorPermission.NONE),
    SR_HELPER("[SrHelper]", SupportPermission.SENIOR, ModeratorPermission.NONE),

    JR_MOD("[JrMod]", SupportPermission.NONE, ModeratorPermission.JR_MOD),
    MOD("[Mod]", SupportPermission.NONE, ModeratorPermission.FULL_MOD),
    SR_MOD("[SrMod]", SupportPermission.NONE, ModeratorPermission.FULL_MOD),

    DEV("[Dev]", SupportPermission.SENIOR, ModeratorPermission.FULL_MOD),
    ADMIN("[Admin]", SupportPermission.SENIOR, ModeratorPermission.FULL_MOD),
    OWNER("[Owner]", SupportPermission.SENIOR, ModeratorPermission.FULL_MOD),;


    public String matcher;
    public SupportPermission supportPermission;
    public ModeratorPermission moderatorPermission;

    DFRank(String matcher, SupportPermission supportPermission, ModeratorPermission moderatorPermission) {
        this.matcher = matcher;
        this.supportPermission = supportPermission;
        this.moderatorPermission = moderatorPermission;
    }

    public static Permissions getStaffPermissions(List<DFRank> ranks) {
        SupportPermission supportPermissions = SupportPermission.NONE;
        ModeratorPermission moderatorPermissions = ModeratorPermission.NONE;

        for (DFRank rank : ranks) {
            if (rank.supportPermission.ordinal() > supportPermissions.ordinal()) supportPermissions = rank.supportPermission;
            if (rank.moderatorPermission.ordinal() > moderatorPermissions.ordinal()) moderatorPermissions = rank.moderatorPermission;
        }
        return new Permissions(supportPermissions, moderatorPermissions);
    }
}
