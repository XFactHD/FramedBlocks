package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.*;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import java.util.Map;

@SuppressWarnings("deprecation")
public class FramedBlock extends Block implements IFramedBlock, IWaterLoggable
{
    private final BlockType blockType;
    private final Map<BlockState, VoxelShape> shapes;

    public FramedBlock(String name, BlockType blockType) { this(name, blockType, IFramedBlock.createProperties()); }

    protected FramedBlock(String name, BlockType blockType, Properties props)
    {
        super(props);
        setRegistryName(FramedBlocks.MODID, name);

        this.blockType = blockType;
        shapes = blockType.generateShapes(getStateContainer().getValidStates());
        if (blockType != BlockType.FRAMED_CUBE)
        {
            setDefaultState(getDefaultState().with(BlockStateProperties.WATERLOGGED, false));
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        return handleBlockActivated(world, pos, player, hand);
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
    public IFluidState getFluidState(BlockState state)
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
        if (isWaterLoggable()) { return false; }
        return IWaterLoggable.super.canContainFluid(world, pos, state, fluid);
    }

    @Override
    public BlockType getBlockType() { return blockType; }

    protected BlockState withTop(BlockState state, Direction side, Vec3d hitVec)
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

    protected BlockState withWater(BlockState state, IWorldReader water, BlockPos pos)
    {
        IFluidState fluidState = water.getFluidState(pos);
        return state.with(BlockStateProperties.WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    protected boolean isWaterLoggable() { return blockType != BlockType.FRAMED_CUBE; }
}