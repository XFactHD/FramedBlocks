package io.github.xfacthd.framedblocks.api.block.item;

import com.google.common.base.Preconditions;
import io.github.xfacthd.framedblocks.api.block.IFramedBlock;
import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoPrinter;
import io.github.xfacthd.framedblocks.api.util.sound.SoundEventType;
import io.github.xfacthd.framedblocks.api.util.sound.SoundUtils;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class FramedBlockItem extends BlockItem
{
    public FramedBlockItem(Block block, Properties props)
    {
        super(block, props);
        Preconditions.checkArgument(block instanceof IFramedBlock);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context)
    {
        InteractionResult result = super.place(context);
        if (result == InteractionResult.SUCCESS)
        {
            playPlaceSound(context);
        }
        return result;
    }

    protected void playPlaceSound(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (!(level.getBlockEntity(pos) instanceof IFramedBlockEntity be)) return;

        SoundType soundOne = be.getCamo().getContent().getSoundType();
        SoundUtils.playPlaceSound(context, soundOne, false);

        if (!(be instanceof FramedDoubleBlockEntity dbe)) return;

        SoundType soundTwo = dbe.getCamoTwo().getContent().getSoundType();
        if (!SoundUtils.isSameSound(soundOne, soundTwo, SoundEventType.PLACE))
        {
            SoundUtils.playPlaceSound(context, soundTwo, false);
        }
    }

    @Override
    protected SoundEvent getPlaceSound(BlockState state, Level level, BlockPos pos, Player entity)
    {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity)
        {
            // Dummy out the automatically played place sound
            return SoundEvents.EMPTY;
        }
        return super.getPlaceSound(state, level, pos, entity);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> appender, TooltipFlag flag)
    {
        appendCamoHoverText(stack, appender);
    }

    public static void appendCamoHoverText(ItemStack stack, Consumer<Component> appender)
    {
        CamoPrinter.printCamoList(appender, stack.get(Utils.DC_TYPE_CAMO_LIST), false);
    }
}
