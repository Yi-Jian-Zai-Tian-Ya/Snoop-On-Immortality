package soi.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class TransformerShulkerClass implements IClassTransformer
{
    private static final String DEV_NAME = "net.minecraft.inventory.SlotShulkerBox";
    private static final String SRG_NAME = "net.minecraft.inventory.akz";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (!DEV_NAME.equals(name) && !SRG_NAME.equals(name)) return basicClass;

        ClassReader cr = new ClassReader(basicClass);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
        SlotShulkerBoxTransformer cv = new SlotShulkerBoxTransformer(cw);
        cr.accept(cv, ClassReader.SKIP_FRAMES);
        return cw.toByteArray();
    }
}