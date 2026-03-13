package io.github.xfacthd.framedblocks.api.block.item;

import io.github.xfacthd.framedblocks.api.block.blockentity.FramedDoubleBlockEntity;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.camo.CamoPrinter;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.api.util.sound.SoundEventType;
import io.github.xfacthd.framedblocks.api.util.sound.SoundUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Consumer;
import java.util.function.Function;

public interface IFramedBlockItem
{
    @ApiStatus.NonExtendable
    default InteractionResult handlePlace(BlockPlaceContext context, Function<BlockPlaceContext, InteractionResult> superHandler)
    {
        InteractionResult result = superHandler.apply(context);
        if (result == InteractionResult.SUCCESS)
        {
            playPlaceSound(context);
        }
        return result;
    }

    @ApiStatus.OverrideOnly
    default void playPlaceSound(BlockPlaceContext context)
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

    @ApiStatus.NonExtendable
    default SoundEvent getCamoPlaceSound(BlockState state, Level level, BlockPos pos, Player player, PlaceSoundGetter superGetter)
    {
        if (level.getBlockEntity(pos) instanceof IFramedBlockEntity)
        {
            // Dummy out the automatically played place sound
            return SoundEvents.EMPTY;
        }
        return superGetter.get(state, level, pos, player);
    }

    static void appendCamoHoverText(ItemStack stack, Consumer<Component> appender)
    {
        CamoPrinter.printCamoList(appender, stack.get(Utils.DC_TYPE_CAMO_LIST), false);
    }

    interface PlaceSoundGetter
    {
        SoundEvent get(BlockState state, Level level, BlockPos pos, Player player);
    }
}
