package io.github.xfacthd.framedblocks.client.render.block.state;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.saveddata.maps.MapId;
import org.jspecify.annotations.Nullable;

public final class FramedItemFrameRenderState extends BlockEntityRenderState {
    public Direction direction = Direction.NORTH;
    public final ItemStackRenderState item = new ItemStackRenderState();
    public int rotation;
    public boolean isGlowFrame;
    @Nullable
    public MapId mapId;
    public final MapRenderState mapRenderState = new MapRenderState();
    @Nullable
    public Component nameTag;
    public double distanceToCameraSq;
}
