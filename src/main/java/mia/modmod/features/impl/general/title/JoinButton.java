package mia.modmod.features.impl.general.title;

import mia.modmod.features.Categories;
import mia.modmod.features.Feature;
import mia.modmod.features.parameters.ParameterIdentifier;
import mia.modmod.features.parameters.impl.EnumDataField;
import mia.modmod.features.parameters.impl.StringDataField;

public final class JoinButton extends Feature {
    private static StringDataField serverAddress;
    private static EnumDataField<DFIcons> joinIcon;

    public JoinButton(Categories category) {
        super(category, "Menu Join Button", "quickjoin", "Title menu join button");
        serverAddress = new StringDataField("Server Address", ParameterIdentifier.of(this, "server_address"), "mcdiamondfire.com:25565", true);
        joinIcon = new EnumDataField<>("Join Icon", ParameterIdentifier.of(this, "join_icon"), DFIcons.gay, true);
    }

    public static String getCustomServerAddress() { return serverAddress.getValue(); }
    public static DFIcons getJoinIcon() { return joinIcon.getValue(); }
}
