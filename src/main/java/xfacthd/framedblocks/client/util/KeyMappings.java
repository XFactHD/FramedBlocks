package xfacthd.framedblocks.client.util;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import org.lwjgl.glfw.GLFW;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.model.FramedBlockModel;
import xfacthd.framedblocks.api.util.FramedConstants;

public final class KeyMappings
{
    public static final String KEY_CATEGORY = FramedConstants.MOD_ID + ".key.categories.framedblocks";
    public static final Lazy<KeyMapping> KEYMAPPING_UPDATE_CULLING = makeKeyMapping("update_cull", GLFW.GLFW_KEY_F9);
    public static final Lazy<KeyMapping> KEYMAPPING_WIPE_CACHE = makeKeyMapping("wipe_cache", -1);

    private static Lazy<KeyMapping> makeKeyMapping(String name, int key)
    {
        return Lazy.of(() ->
                new KeyMapping(FramedConstants.MOD_ID + ".key." + name, key, KEY_CATEGORY)
        );
    }

    public static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        Level level = Minecraft.getInstance().level;
        if (event.phase != TickEvent.Phase.START || level == null || Minecraft.getInstance().screen != null)
        {
            return;
        }

        if (KEYMAPPING_UPDATE_CULLING.get().consumeClick())
        {
            HitResult hit = Minecraft.getInstance().hitResult;
            if (hit instanceof BlockHitResult blockHit && level.getBlockEntity(blockHit.getBlockPos()) instanceof FramedBlockEntity be)
            {
                be.updateCulling(true, true);

                BlockPos pos = blockHit.getBlockPos();
                Component blockName = be.getBlockState().getBlock().getName();

                Component msg = Component.literal("Culling updated for '")
                        .append(blockName)
                        .append("' at ")
                        .append(Component.literal(
                                String.format("{x=%d, y=%d, z=%d}", pos.getX(), pos.getY(), pos.getZ())
                        ));

                //noinspection ConstantConditions
                Minecraft.getInstance().player.displayClientMessage(msg, true);
            }
        }

        if (KEYMAPPING_WIPE_CACHE.get().consumeClick())
        {
            Minecraft.getInstance()
                    .getModelManager()
                    .getModelBakery()
                    .getBakedTopLevelModels()
                    .values()
                    .stream()
                    .filter(FramedBlockModel.class::isInstance)
                    .map(FramedBlockModel.class::cast)
                    .forEach(FramedBlockModel::clearCache);

            //noinspection ConstantConditions
            Minecraft.getInstance().player.displayClientMessage(Component.literal("Model cache cleared"), true);
        }
    }



    private KeyMappings() { }
}
