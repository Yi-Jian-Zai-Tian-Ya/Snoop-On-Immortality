package soi.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodNode;

public class TransformerLocaleFont implements IClassTransformer
{
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (!transformedName.equals("net.minecraft.client.resources.Locale")) return basicClass;

        ClassNode cn = new ClassNode();
        new ClassReader(basicClass).accept(cn, 0);


        for (MethodNode mn : cn.methods)
        {
            String mapped = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(cn.name, mn.name, mn.desc);
            if (!mapped.equals("func_135024_b") && !mapped.equals("checkUnicode")) continue;

            for (AbstractInsnNode insn : mn.instructions.toArray()) { if (insn.getOpcode() == Opcodes.PUTFIELD) { mn.instructions.insertBefore(insn, new InsnNode(Opcodes.RETURN)); break; } }
            break;

        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        cn.accept(cw);
        return cw.toByteArray();
    }
}