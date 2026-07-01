package soi.asm;

import net.minecraft.item.ItemStack;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import soi.common.item.dao_zai.ItemStorageBag;

public class SlotShulkerBoxTransformer extends ClassVisitor
{
    private static final String CHECKER_DESC = "(Lnet/minecraft/item/ItemStack;)Z";

    public SlotShulkerBoxTransformer(ClassVisitor cv) { super(Opcodes.ASM5, cv); }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String desc, String signature, String[] exceptions)
    {
        MethodVisitor originalMV = super.visitMethod(access, methodName, desc, signature, exceptions);
        boolean target = ("isItemValid".equals(methodName) || "func_75208_a".equals(methodName)) && desc.equals(CHECKER_DESC);
        if (target) return new InjectPreCheckMV(originalMV);
        return originalMV;
    }

    private static class InjectPreCheckMV extends MethodVisitor
    {
        public InjectPreCheckMV(MethodVisitor mv) { super(Opcodes.ASM5, mv); }

        @Override
        public void visitCode()
        {
            Label pass = new Label();

            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "soi/asm/SlotShulkerBoxTransformer",
                    "isForbiddenItem",
                    CHECKER_DESC,
                    false
            );
            mv.visitJumpInsn(Opcodes.IFEQ, pass);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitInsn(Opcodes.IRETURN);
            mv.visitLabel(pass);

            super.visitCode();
        }
    }

    public static boolean isForbiddenItem(ItemStack stack)
    {
        if (stack.isEmpty()) return false;
        return stack.getItem() instanceof ItemStorageBag;
    }
}