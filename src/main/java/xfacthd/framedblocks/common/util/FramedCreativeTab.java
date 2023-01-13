package xfacthd.framedblocks.common.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.FramedToolType;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FramedCreativeTab
{
    private static CreativeModeTab tab = null;

    @SubscribeEvent
    public static void onRegisterCreativeTabs(final CreativeModeTabEvent.Register event)
    {
        tab = event.registerCreativeModeTab(Utils.rl("main_tab"), builder ->
                builder.title(Component.translatable("itemGroup.framed_blocks"))
                        .icon(() -> new ItemStack(FBContent.blockFramedCube.get()))
                        .displayItems((flagSet, output, opPerm) ->
                        {
                            for (BlockType type : BlockType.values())
                            {
                                if (type.hasBlockItem())
                                {
                                    output.accept(FBContent.byType(type));
                                }
                            }

                            for (FramedToolType tool : FramedToolType.values())
                            {
                                output.accept(FBContent.toolByType(tool));
                            }
                        })
        );
    }

    public static CreativeModeTab get()
    {
        Objects.requireNonNull(tab, "Creative tab not initialized yet!");
        return tab;
    }
}
