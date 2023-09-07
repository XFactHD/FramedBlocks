package xfacthd.framedblocks.common.block.special;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.*;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.blockentity.special.PoweredFramingSawBlockEntity;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.menu.PoweredFramingSawMenu;

public class PoweredFramingSawBlock extends FramingSawBlock implements EntityBlock
{
    public static final Component POWERED_SAW_MENU_TITLE = Utils.translate("title", "powered_framing_saw");

    public PoweredFramingSawBlock()
    {
        registerDefaultState(defaultBlockState().setValue(PropertyHolder.ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.ACTIVE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.block();
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return FramingSawBlock.SHAPE;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved)
    {
        if (newState.getBlock() != state.getBlock() && level.getBlockEntity(pos) instanceof PoweredFramingSawBlockEntity be)
        {
            be.dropContents(stack -> popResource(level, pos, stack));
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new PoweredFramingSawBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (!level.isClientSide())
        {
            return Utils.createBlockEntityTicker(
                    type, FBContent.BE_TYPE_POWERED_FRAMING_SAW.get(), PoweredFramingSawBlockEntity::tick
            );
        }
        return null;
    }

    @Override
    protected Component getSawMenuTitle()
    {
        return POWERED_SAW_MENU_TITLE;
    }

    @Override
    protected AbstractContainerMenu createSawMenu(int containerId, Inventory inventory, Level level, BlockPos pos)
    {
        return new PoweredFramingSawMenu(containerId, inventory, level, pos);
    }
}
