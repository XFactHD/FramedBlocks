package io.github.xfacthd.framedblocks.api.block.item;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FramedBlockItem extends BlockItem implements IFramedBlockItem {
    private final StateCycleSpec cycleSpec;
    private final boolean customEmptyPlaceSound;

    public FramedBlockItem(Block block, Properties props) {
        this(block, props, false);
    }

    public FramedBlockItem(Block block, Properties props, boolean customEmptyPlaceSound) {
        Preconditions.checkArgument(block instanceof IFramedBlock);
        super(block, props);
        this.customEmptyPlaceSound = customEmptyPlaceSound;
        this.cycleSpec = ((IFramedBlock) block).createStateCycleSpec();
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        return handlePlace(context, super::place);
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        return getPlacementState(context, super::getPlacementState);
    }

    @Override
    public boolean useCustomEmptyPlaceSound() {
        return customEmptyPlaceSound;
    }

    @Override
    public StateCycleSpec getStateCycleSpec() {
        return cycleSpec;
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player entity) {
        return getCamoPlaceSound(state, level, pos, entity, super::getPlaceSound);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag) {
        appendDefaultHoverText(stack, ctx, appender);
    }
}
