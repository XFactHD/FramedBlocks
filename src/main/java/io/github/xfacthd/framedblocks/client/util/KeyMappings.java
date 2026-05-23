package io.github.xfacthd.framedblocks.client.util;

import com.mojang.logging.LogUtils;
import io.github.xfacthd.framedblocks.api.block.blockentity.IFramedBlockEntity;
import io.github.xfacthd.framedblocks.api.block.item.IFramedBlockItem;
import io.github.xfacthd.framedblocks.api.util.FramedConstants;
import io.github.xfacthd.framedblocks.api.util.Utils;
import io.github.xfacthd.framedblocks.common.net.payload.serverbound.ServerboundStateCycleActionPayload;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

public final class KeyMappings {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final KeyMapping.Category KEY_CATEGORY = new KeyMapping.Category(Utils.id("main"));
    public static final Lazy<KeyMapping> UPDATE_CULLING = makeKeyMapping("update_cull", -1);
    public static final Lazy<KeyMapping> WIPE_CACHE = makeKeyMapping("wipe_cache", -1);
    public static final Lazy<KeyMapping> TOGGLE_STATE_CYCLE = makeKeyMapping("toggle_state_cycle", GLFW.GLFW_KEY_Y);
    public static final Lazy<KeyMapping> UNLOCK_STATE_CYCLE = makeKeyMapping("unlock_state_cycle", GLFW.GLFW_KEY_LEFT_CONTROL);

    private static Lazy<KeyMapping> makeKeyMapping(String name, int key) {
        return Lazy.of(() -> new KeyMapping(FramedConstants.MOD_ID + ".key." + name, key, KEY_CATEGORY));
    }

    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(KEY_CATEGORY);

        event.register(UPDATE_CULLING.get());
        event.register(WIPE_CACHE.get());
        event.register(TOGGLE_STATE_CYCLE.get());
        event.register(UNLOCK_STATE_CYCLE.get());
    }

    public static void onClientTick(@SuppressWarnings("unused") ClientTickEvent.Pre event) {
        Level level = Minecraft.getInstance().level;
        if (level == null || Minecraft.getInstance().screen != null) {
            return;
        }

        if (isKeyPressed(UPDATE_CULLING)) {
            HitResult hit = Minecraft.getInstance().hitResult;
            if (hit instanceof BlockHitResult blockHit && level.getBlockEntity(blockHit.getBlockPos()) instanceof IFramedBlockEntity be) {
                try {
                    be.updateCulling(true, true);
                } catch (Throwable throwable) {
                    LOGGER.error(
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

        if (isKeyPressed(WIPE_CACHE)) {
            CacheCleaner.clearModelCaches(CacheCleaner.Reason.MANUAL);

            //noinspection ConstantConditions
            Minecraft.getInstance().player.sendOverlayMessage(Component.literal("Model cache cleared"));
        }

        if (isKeyPressed(TOGGLE_STATE_CYCLE)) {
            Player player = Minecraft.getInstance().player;
            if (player != null && player.getMainHandItem().getItem() instanceof IFramedBlockItem item && item.getStateCycleSpec().canCycle()) {
                ClientPacketDistributor.sendToServer(ServerboundStateCycleActionPayload.TOGGLE);
            }
        }
    }

    private static boolean isKeyPressed(Lazy<KeyMapping> mapping) {
        boolean pressed = false;
        while (mapping.get().consumeClick()) {
            pressed = true;
        }
        return pressed;
    }

    private KeyMappings() { }
}
