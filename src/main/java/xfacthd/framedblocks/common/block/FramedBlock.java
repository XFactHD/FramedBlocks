package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.blockentity.FramedBlockEntity;
import xfacthd.framedblocks.common.util.Utils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class FramedBlock extends Block implements IFramedBlock, SimpleWaterloggedBlock
{
    private final BlockType blockType;
    private final Map<BlockState, VoxelShape> shapes;

    public FramedBlock(BlockType blockType) { this(blockType, IFramedBlock.createProperties()); }

    protected FramedBlock(BlockType blockType, Properties props)
    {
        super(props);

        this.blockType = blockType;
        shapes = blockType.generateShapes(getStateDefinition().getPossibleStates());
        if (blockType.supportsWaterLogging())
        {
            registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return handleUse(level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        if (isWaterLoggable() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            level.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) { return getLight(level, pos); }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity)
    {
        return getCamoSound(state, level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return shapes.get(state);
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        return getCamoSlipperiness(state, level, pos, entity);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) { return 1F; }

    @Override
    public float getExplosionResistance(BlockState state, BlockGetter level, BlockPos pos, Explosion explosion)
    {
        return getCamoExplosionResistance(state, level, pos, explosion);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction face)
    {
        return isCamoFlammable(level, pos, face);
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction face)
    {
        return getCamoFlammability(level, pos, face);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        if (isWaterLoggable() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            return Fluids.WATER.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedBlockEntity(pos, state); }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid)
    {
        if (!isWaterLoggable()) { return false; }
        return SimpleWaterloggedBlock.super.canPlaceLiquid(level, pos, state, fluid);
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (!isWaterLoggable()) { return ItemStack.EMPTY; }
        return SimpleWaterloggedBlock.super.pickupBlock(level, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public BlockType getBlockType() { return blockType; }

    protected BlockState withSlopeType(BlockState state, Direction side, Direction facing, Vec3 hitVec)
    {
        state = state.setValue(PropertyHolder.FACING_HOR, facing);

        Vec3 hitPoint = Utils.fraction(hitVec);
        if (side.getAxis() != Direction.Axis.Y)
        {
            if (hitPoint.y() < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (hitPoint.y() > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        if (side == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM);
        }
        else
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);

            boolean xAxis = side.getAxis() == Direction.Axis.X;
            boolean positive = side.getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.z() : hitPoint.x();

            if ((xz > .5D) == positive)
            {
                state = state.setValue(PropertyHolder.FACING_HOR, side.getOpposite().getClockWise());
            }
            else
            {
                state = state.setValue(PropertyHolder.FACING_HOR, side.getOpposite());
            }
        }

        return state;
    }

    protected BlockState withCornerType(BlockState state, BlockPlaceContext context, Direction side, Vec3 hitPoint, Direction facing)
    {
        state = state.setValue(PropertyHolder.FACING_HOR, facing);

        if (side == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.BOTTOM);
        }
        else
        {
            boolean xAxis = context.getClickedFace().getAxis() == Direction.Axis.X;
            boolean positive = context.getClickedFace().getCounterClockWise().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.z() : hitPoint.x();
            double y = hitPoint.y();

            CornerType type;
            if ((xz > .5D) == positive)
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_RIGHT : CornerType.HORIZONTAL_BOTTOM_RIGHT;
            }
            else
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_LEFT : CornerType.HORIZONTAL_BOTTOM_LEFT;
            }
            state = state.setValue(PropertyHolder.CORNER_TYPE, type);
        }
        return state;
    }

    protected BlockState withTop(BlockState state, Direction side, Vec3 hitVec)
    {
        if (side == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.TOP, true);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(PropertyHolder.TOP, false);
        }
        else
        {
            double y = hitVec.y;
            y -= Math.floor(y);

            state = state.setValue(PropertyHolder.TOP, y >= .5D);
        }
        return state;
    }

    protected BlockState withWater(BlockState state, LevelReader level, BlockPos pos)
    {
        FluidState fluidState = level.getFluidState(pos);
        return state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    protected boolean isWaterLoggable() { return blockType.supportsWaterLogging(); }
}