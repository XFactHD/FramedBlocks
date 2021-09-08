package xfacthd.framedblocks.common.item;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
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

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state)
    {
        boolean success = super.placeBlock(context, state);
        if (!success) { return false; }

        if (this != FBContent.blockFramedDoublePanel.get().asItem()) { return true; }

        Direction dir = context.getHorizontalDirection();
        if (dir == Direction.SOUTH || dir == Direction.WEST)
        {
            CompoundTag beTag = context.getItemInHand().getOrCreateTagElement("BlockEntityTag");

            CompoundTag stateTag = beTag.getCompound("camo_state");
            beTag.put("camo_state", beTag.getCompound("camo_state_two"));
            beTag.put("camo_state_two", stateTag);

            CompoundTag stackTag = beTag.getCompound("camo_stack");
            beTag.put("camo_stack", beTag.getCompound("camo_stack_two"));
            beTag.put("camo_stack_two", stackTag);
        }
        return true;
    }

    @Override
    protected boolean allowdedIn(CreativeModeTab group) { return false; }
}