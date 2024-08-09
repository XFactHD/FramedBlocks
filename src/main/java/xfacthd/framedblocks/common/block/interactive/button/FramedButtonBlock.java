package xfacthd.framedblocks.common.block.interactive.button;

import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.block.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.model.wrapping.WrapHelper;
import xfacthd.framedblocks.api.model.wrapping.statemerger.StateMerger;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.BlockType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class FramedButtonBlock extends ButtonBlock implements IFramedBlock
{
    public static final ButtonStateMerger STATE_MERGER = new ButtonStateMerger();

    private final BlockType type;
    private final float jadeScale;

    protected FramedButtonBlock(BlockType type, BlockSetType blockSet, int pressTime)
    {
        super(blockSet, pressTime, Properties.of()
                .pushReaction(PushReaction.DESTROY)
                .noCollission()
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .noOcclusion()
        );
        this.type = type;
        this.jadeScale = (type == BlockType.FRAMED_BUTTON || type == BlockType.FRAMED_STONE_BUTTON) ? 2F : 1F;
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.GLOWING, false)
                .setValue(FramedProperties.PROPAGATES_SKYLIGHT, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.GLOWING, FramedProperties.PROPAGATES_SKYLIGHT);
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
    protected float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoShadeBrightness(state, level, pos, super.getShadeBrightness(state, level, pos));
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getValue(FramedProperties.PROPAGATES_SKYLIGHT);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (state.getValue(FACE) != AttachFace.WALL)
        {
            return rotate(state, rot);
        }
        return state;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> lines, TooltipFlag flag)
    {
        appendCamoHoverText(stack, lines);
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



    public static FramedButtonBlock wood()
    {
        return new FramedButtonBlock(
                BlockType.FRAMED_BUTTON,
                BlockSetType.OAK,
                30
        );
    }

    public static FramedButtonBlock stone()
    {
        return new FramedButtonBlock(
                BlockType.FRAMED_STONE_BUTTON,
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
