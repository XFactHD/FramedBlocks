package xfacthd.framedblocks.common.block;

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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.CtmPredicate;

import javax.annotation.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class FramedDoorBlock extends DoorBlock implements IFramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        if (state.getValue(BlockStateProperties.OPEN))
        {
            if (state.getValue(BlockStateProperties.DOOR_HINGE) == DoorHingeSide.LEFT)
            {
                return facing.getCounterClockWise() == dir;
            }
            else
            {
                return facing.getClockWise() == dir;
            }
        }
        else
        {
            return facing.getOpposite() == dir;
        }
    };

    public FramedDoorBlock()
    {
        super(IFramedBlock.createProperties(BlockType.FRAMED_DOOR));
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.SOLID, false)
                .setValue(FramedProperties.GLOWING, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID, FramedProperties.GLOWING);
    }

    @Override
    public final InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        InteractionResult result = handleUse(level, pos, player, hand, hit);
        if (result.consumesAction()) { return result; }

        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        //noinspection ConstantConditions
        super.setPlacedBy(level, pos, state, placer, stack);

        tryApplyCamoImmediately(level, pos, placer, stack);
        tryApplyCamoImmediately(level, pos.above(), placer, stack); //Apply to upper half as well
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        BlockState newState = super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        if (newState == state)
        {
            updateCulling(level, currentPos, facingState, facing, false);
        }
        return newState;
    }

    @Override
    public void onStateChangeClient(Level level, BlockPos pos, BlockState oldState, BlockState newState)
    {
        // Only check here when the block didn't change (i.e. by opening the door), everything else is handled in the BE packet handlers
        if (needCullingUpdateAfterStateChange(level, oldState, newState) && level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            be.updateCulling(false, false);
        }
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) { return useCamoOcclusionShapeForLightOcclusion(state); }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoOcclusionShape(state, level, pos);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return getCamoVisualShape(state, level, pos, ctx);
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
    public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState state, BlockState neighborState, Direction dir)
    {
        return doesHideNeighborFace(level, pos, state, neighborState, dir);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new FramedBlockEntity(pos, state); }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_DOOR; }
}