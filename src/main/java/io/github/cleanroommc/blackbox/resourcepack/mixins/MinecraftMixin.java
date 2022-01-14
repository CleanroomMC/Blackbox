package io.github.cleanroommc.blackbox.resourcepack.mixins;

import io.github.cleanroommc.blackbox.resourcepack.ResourcePackInjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(Minecraft.class)
public class MinecraftMixin {

	@SuppressWarnings("all")
	@Inject(method = "refreshResources", at = @At(value = "INVOKE_ASSIGN", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;", remap = false, shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
	private void addInternalResourcePack(CallbackInfo ci, List<IResourcePack> list) {
		list.addAll(ResourcePackInjection.getInjectedPacks());
	}

}
