package io.github.xfacthd.framedblocks.api.model.data;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;

@ApiStatus.NonExtendable
public interface QuadMapBuilder
{
    ArrayList<BakedQuad> getOrCreate(@Nullable Direction side);
}
