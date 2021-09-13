package xfacthd.framedblocks.common.block;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RailShape;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.tileentity.FramedTileEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

//FIXME: placing a rail next to this that would set an invalid RailShape causes an IllegalArgumentException because RailState
//       doesn't check if the shape property supports the new shape
@SuppressWarnings("deprecation")
public class FramedRailSlopeBlock extends AbstractRailBlock implements IFramedBlock
{
    private final Map<BlockState, VoxelShape> shapes;

    public FramedRailSlopeBlock()
    {
        super(true, IFramedBlock.createProperties());
        shapes = getBlockType().generateShapes(getStateContainer().getValidStates());
        setDefaultState(getDefaultState().with(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE, PropertyHolder.ASCENDING_RAIL_SHAPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        RailShape shape = shapeFromDirection(context.getPlacementHorizontalFacing());

        FluidState fluidState = context.getWorld().getFluidState(context.getPos());
        boolean waterlogged = fluidState.getFluid() == Fluids.WATER;

        return getDefaultState()
                .with(PropertyHolder.FACING_HOR, context.getPlacementHorizontalFacing())
                .with(PropertyHolder.ASCENDING_RAIL_SHAPE, shape)
                .with(BlockStateProperties.WATERLOGGED, waterlogged);
    }

    @Override
    protected BlockState getUpdatedState(World world, BlockPos pos, BlockState state, boolean placing)
    {
        BlockState newState = super.getUpdatedState(world, pos, state, placing);

        RailShape shape = newState.get(PropertyHolder.ASCENDING_RAIL_SHAPE);
        newState = newState.with(PropertyHolder.FACING_HOR, directionFromShape(shape));

        world.setBlockState(pos, newState);

        return newState;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos)
    {
        Direction dir = state.get(PropertyHolder.FACING_HOR);
        return hasSolidSideOnTop(world, pos.offset(dir));
    }

    @Override //Copy of AbstractRailBlock#neighborChanged() to use our own check for removal
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        if (!world.isRemote && world.getBlockState(pos).matchesBlock(this))
        {
            RailShape shape = getRailDirection(state, world, pos, null);
            if (isInvalidRailDirection(pos, world, shape))
            {
                spawnDrops(state, world, pos);
                world.removeBlock(pos, isMoving);
            }
            else
            {
                updateState(state, world, pos, block);
            }
        }
    }

    //Adapted from AbstractRailBlock#isValidRailDirection() (the MCP name is inverted) to not check the block below the rail
    private static boolean isInvalidRailDirection(BlockPos pos, World world, RailShape shape)
    {
        if (!shape.isAscending()) { throw new IllegalArgumentException("Invalid shape " + shape); }
        return !hasSolidSideOnTop(world, pos.offset(directionFromShape(shape)));
    }

    public static RailShape shapeFromDirection(Direction dir)
    {
        switch (dir)
        {
            case NORTH: return RailShape.ASCENDING_NORTH;
            case EAST: return RailShape.ASCENDING_EAST;
            case SOUTH: return RailShape.ASCENDING_SOUTH;
            case WEST: return RailShape.ASCENDING_WEST;
            default: throw new IllegalArgumentException("Invalid facing " + dir);
        }
    }

    private static Direction directionFromShape(RailShape shape)
    {
        switch (shape)
        {
            case ASCENDING_NORTH: return Direction.NORTH;
            case ASCENDING_EAST: return Direction.EAST;
            case ASCENDING_SOUTH: return Direction.SOUTH;
            case ASCENDING_WEST: return Direction.WEST;
            default: throw new IllegalArgumentException("Invalid shape " + shape);
        }
    }

    @Override
    public Property<RailShape> getShapeProperty() { return PropertyHolder.ASCENDING_RAIL_SHAPE; }

    @Override
    public final ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        return handleBlockActivated(world, pos, player, hand, hit);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(world, pos, placer, stack);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) { return getLight(world, pos); }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, Entity entity)
    {
        return getSound(state, world, pos);
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion)
    {
        return getCamoBlastResistance(state, world, pos, explosion);
    }

    @Override
    public boolean isFlammable(BlockState state, IBlockReader world, BlockPos pos, Direction face)
    {
        return isCamoFlammable(world, pos, face);
    }

    @Override
    public int getFlammability(BlockState state, IBlockReader world, BlockPos pos, Direction face)
    {
        return getCamoFlammability(world, pos, face);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public float getSlipperiness(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity)
    {
        return getCamoSlipperiness(state, world, pos, entity);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return shapes.get(state);
    }

    @Override //The default implementation defers to the AbstractBlock#getShape() overload without ISelectionContext argument
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        if (context instanceof EntitySelectionContext && context.getEntity() instanceof AbstractMinecartEntity)
        {
            return VoxelShapes.empty();
        }
        return getShape(state, worldIn, pos, context);
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos)
    {
        if (state.get(BlockStateProperties.WATERLOGGED))
        {
            world.getPendingFluidTicks().scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return super.updatePostPlacement(state, facing, facingState, world, pos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        if (state.get(BlockStateProperties.WATERLOGGED))
        {
            return Fluids.WATER.getStillFluidState(false);
        }
        return Fluids.EMPTY.getDefaultState();
    }

    @Override
    public final boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public final TileEntity createTileEntity(BlockState state, IBlockReader world) { return new FramedTileEntity(); }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_RAIL_SLOPE; }
}