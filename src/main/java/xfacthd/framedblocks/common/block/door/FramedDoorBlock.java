package xfacthd.framedblocks.common.block.door;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.predicate.CtmPredicate;
import xfacthd.framedblocks.api.util.ClientUtils;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.*;

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

    private final BlockType type;

    private FramedDoorBlock(BlockType type, Properties props, SoundEvent closeSound, SoundEvent openSound)
    {
        super(props, closeSound, openSound);
        this.type = type;
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
        InteractionResult result = handleUse(state, level, pos, player, hand, hit);
        if (result.consumesAction()) { return result; }

        return material == IRON_WOOD ? InteractionResult.PASS : super.use(state, level, pos, player, hand, hit);
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
        if (level.isClientSide())
        {
            if (newState == state)
            {
                updateCulling(level, currentPos, facingState, facing, false);
            }
            else if (newState.isAir())
            {
                BlockPos pos = currentPos.immutable();
                Direction dir = state.getValue(FACING);
                Set<Direction> faces = EnumSet.of(
                        dir.getClockWise(),
                        dir.getCounterClockWise(),
                        state.getValue(HALF) == DoubleBlockHalf.UPPER ? Direction.UP : Direction.DOWN
                );
                ClientUtils.enqueueClientTask(3, () ->
                {
                    for (Direction face : faces)
                    {
                        if (level.getBlockEntity(pos.relative(face)) instanceof FramedBlockEntity be)
                        {
                            be.updateCulling(face.getOpposite(), true);
                        }
                    }
                });
            }
        }
        return newState;
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
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedBlockEntity(pos, state);
    }

    @Override
    public BlockType getBlockType() { return type; }



    // Fake wooden material to satisfy the hard-coded wooden door check while staying wooden
    public static final Material IRON_WOOD = new Material(
            Material.WOOD.getColor(),
            Material.WOOD.isLiquid(),
            Material.WOOD.isSolid(),
            Material.WOOD.blocksMotion(),
            Material.WOOD.isSolidBlocking(),
            Material.WOOD.isFlammable(),
            Material.WOOD.isReplaceable(),
            Material.WOOD.getPushReaction()
    );

    public static FramedDoorBlock wood()
    {
        return new FramedDoorBlock(
                BlockType.FRAMED_DOOR,
                IFramedBlock.createProperties(BlockType.FRAMED_DOOR),
                SoundEvents.WOODEN_DOOR_CLOSE,
                SoundEvents.WOODEN_DOOR_OPEN
        );
    }

    public static FramedDoorBlock iron()
    {
        return new FramedDoorBlock(
                BlockType.FRAMED_IRON_DOOR,
                IFramedBlock.createProperties(BlockType.FRAMED_IRON_DOOR, IRON_WOOD)
                        .requiresCorrectToolForDrops(),
                SoundEvents.IRON_DOOR_CLOSE,
                SoundEvents.IRON_DOOR_OPEN
        );
    }
}