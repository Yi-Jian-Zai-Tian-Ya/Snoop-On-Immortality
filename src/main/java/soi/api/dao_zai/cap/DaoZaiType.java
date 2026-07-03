package soi.api.dao_zai.cap;

public enum DaoZaiType
{
    Bag(3),
    GongFa(3),
    FaBao(3);

    private final int slots;

    DaoZaiType(int slots) { this.slots = slots; }

    public int getBaseSlots() { return slots; }

    public boolean onlyForPlayer() { return this == GongFa; }

    public static DaoZaiType get(String name)
    {
        for (DaoZaiType type : values()) if (type.name().equalsIgnoreCase(name)) return type;
        return null;
    }
}