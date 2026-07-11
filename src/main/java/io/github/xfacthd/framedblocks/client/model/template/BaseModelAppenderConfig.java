package io.github.xfacthd.framedblocks.client.model.template;

import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

record BaseModelAppenderConfig(boolean includeNull, boolean cullNonNull, @Nullable BlockState shaderState) {
}
