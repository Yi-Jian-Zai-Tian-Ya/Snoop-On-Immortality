/*
  Source from Baubles
 */

package soi.api.dao_zai;

public enum DaoZaiType
{
    BAG(0, 1, 2),
    GONG_FA(3, 4, 5, 6),
    FA_BAO(7, 8, 9, 10),
    DAO_ZAI(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    int[] validSlots;

    private DaoZaiType(int ... validSlots) { this.validSlots = validSlots; }

    public boolean hasSlot(int slot) { for (int s : validSlots) if (s == slot) return true; return false; }

    public boolean onlyForPlayer() { return this == GONG_FA; }

    public int[] getValidSlots() { return validSlots; }
}