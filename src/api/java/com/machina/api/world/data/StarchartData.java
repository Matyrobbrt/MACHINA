/**
 * This file is part of the Machina Minecraft (Java Edition) mod and is licensed
 * under the MIT license:
 *
 * MIT License
 *
 * Copyright (c) 2021 Machina Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * If you want to contribute please join https://discord.com/invite/x9Mj63m4QG.
 * More information can be found on Github: https://github.com/Cy4Shot/MACHINA
 */

package com.machina.api.world.data;

import static com.machina.api.ModIDs.MACHINA;

import javax.annotation.WillNotClose;

import com.machina.api.network.BaseNetwork;
import com.machina.api.network.machina.message.S2CSyncStarchart;
import com.machina.api.starchart.Starchart;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class StarchartData extends WorldSavedData {

	private Starchart starchart = new Starchart();
	private boolean isGenerated;

	public StarchartData(String n) {
		super(n);
		starchart = new Starchart();
		isGenerated = false;
	}

	private static final String ID = MACHINA + "_starchart";

	public static StarchartData getDefaultInstance(@WillNotClose MinecraftServer server) {
		ServerWorld w = server.getLevel(World.OVERWORLD);
		return w.getDataStorage().computeIfAbsent(() -> new StarchartData(ID), ID);
	}

	@Override
	public void load(CompoundNBT nbt) {
		isGenerated = nbt.getBoolean("generated");
		starchart.deserializeNBT(nbt);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.putBoolean("generated", isGenerated);
		nbt.putString("dataOwnerMod", MACHINA);
		return starchart.serializeNBT(nbt);
	}

	public void setStarchart(Starchart sc) {
		starchart = sc;
		this.setDirty();
	}

	public Starchart getStarchart() { return starchart; }

	public void setGenerated(boolean gen) {
		isGenerated = gen;
		this.setDirty();
	}

	public boolean getGenerated() { return isGenerated; }

	public void setStarchartIfNull(Starchart sc) {
		if (starchart.planets.size() == 0) {
			setStarchart(sc);
		}
	}

	public void syncClients() {
		BaseNetwork.sendToAll(BaseNetwork.MACHINA_CHANNEL, new S2CSyncStarchart(starchart));
	}

	public void syncClient(ServerPlayerEntity e) {
		BaseNetwork.sendTo(BaseNetwork.MACHINA_CHANNEL, new S2CSyncStarchart(starchart), e);
	}

	public void generateIf(long seed) {
		if (!isGenerated) {
			starchart.generateStarchart(seed);
			isGenerated = true;
			this.setDirty();
			syncClients();
		}
	}

	public static Starchart getStarchartForServer(MinecraftServer server) {
		return StarchartData.getDefaultInstance(server).getStarchart();
	}

}
