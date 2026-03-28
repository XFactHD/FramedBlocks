package io.github.xfacthd.framedblocks.client.util;

import io.github.xfacthd.framedblocks.FramedBlocks;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

public final class KeyMappings {
    public static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(Utils.id("main"));
    public static final Lazy<KeyMapping> KEYMAPPING_UPDATE_CULLING = makeKeyMapping("update_cull", GLFW.GLFW_KEY_F9);
    public static final Lazy<KeyMapping> KEYMAPPING_WIPE_CACHE = makeKeyMapping("wipe_cache", -1);

    private static Lazy<KeyMapping> makeKeyMapping(String name, int key) {
        return Lazy.of(() -> new KeyMapping(FramedConstants.MOD_ID + ".key." + name, key, KEY_CATEGORY));
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(KEY_CATEGORY);

        event.register(KEYMAPPING_UPDATE_CULLING.get());
        event.register(KEYMAPPING_WIPE_CACHE.get());
    }

    public static void onClientTick(@SuppressWarnings("unused") ClientTickEvent.Pre event) {
        Level level = Minecraft.getInstance().level;
        if (level == null || Minecraft.getInstance().screen != null) {
            return;
        }

        if (KEYMAPPING_UPDATE_CULLING.get().consumeClick()) {
            HitResult hit = Minecraft.getInstance().hitResult;
            if (hit instanceof BlockHitResult blockHit && level.getBlockEntity(blockHit.getBlockPos()) instanceof IFramedBlockEntity be) {
                try {
                    be.updateCulling(true, true);
                } catch (Throwable throwable) {
                    FramedBlocks.LOGGER.error(
                            "Encountered unexpected exception while updating culling of '{}'",
                            be.getBlockState().getBlock(),
                            throwable
                    );
                }

                BlockPos pos = blockHit.getBlockPos();
                Component blockName = be.getBlockState().getBlock().getName();

                Component msg = Component.literal("Culling updated for '")
                        .append(blockName)
                        .append("' at ")
                        .append(Component.literal(
                                String.format("{x=%d, y=%d, z=%d}", pos.getX(), pos.getY(), pos.getZ())
                        ));

                //noinspection ConstantConditions
                Minecraft.getInstance().player.sendOverlayMessage(msg);
            }
        }

        if (KEYMAPPING_WIPE_CACHE.get().consumeClick()) {
            CacheCleaner.clearModelCaches(CacheCleaner.Reason.MANUAL);

            //noinspection ConstantConditions
            Minecraft.getInstance().player.sendOverlayMessage(Component.literal("Model cache cleared"));
        }
    }

    private KeyMappings() { }
}
