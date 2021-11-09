package com.cy4.machina.events;

import java.util.Random;

import com.cy4.machina.Machina;
import com.cy4.machina.api.capability.trait.CapabilityPlanetTrait;
import com.cy4.machina.api.events.planet.PlanetCreatedEvent;
import com.cy4.machina.starchart.PlanetData;
import com.cy4.machina.starchart.Starchart;

import net.minecraft.world.server.ServerWorld;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Machina.MOD_ID, bus = Bus.FORGE)
public class PlanetEvents {

	@SubscribeEvent
	public static void onPlanetCreated(PlanetCreatedEvent event) {
		if (event.getPlanet().isClientSide())
			return;

		event.getPlanet().getCapability(CapabilityPlanetTrait.PLANET_TRAIT_CAPABILITY).ifPresent(cap -> {
			long seed = ((ServerWorld) event.getPlanet()).getSeed();
			Starchart chart = new Starchart(seed);
			try {
				int id = Integer.parseInt(event.getPlanet().dimension().location().getPath());
				if (chart.planets.size() - 1 >= id) {
					chart.planets.get(id).traits.forEach(cap::addTrait);
				} else {
					PlanetData.getTraits(new Random(seed)).forEach(cap::addTrait);
				}
			} catch (NumberFormatException e) {
				
			}
		});
	}

}
