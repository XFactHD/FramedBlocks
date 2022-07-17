package xfacthd.framedblocks.client.data.ghost;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.IModelData;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.common.blockentity.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CollapseFace;

public class CollapsibleBlockGhostRenderBehaviour implements GhostRenderBehaviour
{
    @Override
    @Nullable
    public BlockState getRenderState(ItemStack stack, ItemStack proxiedStack, BlockHitResult hit, BlockPlaceContext ctx, BlockState hitState, boolean secondPass)
    {
        BlockState state = GhostRenderBehaviour.super.getRenderState(stack, proxiedStack, hit, ctx, hitState, secondPass);
        //noinspection ConstantConditions
        if (state != null && stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
        {
            int faceIdx = stack.getTag().getCompound("BlockEntityTag").getInt("face");
            Direction face = faceIdx == -1 ? null : Direction.from3DDataValue(faceIdx);
            state = state.setValue(PropertyHolder.COLLAPSED_FACE, CollapseFace.fromDirection(face));
        }
        return state;
    }

    @Override
    public IModelData appendModelData(ItemStack stack, @Nullable ItemStack proxiedStack, BlockPlaceContext ctx, BlockState renderState, boolean secondPass, IModelData data)
    {
        //noinspection ConstantConditions
        if (stack.hasTag() && stack.getTag().contains("BlockEntityTag"))
        {
            int offsets = stack.getTag().getCompound("BlockEntityTag").getInt("offsets");
            data.setData(FramedCollapsibleBlockEntity.OFFSETS, offsets);
        }
        return data;
    }
}
