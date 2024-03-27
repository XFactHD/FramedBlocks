package xfacthd.framedblocks.common.block.cube;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.*;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;
import net.neoforged.neoforge.common.IPlantable;
import xfacthd.framedblocks.api.block.*;
import xfacthd.framedblocks.api.block.render.FramedBlockRenderProperties;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.common.blockentity.special.FramedTargetBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public class FramedTargetBlock extends TargetBlock implements IFramedBlock
{
    public FramedTargetBlock()
    {
        super(IFramedBlock.createProperties(BlockType.FRAMED_TARGET));
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.SOLID, false)
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.SOLID, FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    public final InteractionResult use(
            BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        InteractionResult result = handleUse(state, level, pos, player, hand, hit);
        if (result != InteractionResult.PASS || !player.mayBuild())
        {
            return result;
        }

        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && level.getBlockEntity(pos) instanceof FramedTargetBlockEntity be)
        {
            DyeColor dye = DyeColor.getColor(stack);
            if (dye != null && be.setOverlayColor(dye))
            {
                level.playSound(player, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                player.awardStat(Stats.ITEM_USED.get(stack.getItem()));

                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos neighborPos
    )
    {
        updateCulling(level, currentPos);
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Override
    public void neighborChanged(
            BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving
    )
    {
        updateCulling(level, pos);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state)
    {
        return useCamoOcclusionShapeForLightOcclusion(state);
    }

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
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
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
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction side, IPlantable plant)
    {
        return canCamoSustainPlant(state, level, pos, side, plant);
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedTargetBlockEntity(pos, state);
    }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(FramedBlockRenderProperties.INSTANCE);
    }

    @Override
    public IBlockType getBlockType()
    {
        return BlockType.FRAMED_TARGET;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState();
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return state;
    }
}
