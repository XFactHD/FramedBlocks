package io.github.xfacthd.framedblocks.api.camo.block;

import io.github.xfacthd.framedblocks.api.camo.CamoCraftingHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.state.BlockState;

final class BlockCamoCraftingHandler implements CamoCraftingHandler<SimpleBlockCamoContainer> {
    private final SimpleBlockCamoContainerFactory factory;

    public BlockCamoCraftingHandler(SimpleBlockCamoContainerFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean canApply(ItemStack stack, boolean consume) {
        if (stack.getItem() instanceof BlockItem item) {
            BlockState state = item.getBlock().defaultBlockState();
            return factory.isValidBlock(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null);
        }
        return false;
    }

    @Override
    public SimpleBlockCamoContainer apply(ItemStack stack, boolean consume) {
        if (stack.getItem() instanceof BlockItem item) {
            BlockState state = item.getBlock().defaultBlockState();
            if (factory.isValidBlock(state, EmptyBlockGetter.INSTANCE, BlockPos.ZERO, null)) {
                return new SimpleBlockCamoContainer(item.getBlock().defaultBlockState(), factory);
            }
        }
        throw new IllegalStateException("CamoCraftingHandler#apply() called without CamoCraftingHandler#canApply() check");
    }

    @Override
    public ItemStack getRemainder(ItemStack stack, boolean consume) {
        return consume ? ItemStack.EMPTY : stack.copyWithCount(1);
    }
}
