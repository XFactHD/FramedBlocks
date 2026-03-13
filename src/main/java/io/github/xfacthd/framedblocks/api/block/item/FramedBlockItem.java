package io.github.xfacthd.framedblocks.api.block.item;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
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

import java.util.function.Consumer;

public class FramedBlockItem extends BlockItem implements IFramedBlockItem
{
    public FramedBlockItem(Block block, Properties props)
    {
        super(block, props);
        Preconditions.checkArgument(block instanceof IFramedBlock);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context)
    {
        return handlePlace(context, super::place);
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player entity)
    {
        return getCamoPlaceSound(state, level, pos, entity, super::getPlaceSound);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag)
    {
        IFramedBlockItem.appendCamoHoverText(stack, appender);
    }
}
