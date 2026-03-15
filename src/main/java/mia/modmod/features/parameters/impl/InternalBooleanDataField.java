package mia.modmod.features.parameters.impl;

import mia.modmod.features.parameters.ParameterIdentifier;

public class InternalBooleanDataField extends BooleanDataField implements InternalDataField {
    public InternalBooleanDataField(String name, ParameterIdentifier identifier, Boolean defaultValue, boolean isConfig) {
        super(name, identifier, defaultValue, isConfig);
    }
}
