package io.github.xfacthd.framedblocks.common.block.stairs.standard;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class FramedStairsBlock extends StairBlock implements IFramedBlockInternal
{
    public static final StateMerger STATE_MERGER = new StairStateMerger();

    private final BlockType type;

    public FramedStairsBlock(BlockType type, Properties props)
    {
        super(FBContent.BLOCK_FRAMED_CUBE.value().defaultBlockState(), IFramedBlock.applyDefaultProperties(props, type));
        this.type = type;
        BlockUtils.configureStandardProperties(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        BlockUtils.addRequiredProperties(builder);
        builder.add(FramedProperties.SOLID, FramedProperties.STATE_LOCKED);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
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
    )
    {
        BlockState newState = updateShapeLockable(state, level, tickAccess, pos, side, adjPos, adjState, random, super::updateShape);
        if (newState == state)
        {
            updateCulling(level, pos);
        }
        return newState;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving)
    {
        updateCulling(level, pos);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state)
    {
        return useCamoOcclusionShapeForLightOcclusion(state);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state)
    {
        return getCamoOcclusionShape(state, null);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return getCamoVisualShape(state, level, pos, ctx);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, ctx))
        {
            return Shapes.empty();
        }
        return super.getShape(state, level, pos, ctx);
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return true;
    }

    @Override
    public BlockType getBlockType()
    {
        return type;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FACING, Direction.SOUTH);
    }

    @Override
    public Class<? extends Block> getJadeTargetClass()
    {
        return FramedStairsBlock.class;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FACING, Direction.SOUTH);
    }



    private static final class StairStateMerger implements StateMerger
    {
        private final StateMerger ignoringMerger = StateMerger.ignoring(WrapHelper.IGNORE_DEFAULT_LOCK);

        @Override
        public BlockState apply(BlockState state)
        {
            state = ignoringMerger.apply(state);
            StairsShape shape = state.getValue(SHAPE);
            if (shape == StairsShape.INNER_RIGHT)
            {
                state = state.setValue(SHAPE, StairsShape.INNER_LEFT)
                        .setValue(FACING, state.getValue(FACING).getClockWise());
            }
            else if (shape == StairsShape.OUTER_RIGHT)
            {
                state = state.setValue(SHAPE, StairsShape.OUTER_LEFT)
                        .setValue(FACING, state.getValue(FACING).getClockWise());
            }
            return state;
        }

        @Override
        public Set<Property<?>> getHandledProperties(Holder<Block> block)
        {
            return Utils.concat(
                    ignoringMerger.getHandledProperties(block),
                    Set.of(SHAPE)
            );
        }
    }
}
