package io.github.xfacthd.framedblocks.api.block;

import io.github.xfacthd.framedblocks.api.shapes.ShapeLookup;
import io.github.xfacthd.framedblocks.api.util.Utils;
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
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class AbstractFramedBlock extends Block implements IFramedBlock, SimpleWaterloggedBlock
{
    private final IBlockType blockType;
    protected final ShapeLookup shapes;

    public AbstractFramedBlock(IBlockType blockType, Properties props)
    {
        this.blockType = blockType;
        super(props);
        this.shapes = ShapeLookup.of(this);

        BlockUtils.configureStandardProperties(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        BlockUtils.addStandardProperties(this, builder);
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
        updateCulling(level, pos);
        if (isWaterLoggable() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, tickAccess, pos, side, adjPos, adjState, random);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean isMoving)
    {
        updateCulling(level, pos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, ctx))
        {
            return Shapes.empty();
        }
        return shapes.getShape(state);
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state)
    {
        return useCamoOcclusionShapeForLightOcclusion(state);
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state)
    {
        return getCamoOcclusionShape(state, shapes);
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
    protected FluidState getFluidState(BlockState state)
    {
        if (isWaterLoggable() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            return Fluids.WATER.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public boolean canPlaceLiquid(@Nullable LivingEntity entity, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid)
    {
        return isWaterLoggable() && SimpleWaterloggedBlock.super.canPlaceLiquid(entity, level, pos, state, fluid);
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState)
    {
        return isWaterLoggable() && SimpleWaterloggedBlock.super.placeLiquid(pLevel, pPos, pState, pFluidState);
    }

    @Override
    public ItemStack pickupBlock(@Nullable LivingEntity entity, LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (!isWaterLoggable())
        {
            return ItemStack.EMPTY;
        }
        return SimpleWaterloggedBlock.super.pickupBlock(entity, level, pos, state);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return super.getDrops(state, getCamoDrops(builder));
    }

    /**
     * Return true if the given {@link BlockState} occludes the full area of the beacon beam and
     * can therefore tint the beam
     */
    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return shapes.occludesBeaconBeam(state);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type)
    {
        return state.is(Utils.GROUP_FULL_CUBE) && super.isPathfindable(state, type);
    }

    @Override
    public IBlockType getBlockType()
    {
        return blockType;
    }

    protected final boolean isWaterLoggable()
    {
        return blockType.supportsWaterLogging();
    }
}
