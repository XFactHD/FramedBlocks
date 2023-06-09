package xfacthd.framedblocks.common.block.stairs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.common.IPlantable;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.predicate.CtmPredicate;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class FramedStairsBlock extends StairBlock implements IFramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, dir) ->
    {
        if (dir == Direction.UP)
        {
            return state.getValue(BlockStateProperties.HALF) == Half.TOP;
        }
        else if (dir == Direction.DOWN)
        {
            return state.getValue(BlockStateProperties.HALF) == Half.BOTTOM;
        }
        else
        {
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            StairsShape shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
            if (shape == StairsShape.STRAIGHT)
            {
                return facing == dir;
            }
            else if (shape == StairsShape.INNER_LEFT)
            {
                return facing == dir || facing.getCounterClockWise() == dir;
            }
            else if (shape == StairsShape.INNER_RIGHT)
            {
                return facing == dir || facing.getClockWise() == dir;
            }
            else
            {
                return false;
            }
        }
    };

    public FramedStairsBlock()
    {
        super(() -> FBContent.blockFramedCube.get().defaultBlockState(), IFramedBlock.createProperties(BlockType.FRAMED_STAIRS));
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.SOLID, false)
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.STATE_LOCKED, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID, FramedProperties.GLOWING, FramedProperties.STATE_LOCKED);
    }

    @Override
    public final InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        BlockState newState = updateShapeLockable(
                state, level, currentPos,
                () -> super.updateShape(state, facing, facingState, level, currentPos, facingPos)
        );

        if (newState == state)
        {
            updateCulling(level, currentPos);
        }
        return newState;
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        updateCulling(level, pos);
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
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state)
    {
        spawnCamoDestroyParticles(level, player, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, ctx)) { return Shapes.empty(); }
        return super.getShape(state, level, pos, ctx);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction side, IPlantable plant)
    {
        return canCamoSustainPlant(state, level, pos, side, plant);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(new FramedBlockRenderProperties());
    }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_STAIRS; }
}