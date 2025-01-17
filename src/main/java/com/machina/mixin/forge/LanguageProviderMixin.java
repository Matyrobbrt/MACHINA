package com.machina.mixin.forge;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraftforge.common.data.LanguageProvider;

@Mixin(value = LanguageProvider.class, remap = false)
public abstract class LanguageProviderMixin {

	private static final Logger LOGGER = LogManager.getLogger();

	@Final
	@Shadow
	private Map<String, String> data;

	@Inject(remap = false, at = @At(value = "HEAD"), method = "add(Ljava/lang/String;Ljava/lang/String;)V", cancellable = true)
	private void machina$addMixin(String key, String value, CallbackInfo ci) {
		if (data.put(key, value) != null) {
			LOGGER.warn("Found already existing key {}! Skipping.", key);
		}
		ci.cancel();
	}

}
