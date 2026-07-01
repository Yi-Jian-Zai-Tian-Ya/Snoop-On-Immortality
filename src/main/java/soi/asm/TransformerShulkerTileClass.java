package soi.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class TransformerShulkerTileClass implements IClassTransformer
{
    private static final String DEV = "net.minecraft.tileentity.TileEntityShulkerBox";
    private static final String SRG = "net.minecraft.tileentity.aaz";

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes)
    {
        if(!DEV.equals(name) && !SRG.equals(name)) return bytes;

        ClassReader cr = new ClassReader(bytes);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        ShulkerTileCanInsertTransformer cv = new ShulkerTileCanInsertTransformer(cw);
        cr.accept(cv, ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }
}