package mia.modmod.features;

import mia.modmod.features.impl.internal.permissions.Permissions;
import mia.modmod.features.listeners.impl.AlwaysEnabled;
import mia.modmod.features.parameters.ParameterDataField;
import mia.modmod.features.parameters.ParameterIdentifier;
import mia.modmod.features.parameters.impl.InternalBooleanDataField;

import java.util.ArrayList;

public abstract class Feature {
    protected String id, name, description;
    protected Category category;
    private final ArrayList<ParameterDataField<?>> parameterDataFields;
    private final InternalBooleanDataField enabledParameter;
    protected final Permissions permissions;

    public Feature(Categories category, String name, String id, String description) {
        this(category, name, id, description, Permissions.NONE);
    }

    public Feature(Categories category, String name, String id, String description, Permissions permissions) {
        this.permissions = permissions;
        this.id = id;
        this.name = name;
        this.description = description;
        this.parameterDataFields = new ArrayList<>();

        enabledParameter = new InternalBooleanDataField("Enabled", "", ParameterIdentifier.of(this, "enabled"), true, false);

        category.getCategory().addFeature(this);
    }

    public ArrayList<? extends ParameterDataField<?>> getParameterDataFields() { return parameterDataFields; }
    public void addParameter(ParameterDataField<?> parameterDataField) {
        parameterDataFields.add(parameterDataField);
    }

    public void setCategory(Category category) { this.category = category; }
    public void setEnabled(boolean enabled) { this.enabledParameter.setValue(enabled); }
    public boolean getEnabled() { return getAlwaysEnabled() || this.enabledParameter.getValue(); }
    public boolean getAlwaysEnabled() { return this instanceof AlwaysEnabled; }

    public String getID() { return this.id; }
    public String getName() { return this.name; }
    public String getDescription() { return this.description; }

    public Permissions getRequiredPermissions() { return permissions; }
}
