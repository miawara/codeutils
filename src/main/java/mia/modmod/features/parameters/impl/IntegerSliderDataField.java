package mia.modmod.features.parameters.impl;

import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.OptionGroup;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import mia.modmod.features.parameters.ParameterIdentifier;
import net.minecraft.network.chat.Component;

public class IntegerSliderDataField extends IntegerDataField {
    private final int minValue, maxValue;
    public IntegerSliderDataField(String name, String description, ParameterIdentifier identifier, Integer defaultValue, Integer minValue, Integer maxValue, boolean isConfig) {
        super(name, description, identifier, defaultValue, isConfig);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public void setValue(Integer value) { this.dataField = Math.clamp(value, minValue, maxValue);}

    @Override
    public Integer getValue() { return Math.clamp(dataField, minValue, maxValue); }

    public Integer getMinValue() { return minValue; }
    public Integer getMaxValue() { return maxValue; }

    @Override
    public void addYACLParameter(OptionGroup.Builder featureGroup) {
        featureGroup.option(
                Option.createBuilder(Integer.class)
                        .name(Component.literal(this.getName()))
                        .description(OptionDescription.of(Component.literal(this.getDescription())))
                        .binding(
                                this.getValue(),
                                this::getValue,
                                this::setValue
                        )
                        .controller(opt -> IntegerSliderControllerBuilder.create(opt)
                                .range(getMinValue(), getMaxValue())
                                .step(1)
                                .valueFormatter(val -> Component.literal("" + val)))
                        .build()
        );
    }
}
