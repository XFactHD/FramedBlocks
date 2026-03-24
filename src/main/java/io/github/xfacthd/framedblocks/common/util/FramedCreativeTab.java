package io.github.xfacthd.framedblocks.common.util;

import io.github.xfacthd.framedblocks.common.FBContent;
import io.github.xfacthd.framedblocks.common.data.BlockType;
import io.github.xfacthd.framedblocks.common.data.FramedToolType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public final class FramedCreativeTab
{
    public static CreativeModeTab makeTab()
    {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.framed_blocks"))
                .icon(() -> new ItemStack(FBContent.BLOCK_FRAMED_CUBE.value()))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .displayItems((_, output) ->
                {
                    for (BlockType type : BlockType.values())
                    {
                        if (type.hasBlockItem())
                        {
                            output.accept(FBContent.byType(type));
                        }
                    }

                    output.accept(FBContent.BLOCK_FRAMING_SAW.value());
                    output.accept(FBContent.BLOCK_POWERED_FRAMING_SAW.value());

                    for (FramedToolType tool : FramedToolType.values())
                    {
                        output.accept(FBContent.toolByType(tool));
                    }

                    output.accept(FBContent.ITEM_FRAMED_REINFORCEMENT.value());
                    output.accept(FBContent.ITEM_PHANTOM_PASTE.value());
                    output.accept(FBContent.ITEM_GLOW_PASTE.value());
                })
                .build();
    }

    private FramedCreativeTab() { }
}
