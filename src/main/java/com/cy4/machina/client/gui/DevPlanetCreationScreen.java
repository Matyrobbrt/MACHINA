package com.cy4.machina.client.gui;

import java.awt.event.KeyEvent;
import java.util.UUID;

import com.cy4.machina.api.capability.planet_data.ClientPlanetDataStorage;
import com.cy4.machina.api.planet.trait.PlanetTraitSpriteUploader;
import com.cy4.machina.init.KeyBindingsInit;
import com.cy4.machina.network.MachinaNetwork;
import com.cy4.machina.network.message.C2SDevPlanetCreationGUI;
import com.cy4.machina.network.message.C2SRequestTraitsUpdate;
import com.cy4.machina.network.message.C2SDevPlanetCreationGUI.ActionType;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class DevPlanetCreationScreen extends Screen {

	private TextFieldWidget dimensionID;

	public DevPlanetCreationScreen() {
		super(new StringTextComponent("CreativePlanetCreation"));
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new Button(width / 2 - 55, 50, 40, 20, new StringTextComponent("Create"),
				btn -> this.onCreateButton()));
		this.addButton(new Button(width / 2 - 105, 50, 50, 20, new StringTextComponent("Teleport"),
				btn -> this.onTeleportButton()));

		dimensionID = new TextFieldWidget(font, width / 2 - 150, 50, 40, 20,
				new TranslationTextComponent("Dimension ID"));
		dimensionID.setMaxLength(10);
		children.add(dimensionID);
		this.setInitialFocus(dimensionID);
		dimensionID.setFocus(true);
	}

	@Override
	public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
		String s = dimensionID.getValue();
		this.init(pMinecraft, pWidth, pHeight);
		dimensionID.setValue(s);
	}

	@Override
	public void tick() {
		super.tick();
		if (dimensionID.getValue().equalsIgnoreCase(
				KeyEvent.getKeyText(KeyBindingsInit.DEV_PLANET_CREATION_SCREEN.getKey().getValue()))) {
			dimensionID.setValue("");
		}
	}

	@SuppressWarnings("resource")
	@SubscribeEvent
	public static void overlayEvent(RenderGameOverlayEvent.Pre event) {
		if (event.getType() != ElementType.CROSSHAIRS) {
			return;
		}

		if (Minecraft.getInstance().screen instanceof DevPlanetCreationScreen) {
			event.setCanceled(true);
		}
	}

	private void onCreateButton() {
		try {
			MachinaNetwork.CHANNEL.sendToServer(
					new C2SDevPlanetCreationGUI(ActionType.CREATE, Integer.valueOf(dimensionID.getValue())));
			minecraft.player.sendMessage(
					new StringTextComponent(
							"Planet with the id of " + Integer.valueOf(dimensionID.getValue()) + " was created!"),
					UUID.randomUUID());
			this.onClose();
		} catch (NumberFormatException e) {
			minecraft.player.sendMessage(new StringTextComponent("Invalid Planet ID!"), UUID.randomUUID());
		}
	}

	private void onTeleportButton() {
		try {
			MachinaNetwork.CHANNEL.sendToServer(
					new C2SDevPlanetCreationGUI(ActionType.TELEPORT, Integer.valueOf(dimensionID.getValue())));
			this.onClose();
		} catch (NumberFormatException e) {
			minecraft.player.sendMessage(new StringTextComponent("Invalid Planet ID!"), UUID.randomUUID());
		}
	}

	@Override
	public void render(MatrixStack pMatrixStack, int pMouseX, int pMouseY, float pPartialTicks) {
		super.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);
		RenderSystem.color4f(1f, 1f, 1f, 1f);
		this.renderBackground(pMatrixStack);
		dimensionID.render(pMatrixStack, pMouseX, pMouseY, pPartialTicks);

		ClientPlanetDataStorage.getFromWorld(minecraft.level).ifPresent(cap -> {
			for (int i = 0; i < cap.getTraits().size(); i++) {
				TextureAtlasSprite textureatlassprite = PlanetTraitSpriteUploader.getFromInstance(cap.getTraits().get(i));
				minecraft.getTextureManager().bind(textureatlassprite.atlas().location());
				blit(pMatrixStack, width / 2 - 140 + (i * 18), 120, 12, 16, 16, textureatlassprite);
			}
		});
	}

	@Override
	public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
		return super.mouseClicked(pMouseX, pMouseY, pButton);
	}

	@Override
	public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
		if (pKeyCode == 257 && dimensionID.isFocused()) {
			onTeleportButton();
			onClose();
			return true;
		}
		return super.keyReleased(pKeyCode, pScanCode, pModifiers);
	}

}
