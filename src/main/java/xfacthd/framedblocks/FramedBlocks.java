package xfacthd.framedblocks;

import com.google.common.base.Preconditions;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmllegacy.network.NetworkDirection;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xfacthd.framedblocks.client.util.ClientConfig;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.IFramedBlock;
import xfacthd.framedblocks.common.item.FramedToolItem;
import xfacthd.framedblocks.common.net.OpenSignScreenPacket;
import xfacthd.framedblocks.common.net.SignUpdatePacket;
import xfacthd.framedblocks.common.util.CommonConfig;

@Mod(FramedBlocks.MODID)
@Mod.EventBusSubscriber(modid = FramedBlocks.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FramedBlocks
{
	public static final String MODID = "framedblocks";
	public static final Logger LOGGER = LogManager.getLogger();

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
	
    public static final CreativeModeTab FRAMED_GROUP = new CreativeModeTab("framed_blocks")
    {
        @Override
        public ItemStack makeIcon() { return new ItemStack(FBContent.blockFramedCube.get()); }

        @Override
        public void fillItemList(NonNullList<ItemStack> items)
        {
            super.fillItemList(items);
            items.sort((s1, s2) ->
            {
                Item itemOne = s1.getItem();
                Item itemTwo = s2.getItem();

                if (itemOne instanceof FramedToolItem toolOne && itemTwo instanceof FramedToolItem toolTwo)
                {
                    return toolOne.getType().compareTo(toolTwo.getType());
                }
                else if (itemOne instanceof FramedToolItem) { return 1; }
                else if (itemTwo instanceof FramedToolItem) { return -1; }

                Preconditions.checkArgument(
                        itemOne instanceof BlockItem bi && bi.getBlock() instanceof IFramedBlock,
                        String.format("Invalid item in FramedBlocks creative tab: %s", itemOne.getRegistryName())
                );
                Preconditions.checkArgument(
                        itemTwo instanceof BlockItem bi && bi.getBlock() instanceof IFramedBlock,
                        String.format("Invalid item in FramedBlocks creative tab: %s", itemOne.getRegistryName())
                );

                Block b1 = ((BlockItem) itemOne).getBlock();
                Block b2 = ((BlockItem) itemTwo).getBlock();
                return ((IFramedBlock)b1).getBlockType().compareTo(((IFramedBlock)b2).getBlockType());
            });
        }
    };

    public FramedBlocks()
    {
        FBContent.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
    }

    @SubscribeEvent
    public static void setup(final FMLCommonSetupEvent event)
    {
        CHANNEL.messageBuilder(SignUpdatePacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SignUpdatePacket::encode)
                .decoder(SignUpdatePacket::new)
                .consumer(SignUpdatePacket::handle)
                .add();

        CHANNEL.messageBuilder(OpenSignScreenPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(OpenSignScreenPacket::encode)
                .decoder(OpenSignScreenPacket::new)
                .consumer(OpenSignScreenPacket::handle)
                .add();
    }
}
