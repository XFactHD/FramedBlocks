package xfacthd.framedblocks.common.block.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class FramedPaneBlock extends IronBarsBlock implements IFramedBlock
{
    private final BlockType type;

    public FramedPaneBlock(BlockType type)
    {
        super(IFramedBlock.createProperties(type));
        this.type = type;
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.STATE_LOCKED, false)
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.STATE_LOCKED, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    public final InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState facingState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos facingPos
    )
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
    public void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving
    )
    {
        updateCulling(level, pos);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, ctx))
        {
            return Shapes.empty();
        }
        return super.getShape(state, level, pos, ctx);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, null))
        {
            return Shapes.empty();
        }
        return super.getCollisionShape(state, level, pos, ctx);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override //The pane handles this through the SideSkipPredicate instead
    public boolean skipRendering(BlockState state, BlockState adjacentState, Direction side)
    {
        return this == FBContent.BLOCK_FRAMED_BARS.value() && super.skipRendering(state, adjacentState, side);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedBlockRenderProperties.INSTANCE);
    }

    @Override
    public BlockType getBlockType()
    {
        return type;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(EAST, true).setValue(WEST, true);
    }
}