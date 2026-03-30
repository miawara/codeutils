package mia.modmod.features.impl.moderation.tracker.punishments;

import mia.modmod.Mod;

import java.util.Optional;

public class PunishmentData {
    private final String offender;
    private final ServerPunishmentType punishmentType;
    private final String issuer;
    private final String reason;
    private final boolean isActive;
    private final ChronoTimestamp chronoTimestamp;
    private String expirationString = null;

    public PunishmentData(String offender, String punishmentData, String issuer, String reason, String activeString, ChronoTimestamp chronoTimestamp) {
        this.offender = punishmentData;
        this.punishmentType =  parsePunishData(punishmentData);
        this.issuer = issuer;
        this.reason = reason;
        this.isActive = activeString.equals("Active");
        this.chronoTimestamp = chronoTimestamp;
    }

    public String offender() { return offender; }
    public ServerPunishmentType punishmentType() { return punishmentType; }
    public String issuer() { return issuer; }
    public String reason() { return reason; }
    public boolean isActive() { return isActive; }
    public ChronoTimestamp chronoTimestamp() { return chronoTimestamp; }

    public Optional<String> getExpirationString() { return expirationString != null ? Optional.of(expirationString) : Optional.empty(); }
    public void setExpirationString(String expirationString) { this.expirationString = expirationString; }


    private static ServerPunishmentType parsePunishData(String punishmentData) {
        return switch (punishmentData) {
            case "warned" -> ServerPunishmentType.WARN;
            case "muted" -> ServerPunishmentType.MUTE;
            case "banned" -> ServerPunishmentType.BAN;
            case "kicked" -> ServerPunishmentType.KICK;
            default -> {
                Mod.error("Error while parsing punishmentData, \"" + punishmentData + "\" is not a valid type");
                yield null;
            }
        };
    }
}
