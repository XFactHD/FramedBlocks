package xfacthd.framedblocks.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.FramedToolType;

public final class FramedCreativeTab
{
    public static CreativeModeTab makeTab()
    {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.framed_blocks"))
                .icon(() -> new ItemStack(FBContent.BLOCK_FRAMED_CUBE.value()))
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .displayItems((params, output) ->
                {
                    for (BlockType type : BlockType.values())
                    {
                        // Simple workaround for these two needing dedicated items for blueprint purposes
                        if (type == BlockType.FRAMED_DOUBLE_SLAB || type == BlockType.FRAMED_DOUBLE_PANEL)
                        {
                            continue;
                        }

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
                })
                .build();
    }

    private FramedCreativeTab() { }
}
