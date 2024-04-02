package xfacthd.framedblocks.common.data.blueprint;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.camo.CamoContainer;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.doubled.FramedDoubleBlockEntity;

import java.util.Optional;

public final class DoublePanelCopyBehaviour extends DoubleBlockCopyBehaviour
{
    @Override
    public Optional<ItemStack> getBlockItem()
    {
        return Optional.of(new ItemStack(FBContent.BLOCK_FRAMED_PANEL.value(), 2));
    }

    @Override
    public void postProcessPaste(
            Level level, BlockPos pos, Player player, CompoundTag blueprintData, ItemStack dummyStack
    )
    {
        BlockState state = level.getBlockState(pos);
        if (state.getValue(FramedProperties.FACING_NE) != player.getDirection())
        {
            if (level.getBlockEntity(pos) instanceof FramedDoubleBlockEntity be)
            {
                //Mirror camos
                CamoContainer<?, ?> camoOne = be.getCamo();
                CamoContainer<?, ?> camoTwo = be.getCamoTwo();
                be.setCamo(camoTwo, false);
                be.setCamo(camoOne, true);
            }
        }
    }
}
