package io.github.xfacthd.framedblocks.common.compat.diagonalblocks;

import fuzs.diagonalblocks.api.v2.block.DiagonalBlock;
import fuzs.diagonalblocks.api.v2.block.DiagonalFenceBlock;
import fuzs.diagonalblocks.api.v2.block.StarCollisionBlock;
import fuzs.diagonalblocks.api.v2.util.EightWayDirection;
import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.ShapeLockableBlock;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.DirUtils;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public final class FramedDiagonalFenceBlock extends DiagonalFenceBlock implements IFramedBlockInternal, ShapeLockableBlock {
    public FramedDiagonalFenceBlock(BlockBehaviour.Properties props) {
        super(props);
        BlockUtils.configureStandardProperties(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        BlockUtils.addStandardProperties(this, builder);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = handleUse(state, level, pos, player, hand, hit);
        return result.consumesAction() ? result : super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random
    ) {
        BlockState newState = updateShapeLockable(state, level, tickAccess, pos, side, adjPos, adjState, random, super::updateShape);
        if (newState == state) {
            updateCulling(level, pos);
        }
        return newState;
    }

    @Override
    protected boolean shouldChangedStateKeepBlockEntity(BlockState state) {
        return DiagonalBlocksCompat.isFramedFence(state);
    }

    @Override
    public boolean connectsTo(BlockState adjState, boolean sideSolid, Direction adjSide) {
        if (!DirUtils.isY(adjSide) && DiagonalBlocksCompat.isFramedFence(adjState) && adjState.getValue(FramedProperties.STATE_LOCKED)) {
            BooleanProperty prop = CrossCollisionBlock.PROPERTY_BY_DIRECTION.get(adjSide);
            if (!adjState.getValue(prop)) {
                return false;
            }
        }
        return super.connectsTo(adjState, sideSolid, adjSide);
    }

    @Override
    public @Nullable BlockState updateIndirectNeighborDiagonalProperty(BlockState state, LevelAccessor level, BlockPos pos, EightWayDirection dir) {
        if (state.getValue(FramedProperties.STATE_LOCKED)) {
            return null;
        }
        return super.updateIndirectNeighborDiagonalProperty(state, level, pos, dir);
    }

    @Override
    public boolean attachesDiagonallyTo(BlockState adjState, EightWayDirection adjDir) {
        if (adjState.getBlock() == this && adjState.getValue(FramedProperties.STATE_LOCKED)) {
            BooleanProperty prop = StarCollisionBlock.PROPERTY_BY_DIRECTION.get(adjDir);
            if (!adjState.getValue(prop)) {
                return false;
            }
        }
        return super.attachesDiagonallyTo(adjState, adjDir);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        updateCulling(level, pos);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state) {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return state;
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.FRAMED_FENCE;
    }

    @Override
    public Set<Property<?>> getPropertiesToCopy() {
        return Set.of(DiagonalBlock.NORTH, DiagonalBlock.EAST, DiagonalBlock.SOUTH, DiagonalBlock.WEST, NORTH_EAST, SOUTH_EAST, SOUTH_WEST, NORTH_WEST);
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState().setValue(CrossCollisionBlock.EAST, true).setValue(CrossCollisionBlock.WEST, true);
    }
}
