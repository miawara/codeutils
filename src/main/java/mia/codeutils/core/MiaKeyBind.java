package mia.codeutils.core;

import mia.codeutils.Mod;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import java.util.Random;

public class MiaKeyBind extends KeyMapping {
    private long lastLastPressed;
    private long lastPressed;
    private Runnable onToggle;

    private long threshold = 100L;

    public MiaKeyBind(String translationKey, int code, KeyBindCategories category) {
        this(translationKey, code, category, () -> {});

    }

    public MiaKeyBind(String translationKey, int code, KeyBindCategories category, Runnable onToggle) {
        //super(translationKey, code, Category.register(Identifier.fromNamespaceAndPath(Mod.MOD_ID, category.displayName())));
        super(translationKey, code, Category.register(Identifier.fromNamespaceAndPath("test", "sadf" + Math.random())));
        this.onToggle = onToggle;
        lastPressed = 0L;
    }

    public void tick() {
        if (isDown() && (System.currentTimeMillis() - lastLastPressed) > threshold) {
            onToggle.run();
        }
    }

    public boolean isDown() {
        lastLastPressed = lastPressed;
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - lastPressed;
        if (diff < threshold) return true;
        if (super.isDown()) {
            lastPressed = currentTime;
            return true;
        };
        return false;
    }
}
