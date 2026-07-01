package soi.asm;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import soi.common.item.dao_zai.ItemStorageBag;

public class ShulkerTileCanInsertTransformer extends ClassVisitor
{
    private static final String CAN_INSERT_DESC = "(ILnet/minecraft/item/ItemStack;Lnet/minecraft/util/EnumFacing;)Z";
    private static final String SELF = "soi/asm/ShulkerTileCanInsertTransformer";
    private static final String CHECK_METHOD = "isForbiddenBag";

    public ShulkerTileCanInsertTransformer(ClassVisitor cv) { super(Opcodes.ASM5, cv); }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex)
    {
        MethodVisitor mv = super.visitMethod(access, name, desc, sig, ex);
        boolean target = ("canInsertItem".equals(name) || "func_180463_a".equals(name)) && desc.equals(CAN_INSERT_DESC);
        if(target) return new InjectCheckMV(mv);
        return mv;
    }

    private static class InjectCheckMV extends MethodVisitor
    {
        public InjectCheckMV(MethodVisitor mv) { super(Opcodes.ASM5, mv); }

        @Override
        public void visitCode()
        {
            Label pass = new Label();
            mv.visitVarInsn(Opcodes.ALOAD, 2);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, SELF, CHECK_METHOD, "(Lnet/minecraft/item/ItemStack;)Z", false);
            mv.visitJumpInsn(Opcodes.IFEQ, pass);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitInsn(Opcodes.IRETURN);
            mv.visitLabel(pass);

            super.visitCode();
        }
    }

    public static boolean isForbiddenBag(ItemStack stack)
    {
        if(stack.isEmpty()) return false;
        Item item = stack.getItem();
        return item instanceof ItemStorageBag;
    }
}