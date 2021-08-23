package xfacthd.framedblocks.common.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;

public class FramedDoubleBlockItem extends BlockItem
{
    public FramedDoubleBlockItem(AbstractFramedDoubleBlock block)
    {
        super(block, new Properties());
        //noinspection ConstantConditions
        setRegistryName(block.getRegistryName());
    }

    @Override //TODO: Think about removing this in 1.17 in favor of adding all four cardinal directions to the double panel
    protected boolean placeBlock(BlockItemUseContext context, BlockState state)
    {
        boolean success = super.placeBlock(context, state);
        if (!success) { return false; }

        if (this != FBContent.blockFramedDoublePanel.get().asItem()) { return true; }

        Direction dir = context.getPlacementHorizontalFacing();
        if (dir == Direction.SOUTH || dir == Direction.WEST)
        {
            CompoundNBT teTag = context.getItem().getOrCreateChildTag("BlockEntityTag");

            CompoundNBT stateTag = teTag.getCompound("camo_state");
            teTag.put("camo_state", teTag.getCompound("camo_state_two"));
            teTag.put("camo_state_two", stateTag);

            CompoundNBT stackTag = teTag.getCompound("camo_stack");
            teTag.put("camo_stack", teTag.getCompound("camo_stack_two"));
            teTag.put("camo_stack_two", stackTag);

        }
        return true;
    }

    @Override
    protected boolean isInGroup(ItemGroup group) { return false; }
}