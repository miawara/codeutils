package mia.modmod.features.impl.moderation;

import mia.modmod.features.Categories;
import mia.modmod.features.Feature;

public final class VanishFly extends Feature {
    public VanishFly(Categories category) {
        super(category, "Vanish Fly", "vanishfly", "removed default spectator acceleration");
    }
}
