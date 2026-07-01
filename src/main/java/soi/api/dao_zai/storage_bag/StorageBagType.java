/*
  Source from Iron Backpacks
 */

package soi.api.dao_zai.storage_bag;

import net.minecraft.nbt.NBTTagByteArray;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public class StorageBagType extends IForgeRegistryEntry.Impl<StorageBagType> implements INBTSerializable<NBTTagByteArray>
{
    @Nonnull private final String type;
    @Nonnegative private final int meta;
    @Nonnegative private int cols;
    @Nonnegative private int rows;

    public StorageBagType(@Nonnull String type, @Nonnegative int meta, @Nonnegative int cols, @Nonnegative int rows) { this.type = type; this.meta = meta; this.cols = cols; this.rows = rows; setRegistryName(type); }
    public StorageBagType() { this("Inferior", 0, 9, 4); }

    @Nonnull public String getType() { return type; }
    @Nonnegative public int getMeta() { return meta; }
    @Nonnegative public int getCols() { return cols; }
    @Nonnegative public int getRows() { return rows; }
    @Nonnegative public int getTotalSize() { return cols * rows; }

    @Override @Nonnull public NBTTagByteArray serializeNBT() { return new NBTTagByteArray(new byte[]{(byte) cols, (byte) rows}); }
    @Override public void deserializeNBT(@Nonnull NBTTagByteArray nbt) { cols = nbt.getByteArray()[0]; rows = nbt.getByteArray()[1]; }
}