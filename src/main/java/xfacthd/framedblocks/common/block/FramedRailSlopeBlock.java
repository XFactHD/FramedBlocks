package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.blockentity.FramedBlockEntity;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

//FIXME: placing a rail next to this that would set an invalid RailShape causes an IllegalArgumentException because RailState
//       doesn't check if the shape property supports the new shape
@SuppressWarnings("deprecation")
public class FramedRailSlopeBlock extends BaseRailBlock implements IFramedBlock
{
    private final Map<BlockState, VoxelShape> shapes;

    public FramedRailSlopeBlock()
    {
        super(true, IFramedBlock.createProperties());
        shapes = getBlockType().generateShapes(getStateDefinition().getPossibleStates());
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.SLOPE_TYPE, PropertyHolder.ASCENDING_RAIL_SHAPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        RailShape shape = shapeFromDirection(context.getHorizontalDirection());

        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        boolean waterlogged = fluidState.getType() == Fluids.WATER;

        return defaultBlockState()
                .setValue(PropertyHolder.FACING_HOR, context.getHorizontalDirection())
                .setValue(PropertyHolder.ASCENDING_RAIL_SHAPE, shape)
                .setValue(BlockStateProperties.WATERLOGGED, waterlogged);
    }

    @Override
    protected BlockState updateDir(Level level, BlockPos pos, BlockState state, boolean placing)
    {
        BlockState newState = super.updateDir(level, pos, state, placing);

        RailShape shape = newState.getValue(PropertyHolder.ASCENDING_RAIL_SHAPE);
        newState = newState.setValue(PropertyHolder.FACING_HOR, directionFromShape(shape));

        level.setBlockAndUpdate(pos, newState);

        return newState;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        Direction dir = state.getValue(PropertyHolder.FACING_HOR);
        return canSupportRigidBlock(level, pos.relative(dir));
    }

    @Override //Copy of AbstractRailBlock#neighborChanged() to use our own check for removal
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pFromPos, boolean isMoving)
    {
        if (!level.isClientSide() && level.getBlockState(pos).is(this))
        {
            RailShape shape = getRailDirection(state, level, pos, null);
            if (shouldBeRemoved(pos, level, shape))
            {
                dropResources(state, level, pos);
                level.removeBlock(pos, isMoving);
            }
            else
            {
                updateState(state, level, pos, block);
            }
        }
    }

    //Adapted from BaseRailBlock#shouldBeRemoved() to not check the block below the rail
    private static boolean shouldBeRemoved(BlockPos pos, Level level, RailShape shape)
    {
        if (!shape.isAscending()) { throw new IllegalArgumentException("Invalid shape " + shape); }
        return !canSupportRigidBlock(level, pos.relative(directionFromShape(shape)));
    }

    public static RailShape shapeFromDirection(Direction dir)
    {
        return switch (dir)
        {
            case NORTH -> RailShape.ASCENDING_NORTH;
            case EAST -> RailShape.ASCENDING_EAST;
            case SOUTH -> RailShape.ASCENDING_SOUTH;
            case WEST -> RailShape.ASCENDING_WEST;
            default -> throw new IllegalArgumentException("Invalid facing " + dir);
        };
    }

    private static Direction directionFromShape(RailShape shape)
    {
        return switch (shape)
        {
            case ASCENDING_NORTH -> Direction.NORTH;
            case ASCENDING_EAST -> Direction.EAST;
            case ASCENDING_SOUTH -> Direction.SOUTH;
            case ASCENDING_WEST -> Direction.WEST;
            default -> throw new IllegalArgumentException("Invalid shape " + shape);
        };
    }

    @Override
    public Property<RailShape> getShapeProperty() { return PropertyHolder.ASCENDING_RAIL_SHAPE; }

    @Override
    public final InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return handleUse(level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) { return getLight(level, pos); }

    @Override
    public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity)
    {
        return getCamoSound(state, level, pos);
    }

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
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public float getFriction(BlockState state, LevelReader level, BlockPos pos, @Nullable Entity entity)
    {
        return getCamoSlipperiness(state, level, pos, entity);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return shapes.get(state);
    }

    @Override //The default implementation defers to the AbstractBlock#getShape() overload without ISelectionContext argument
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context)
    {
        if (context instanceof EntityCollisionContext ctx && ctx.getEntity().orElse(null) instanceof AbstractMinecart)
        {
            return Shapes.empty();
        }
        return getShape(state, worldIn, pos, context);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedBlockEntity(pos, state); }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_RAIL_SLOPE; }
}