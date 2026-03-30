package mia.modmod.features.parameters.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.isxander.yacl3.api.OptionGroup;
import mia.modmod.features.parameters.ParameterDataField;
import mia.modmod.features.parameters.ParameterIdentifier;

public class JsonObjectDataField extends ParameterDataField<JsonObject> implements InternalDataField {
    public JsonObjectDataField(String name, String description, ParameterIdentifier identifier, JsonObject defaultValue, boolean isConfig) {
        super(name, description, identifier, defaultValue, isConfig);
    }

    @Override
    public void serialize(JsonObject jsonObject) {
        jsonObject.add(identifier.getIdentifier(), dataField);
    }

    @Override
    public JsonObject deserialize(JsonElement jsonObject) {
        return jsonObject.getAsJsonObject();
    }

    @Override
    public void addYACLParameter(OptionGroup.Builder featureGroup) {
    }
}
