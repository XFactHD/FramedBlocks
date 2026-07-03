package io.github.xfacthd.framedblocks.api.model.wrapping;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/// Event for registering model wrappers for framed blocks.
///
/// Fired on the mod event bus only on the physical client.
///
/// @see WrapHelper
public final class RegisterModelWrappersEvent extends Event implements IModBusEvent { }
