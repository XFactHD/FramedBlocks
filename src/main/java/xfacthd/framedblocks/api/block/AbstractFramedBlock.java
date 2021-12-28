package xfacthd.framedblocks.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public abstract class AbstractFramedBlock extends Block implements IFramedBlock, SimpleWaterloggedBlock
{
    private final IBlockType blockType;
    private final Map<BlockState, VoxelShape> shapes;

    public AbstractFramedBlock(IBlockType blockType, Properties props)
    {
        super(props);
        this.blockType = blockType;
        this.shapes = blockType.generateShapes(getStateDefinition().getPossibleStates());

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
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
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
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState)
    {
        if (!isWaterLoggable()) { return false; }
        return SimpleWaterloggedBlock.super.placeLiquid(pLevel, pPos, pState, pFluidState);
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
    public IBlockType getBlockType() { return blockType; }

    protected final boolean isWaterLoggable() { return blockType.supportsWaterLogging(); }



    protected static BlockState withTop(BlockState state, Direction side, Vec3 hitVec)
    {
        if (side == Direction.DOWN)
        {
            state = state.setValue(FramedProperties.TOP, true);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(FramedProperties.TOP, false);
        }
        else
        {
            double y = hitVec.y;
            y -= Math.floor(y);

            state = state.setValue(FramedProperties.TOP, y >= .5D);
        }
        return state;
    }

    protected static BlockState withWater(BlockState state, LevelReader level, BlockPos pos)
    {
        FluidState fluidState = level.getFluidState(pos);
        return state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }
}