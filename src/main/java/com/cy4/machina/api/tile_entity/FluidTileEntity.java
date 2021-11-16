package com.cy4.machina.api.tile_entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.cy4.machina.api.inventory.IFluidInventory;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public abstract class FluidTileEntity extends BaseTileEntity implements IFluidInventory {

	protected FluidTank tank = createTank();
	private final LazyOptional<IFluidHandler> holder = LazyOptional.of(() -> tank);

	protected FluidTileEntity(TileEntityType<?> teType) {
		super(teType);
	}

	protected abstract FluidTank createTank();

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		tank.readFromNBT(tag);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		tag = super.save(tag);
		tank.writeToNBT(tag);
		return tag;
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return holder.cast();
		return super.getCapability(capability, facing);
	}

	@Override
	public int getTanks() { return tank.getTanks(); }

	@Override
	public FluidStack getFluidInTank(int tank) {
		return this.tank.getFluidInTank(tank);
	}

	@Override
	public int getTankCapacity(int tank) {
		return this.tank.getTankCapacity(tank);
	}

	@Override
	public boolean isFluidValid(int tank, FluidStack stack) {
		return this.tank.isFluidValid(tank, stack);
	}

	@Override
	public int fill(FluidStack resource, FluidAction action) {
		return tank.fill(resource, action);
	}

	@Override
	public FluidStack drain(FluidStack resource, FluidAction action) {
		return tank.drain(resource, action);
	}

	@Override
	public FluidStack drain(int maxDrain, FluidAction action) {
		return tank.drain(maxDrain, action);
	}

}