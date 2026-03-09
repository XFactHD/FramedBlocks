package io.github.xfacthd.framedblocks.common.block.interactive.button;

import io.github.xfacthd.framedblocks.api.block.BlockUtils;
import io.github.xfacthd.framedblocks.api.block.FramedProperties;
import io.github.xfacthd.framedblocks.api.component.WrenchRotationMode;
import io.github.xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import io.github.xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import io.github.xfacthd.framedblocks.api.util.RotationDirection;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.block.IFramedBlockInternal;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class FramedButtonBlock extends ButtonBlock implements IFramedBlockInternal
{
    public static final ButtonStateMerger STATE_MERGER = new ButtonStateMerger();

    private final BlockType type;
    private final float jadeScale;

    protected FramedButtonBlock(BlockType type, Properties props, BlockSetType blockSet, int pressTime)
    {
        super(blockSet, pressTime, props
                .pushReaction(PushReaction.DESTROY)
                .noCollision()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .noOcclusion()
        );
        this.type = type;
        this.jadeScale = (type == BlockType.FRAMED_BUTTON || type == BlockType.FRAMED_STONE_BUTTON) ? 2F : 1F;
        BlockUtils.configureStandardProperties(this);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        BlockUtils.addRequiredProperties(builder);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
    )
    {
        InteractionResult result = handleUse(state, level, pos, player, hand, hit);
        if (result == InteractionResult.FAIL)
        {
            // Allow interacting with the block while holding a framed block
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return result;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return super.getDrops(state, getCamoDrops(builder));
    }

    @Override
    public BlockState rotate(BlockState state, RotationDirection direction, WrenchRotationMode mode)
    {
        if (state.getValue(FACE) != AttachFace.WALL)
        {
            return IFramedBlockInternal.super.rotate(state, direction, mode);
        }
        return state;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation)
    {
        return super.rotate(state, rotation);
    }

    @Override
    public BlockType getBlockType()
    {
        return type;
    }

    @Override
    public BlockState getItemModelSource()
    {
        return defaultBlockState().setValue(FACE, AttachFace.FLOOR);
    }

    @Override
    @Nullable
    public Direction getHorizontalOrientation(BlockState state)
    {
        return state.getValue(FACING);
    }

    @Override
    public Class<? extends Block> getJadeTargetClass()
    {
        return FramedButtonBlock.class;
    }

    @Override
    public BlockState getJadeRenderState(BlockState state)
    {
        return defaultBlockState().setValue(FACE, AttachFace.FLOOR);
    }

    @Override
    public float getJadeRenderScale(BlockState state)
    {
        return jadeScale;
    }



    public static FramedButtonBlock wood(Properties props)
    {
        return new FramedButtonBlock(
                BlockType.FRAMED_BUTTON,
                props,
                BlockSetType.OAK,
                30
        );
    }

    public static FramedButtonBlock stone(Properties props)
    {
        return new FramedButtonBlock(
                BlockType.FRAMED_STONE_BUTTON,
                props,
                BlockSetType.STONE,
                20
        );
    }



    public static final class ButtonStateMerger implements StateMerger
    {
        private final StateMerger ignoringMerger = StateMerger.ignoring(WrapHelper.IGNORE_ALWAYS);

        private ButtonStateMerger() { }

        @Override
        public BlockState apply(BlockState state)
        {
            state = ignoringMerger.apply(state);

            AttachFace face = state.getValue(FACE);
            if (face != AttachFace.WALL)
            {
                Direction dir = state.getValue(FACING);
                if (dir == Direction.SOUTH || dir == Direction.WEST)
                {
                    state = state.setValue(FACING, dir.getOpposite());
                }
            }
            return state;
        }

        @Override
        public Set<Property<?>> getHandledProperties(Holder<Block> block)
        {
            return Utils.concat(
                    ignoringMerger.getHandledProperties(block),
                    Set.of(FramedLargeButtonBlock.FACING)
            );
        }
    }
}
