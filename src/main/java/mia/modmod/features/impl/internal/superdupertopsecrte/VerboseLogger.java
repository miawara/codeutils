package mia.modmod.features.impl.internal.superdupertopsecrte;

import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.parameters.ParameterIdentifier;
import mia.modmod.features.parameters.impl.BooleanDataField;

public final class VerboseLogger extends Feature {
    public BooleanDataField verboseChatLogger;
    public VerboseLogger(Categories category) {
        super(category, "Verbose Chat Logger", "verbosechatlogger", "Adds verbose chat logging.");
        verboseChatLogger = new BooleanDataField("Enabled", "Logs decomposed chat message data", ParameterIdentifier.of(this, "is_enabled"), false, true);
    }
}
