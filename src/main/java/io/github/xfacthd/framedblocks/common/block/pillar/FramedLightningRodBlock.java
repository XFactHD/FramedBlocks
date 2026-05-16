package io.github.xfacthd.framedblocks.common.block.pillar;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.item.placement.PropertyLabels;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
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
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class FramedLightningRodBlock extends LightningRodBlock implements IFramedBlockInternal {
    public FramedLightningRodBlock(Properties props) {
        super(props);
        BlockUtils.configureStandardProperties(this);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.COPYCAT_STYLE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        BlockUtils.addStandardProperties(this, builder);
        builder.add(FramedProperties.COPYCAT_STYLE);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.is(FramedConstants.Objects.FRAMED_HAMMER.value())) {
            if (!level.isClientSide()) {
                state = state.setValue(FramedProperties.COPYCAT_STYLE, !state.getValue(FramedProperties.COPYCAT_STYLE));
                level.setBlock(pos, state, Block.UPDATE_ALL);
            }
            return true;
        }
        return false;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    protected BlockState updateShape(
            BlockState state,
            LevelReader level,
            ScheduledTickAccess tickAccess,
            BlockPos pos,
            Direction side,
            BlockPos adjPos,
            BlockState adjState,
            RandomSource random
    ) {
        updateCulling(level, pos);
        return super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving) {
        updateCulling(level, pos);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return getCamoOcclusionShape(state, null);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return getCamoVisualShape(state, level, pos, ctx);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode) {
        return direction.cycle(state, BlockStateProperties.FACING);
    }

    @Override
    public BlockType getBlockType() {
        return BlockType.FRAMED_LIGHTNING_ROD;
    }

    @Override
    public @Nullable BlockState getItemModelSource() {
        return defaultBlockState();
    }

    @Override
    public StateCycleSpec createStateCycleSpec() {
        return StateCycleSpec.builder(this)
                .property(FACING, PropertyLabels.FACING)
                .build();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state) {
        return defaultBlockState();
    }
}
