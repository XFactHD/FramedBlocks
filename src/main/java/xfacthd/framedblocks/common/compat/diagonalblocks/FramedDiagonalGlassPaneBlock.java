package xfacthd.framedblocks.common.compat.diagonalblocks;

import fuzs.diagonalblocks.api.v2.EightWayDirection;
import fuzs.diagonalblocks.api.v2.impl.StarCollisionBlock;
import fuzs.diagonalblocks.neoforge.api.v2.impl.NeoForgeDiagonalGlassPaneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.List;
import java.util.function.Consumer;

public final class FramedDiagonalGlassPaneBlock extends NeoForgeDiagonalGlassPaneBlock implements IFramedBlock
{
    public FramedDiagonalGlassPaneBlock(Block block)
    {
        super(block);
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
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
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

    /*@Override // TODO: Missing side context
    public boolean attachsTo(BlockState adjState, boolean sideSolid)
    {
        Direction adjSide = null;
        if (adjSide != null && !Utils.isY(adjSide) && DiagonalBlocksCompat.isFramedPane(adjState) && adjState.getValue(FramedProperties.STATE_LOCKED))
        {
            BooleanProperty prop = CrossCollisionBlock.PROPERTY_BY_DIRECTION.get(adjSide);
            if (!adjState.getValue(prop))
            {
                return false;
            }
        }
        return super.attachsTo(adjState, sideSolid);
    }*/

    @Override
    public BlockState updateIndirectNeighborDiagonalProperty(BlockState state, LevelAccessor level, BlockPos pos, EightWayDirection dir)
    {
        if (state.getValue(FramedProperties.STATE_LOCKED))
        {
            return null;
        }
        return super.updateIndirectNeighborDiagonalProperty(state, level, pos, dir);
    }

    @Override
    public boolean attachesDiagonallyTo(BlockState adjState, EightWayDirection adjDir)
    {
        if (adjState.getBlock() == this && adjState.getValue(FramedProperties.STATE_LOCKED))
        {
            BooleanProperty prop = StarCollisionBlock.PROPERTY_BY_DIRECTION.get(adjDir);
            if (!adjState.getValue(prop))
            {
                return false;
            }
        }
        return super.attachesDiagonallyTo(adjState, adjDir);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        updateCulling(level, pos);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, ctx))
        {
            return Shapes.empty();
        }
        return super.getShape(state, level, pos, ctx);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, null))
        {
            return Shapes.empty();
        }
        return super.getCollisionShape(state, level, pos, ctx);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override //The pane handles this through the SideSkipPredicate instead
    protected boolean skipRendering(BlockState state, BlockState adjacentState, Direction side)
    {
        return this == FBContent.BLOCK_FRAMED_BARS.value() && super.skipRendering(state, adjacentState, side);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> lines, TooltipFlag flag)
    {
        appendCamoHoverText(stack, lines);
    }

    @Override
    public BlockType getBlockType()
    {
        return BlockType.FRAMED_PANE;
    }

    @Override
    @Nullable
    public BlockState getItemModelSource()
    {
        return null;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(CrossCollisionBlock.EAST, true).setValue(CrossCollisionBlock.WEST, true);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer) { }
}
