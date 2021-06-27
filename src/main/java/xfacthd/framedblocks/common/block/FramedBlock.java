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
        shapes = blockType.generateShapes(getStateContainer().getValidStates());
        if (blockType.supportsWaterLogging())
        {
            setDefaultState(getDefaultState().with(BlockStateProperties.WATERLOGGED, false));
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        return handleBlockActivated(world, pos, player, hand, hit);
    }

    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        if (isWaterLoggable() && state.get(BlockStateProperties.WATERLOGGED))
        {
            world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
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
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader world, BlockPos pos) { return 1F; }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        if (isWaterLoggable() && state.get(BlockStateProperties.WATERLOGGED))
        {
            return Fluids.WATER.getStillFluidState(false);
        }
        return Fluids.EMPTY.getDefaultState();
    }

    @Override
    public final boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedTileEntity(); }

    @Override
    public boolean canContainFluid(IBlockReader world, BlockPos pos, BlockState state, Fluid fluid)
    {
        if (!isWaterLoggable()) { return false; }
        return IWaterLoggable.super.canContainFluid(world, pos, state, fluid);
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
        state = state.with(PropertyHolder.FACING_HOR, facing);

        Vector3d hitPoint = Utils.fraction(hitVec);
        if (side.getAxis() != Direction.Axis.Y)
        {
            if (hitPoint.getY() < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (hitPoint.getY() > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        if (side == Direction.DOWN)
        {
            state = state.with(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.with(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM);
        }
        else
        {
            state = state.with(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);

            boolean xAxis = side.getAxis() == Direction.Axis.X;
            boolean positive = side.rotateYCCW().getAxisDirection() == Direction.AxisDirection.POSITIVE;
            double xz = xAxis ? hitPoint.getZ() : hitPoint.getX();

            if ((xz > .5D) == positive)
            {
                state = state.with(PropertyHolder.FACING_HOR, side.getOpposite().rotateY());
            }
            else
            {
                state = state.with(PropertyHolder.FACING_HOR, side.getOpposite());
            }
        }

        return state;
    }

    protected BlockState withTop(BlockState state, Direction side, Vector3d hitVec)
    {
        if (side == Direction.DOWN)
        {
            state = state.with(PropertyHolder.TOP, true);
        }
        else if (side == Direction.UP)
        {
            state = state.with(PropertyHolder.TOP, false);
        }
        else
        {
            double y = hitVec.y;
            y -= Math.floor(y);

            state = state.with(PropertyHolder.TOP, y >= .5D);
        }
        return state;
    }

    protected BlockState withWater(BlockState state, IWorldReader world, BlockPos pos)
    {
        FluidState fluidState = world.getFluidState(pos);
        return state.with(BlockStateProperties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    protected boolean isWaterLoggable() { return blockType.supportsWaterLogging(); }
}