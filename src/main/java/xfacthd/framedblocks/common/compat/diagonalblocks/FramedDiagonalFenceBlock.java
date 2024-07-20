package xfacthd.framedblocks.common.compat.diagonalblocks;

import fuzs.diagonalblocks.api.v2.EightWayDirection;
import fuzs.diagonalblocks.api.v2.impl.StarCollisionBlock;
import fuzs.diagonalblocks.neoforge.api.v2.impl.NeoForgeDiagonalFenceBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.List;

public final class FramedDiagonalFenceBlock extends NeoForgeDiagonalFenceBlock implements IFramedBlock
{
    public FramedDiagonalFenceBlock(Block block)
    {
        super(block);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.STATE_LOCKED, false)
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.GLOWING, FramedProperties.STATE_LOCKED, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        ItemInteractionResult result = handleUse(state, level, pos, player, hand, hit);
        return result.consumesAction() ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState facingState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos facingPos
    )
    {
        BlockState newState = updateShapeLockable(
                state, level, currentPos,
                () -> super.updateShape(state, facing, facingState, level, currentPos, facingPos)
        );

        if (newState == state)
        {
            updateCulling(level, currentPos);
        }
        return newState;
    }

    @Override
    public boolean connectsTo(BlockState adjState, boolean sideSolid, Direction adjSide)
    {
        if (!Utils.isY(adjSide) && DiagonalBlocksCompat.isFramedFence(adjState) && adjState.getValue(FramedProperties.STATE_LOCKED))
        {
            BooleanProperty prop = CrossCollisionBlock.PROPERTY_BY_DIRECTION.get(adjSide);
            if (!adjState.getValue(prop))
            {
                return false;
            }
        }
        return super.connectsTo(adjState, sideSolid, adjSide);
    }

    @Override
    public BlockState updateIndirectNeighborDiagonalProperty(BlockState state, LevelAccessor level, BlockPos pos, EightWayDirection dir)
    {
        if (state.getValue(FramedProperties.STATE_LOCKED))
        {
            return null;
        }
        return super.updateIndirectNeighborDiagonalProperty(state, level, pos, dir);
    }

    @Override
    public boolean attachesDiagonallyTo(BlockState adjState, EightWayDirection adjDir)
    {
        if (adjState.getBlock() == this && adjState.getValue(FramedProperties.STATE_LOCKED))
        {
            BooleanProperty prop = StarCollisionBlock.PROPERTY_BY_DIRECTION.get(adjDir);
            if (!adjState.getValue(prop))
            {
                return false;
            }
        }
        return super.attachesDiagonallyTo(adjState, adjDir);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        updateCulling(level, pos);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public BlockType getBlockType()
    {
        return BlockType.FRAMED_FENCE;
    }

    @Override
    @Nullable
    public BlockState getItemModelSource()
    {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(CrossCollisionBlock.EAST, true).setValue(CrossCollisionBlock.WEST, true);
    }
}
