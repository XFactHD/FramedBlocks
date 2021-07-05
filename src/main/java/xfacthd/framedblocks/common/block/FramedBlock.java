package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.*;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;
import xfacthd.framedblocks.common.util.Utils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class FramedBlock extends Block implements IFramedBlock, IWaterLoggable
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
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        return handleUse(world, pos, player, hand, hit);
    }

    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        if (isWaterLoggable() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            world.getLiquidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        return super.updateShape(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) { return getLight(world, pos); }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
    {
        return getSound(state, world, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
    {
        return shapes.get(state);
    }

    @Override
    public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity)
    {
        return getCamoSlipperiness(state, world, pos, entity);
    }

    @Override
    public float getShadeBrightness(BlockState state, IBlockReader world, BlockPos pos) { return 1F; }

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
    public final boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedTileEntity(); }

    @Override
    public boolean canPlaceLiquid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
    {
        if (!isWaterLoggable()) { return false; }
        return IWaterLoggable.super.canPlaceLiquid(world, pos, state, fluid);
    }

    @Override
    public Fluid takeLiquid(IWorld world, BlockPos pos, BlockState state)
    {
        if (!isWaterLoggable()) { return Fluids.EMPTY; }
        return IWaterLoggable.super.takeLiquid(world, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public BlockType getBlockType() { return blockType; }

    protected BlockState withSlopeType(BlockState state, Direction side, Direction facing, Vector3d hitVec)
    {
        state = state.setValue(PropertyHolder.FACING_HOR, facing);

        Vector3d hitPoint = Utils.fraction(hitVec);
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

    protected BlockState withTop(BlockState state, Direction side, Vector3d hitVec)
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

    protected BlockState withWater(BlockState state, IWorldReader world, BlockPos pos)
    {
        FluidState fluidState = world.getFluidState(pos);
        return state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    protected boolean isWaterLoggable() { return blockType.supportsWaterLogging(); }
}