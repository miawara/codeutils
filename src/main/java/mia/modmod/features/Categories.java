package mia.modmod.features;

import mia.modmod.features.impl.internal.permissions.Permissions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum Categories {
    GENERAL(new Category("General", "General Features"), Permissions.NONE),
    DEV(new Category("Development", "Developer Features"), Permissions.NONE),
    SUPPORT(new Category("Support", "Support Staff Features"), Permissions.SUPPORT),
    MODERATION(new Category("Moderation", "Moderator features"), Permissions.MODERATOR),
    INTERNAL(new Category("Internal", "Machine, turn back now. The layers of this palace are not for your kind."), Permissions.NONE);

    private final Category category;
    private final Permissions requirePermissions;

    Categories(Category category, Permissions requirePermissions) {
        this.category = category;
        this.requirePermissions = requirePermissions;
    }

    public static ArrayList<Category> getCategories() { return Arrays.stream(values()).map(Categories::getCategory).collect(Collectors.toCollection(ArrayList::new)); }
    public Category getCategory() { return this.category; }
    public Permissions getRequirePermissions() { return this.requirePermissions; }
}
