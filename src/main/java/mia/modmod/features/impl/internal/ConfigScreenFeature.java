package mia.modmod.features.impl.internal;

import com.mojang.brigadier.CommandDispatcher;
import mia.modmod.Mod;
import mia.modmod.config.ConfigStore;
import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.listeners.impl.AlwaysEnabled;
import mia.modmod.features.listeners.impl.RegisterCommandListener;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public final class ConfigScreenFeature extends Feature implements RegisterCommandListener, AlwaysEnabled {
    public ConfigScreenFeature(Categories category) {
        super(category, "Config Screen", "config_screen", "Mod config screen");
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(
            ClientCommandManager.literal("modmod")
                .executes(commandContext -> {
                    Mod.message(Component.translatable("modmod.config.open_config"));
                    Mod.MC.execute(() -> {
                        Mod.setCurrentScreen(ConfigStore.getLibConfig().generateScreen(Mod.getCurrentScreen()));
                    });
                    return 1;
                })
        );
    }
}
