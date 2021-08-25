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

    @Override //TODO: Think about removing this in 1.17 in favor of adding all four cardinal directions to the double panel
    protected boolean placeBlock(BlockPlaceContext context, BlockState state)
    {
        boolean success = super.placeBlock(context, state);
        if (!success) { return false; }

        if (this != FBContent.blockFramedDoublePanel.get().asItem()) { return true; }

        Direction dir = context.getHorizontalDirection();
        if (dir == Direction.SOUTH || dir == Direction.WEST)
        {
            CompoundTag teTag = context.getItemInHand().getOrCreateTagElement("BlockEntityTag");

            CompoundTag stateTag = teTag.getCompound("camo_state");
            teTag.put("camo_state", teTag.getCompound("camo_state_two"));
            teTag.put("camo_state_two", stateTag);

            CompoundTag stackTag = teTag.getCompound("camo_stack");
            teTag.put("camo_stack", teTag.getCompound("camo_stack_two"));
            teTag.put("camo_stack_two", stackTag);

        }
        return true;
    }

    @Override
    protected boolean allowdedIn(CreativeModeTab group) { return false; }
}