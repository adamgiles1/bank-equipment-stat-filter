package bankequipmentstatfilter;

public enum EquipmentStat {
    STAB_ATTACK("Stab Attack"),
    SLASH_ATTACK("Slash Attack"),
    CRUSH_ATTACK("Crush Attack"),
    MAGIC_ATTACK("Magic Attack"),
    RANGE_ATTACK("Range Attack"),
    STAB_DEFENCE("Stab Defence"),
    SLASH_DEFENCE("Slash Defence"),
    CRUSH_DEFENCE("Crush Defence"),
    MAGIC_DEFENCE("Magic Defence"),
    RANGE_DEFENCE("Range Defence"),
    MELEE_STRENGTH("Melee Strength"),
    RANGE_STRENGTH("Range Strength"),
    MAGIC_DAMAGE("Magic Damage"),
    PRAYER("Prayer");

    EquipmentStat(String displayName) {
        this.displayName = displayName;
    }

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }
}
