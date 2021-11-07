package com.cy4.machina.api.capability.trait;

import java.util.LinkedList;
import java.util.List;

import com.cy4.machina.api.planet.PlanetTrait;

public class DefaultPlanetTraitCapability implements IPlanetTraitCapability {

	private List<PlanetTrait> traits = new LinkedList<>();

	@Override
	public void addTrait(PlanetTrait trait) {
		traits.add(trait);
	}

	@Override
	public void removeTrait(PlanetTrait trait) {
		traits.remove(trait);
	}

	@Override
	public List<PlanetTrait> getTraits() { return traits; }

}