package mia.modmod.features.parameters.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import mia.modmod.features.parameters.ParameterDataField;
import mia.modmod.features.parameters.ParameterIdentifier;
import net.minecraft.network.chat.Component;

import java.awt.*;

public class ColorDataField extends ParameterDataField<Color> {
    public ColorDataField(String name, String description, ParameterIdentifier identifier, Color defaultValue, boolean isConfig) {
        super(name, description, identifier, defaultValue, isConfig);
    }

    public int getRGB() {
        return getValue().getRGB();
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.addProperty(identifier.getIdentifier(), dataField.getRGB());
    }

    @Override
    public Color deserialize(JsonElement jsonObject) {
        return new Color(jsonObject.getAsInt());
    }

    @Override
    public void addYACLParameter(OptionGroup.Builder featureGroup) {
        featureGroup.option(
                Option.createBuilder(Color.class)
                        .name(Component.literal(this.getName()))
                        .description(OptionDescription.of(Component.literal(this.getDescription())))
                        .binding(
                                this.getValue(),
                                this::getValue,
                                this::setValue
                        )
                        .controller(ColorControllerBuilder::create)
                        .build()
        );
    }
}
