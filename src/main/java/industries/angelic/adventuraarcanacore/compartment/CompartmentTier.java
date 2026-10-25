package industries.angelic.adventuraarcanacore.compartment;

public enum CompartmentTier {

    WOOD("wood", "Compartment", 4, 25),
    COPPER("copper", "CompartmentCopper", 6, 25),
    BRONZE("bronze", "CompartmentBronze", 8, 25),
    IRON("iron", "CompartmentIron", 4, 50),
    GOLD("gold", "CompartmentGold", 6, 50),
    DIAMOND("diamond", "CompartmentDiamond", 8, 50);

    public final String key;
    public final String texture;
    public final int tabs;
    public final int pageSize;

    CompartmentTier(String key, String texture, int tabs, int pageSize) {
        this.key = key;
        this.texture = texture;
        this.tabs = tabs;
        this.pageSize = pageSize;
    }

    public int capacity() {
        return tabs * pageSize;
    }

    public static CompartmentTier fromMetadata(int metadata) {
        return metadata >= 0 && metadata < values().length ? values()[metadata] : WOOD;
    }
}
