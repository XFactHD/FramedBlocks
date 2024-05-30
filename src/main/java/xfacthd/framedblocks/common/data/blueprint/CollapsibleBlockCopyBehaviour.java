package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xfacthd.framedblocks.api.blueprint.BlueprintData;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.FramedCollapsibleBlockEntity;
import xfacthd.framedblocks.common.data.component.CollapsibleBlockData;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.data.PropertyHolder;

public final class CollapsibleBlockCopyBehaviour extends DummyDataHandlingCopyBehaviour<CollapsibleBlockData>
{
    public CollapsibleBlockCopyBehaviour()
    {
        super(FBContent.DC_TYPE_COLLAPSIBLE_BLOCK_DATA.value(), CollapsibleBlockData.EMPTY);
    }

    @Override
    public void postProcessPaste(Level level, BlockPos pos, Player player, BlueprintData data, ItemStack dummyStack)
    {
        if (level.getBlockEntity(pos) instanceof FramedCollapsibleBlockEntity be)
        {
            NullableDirection face = NullableDirection.fromDirection(be.getCollapsedFace());
            level.setBlockAndUpdate(pos, be.getBlockState().setValue(PropertyHolder.NULLABLE_FACE, face));
        }
    }
}
