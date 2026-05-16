package io.github.xfacthd.framedblocks.common.item.block;

import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.block.item.placement.StateCycleSpec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

public class FramedStandingAndWallBlockItem extends StandingAndWallBlockItem implements IFramedBlockItem {
    private final StateCycleSpec cycleSpec;

    public FramedStandingAndWallBlockItem(Block block, Block wallBlock, Direction attachDir, Properties properties) {
        super(block, wallBlock, attachDir, properties);
        this.cycleSpec = ((IFramedBlock) block).createStateCycleSpec();
    }

    @Override
    public StateCycleSpec getStateCycleSpec() {
        return cycleSpec;
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
    protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player entity) {
        return getCamoPlaceSound(state, level, pos, entity, super::getPlaceSound);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag) {
        appendDefaultHoverText(stack, ctx, appender);
    }
}
