package xfacthd.framedblocks.client.util;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.FramedConstants;

@Mod.EventBusSubscriber(modid = FramedConstants.MOD_ID, value = Dist.CLIENT)
public final class KeyMappings
{
    public static final String KEY_CATEGORY = FramedConstants.MOD_ID + ".key.categories.framedblocks";
    public static final Lazy<KeyMapping> KEYMAPPING_UPDATE_CULLING = makeKeyMapping("update_cull", GLFW.GLFW_KEY_F9);

    public static void register()
    {
        ClientRegistry.registerKeyBinding(KEYMAPPING_UPDATE_CULLING.get());
    }

    private static Lazy<KeyMapping> makeKeyMapping(String name, int key)
    {
        return Lazy.of(() ->
                new KeyMapping(FramedConstants.MOD_ID + ".key." + name, key, KEY_CATEGORY)
        );
    }

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        Level level = Minecraft.getInstance().level;
        if (event.phase != TickEvent.Phase.START || level == null || Minecraft.getInstance().screen != null) { return; }

        if (KEYMAPPING_UPDATE_CULLING.get().consumeClick())
        {
            HitResult hit = Minecraft.getInstance().hitResult;
            if (hit instanceof BlockHitResult blockHit && level.getBlockEntity(blockHit.getBlockPos()) instanceof FramedBlockEntity be)
            {
                be.updateCulling(true, true);
                //noinspection ConstantConditions
                Minecraft.getInstance().player.displayClientMessage(new TextComponent("Culling updated"), true);
            }
        }
    }



    private KeyMappings() { }
}
