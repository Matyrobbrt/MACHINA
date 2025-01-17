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

package com.machina.client.util;

import java.util.function.Consumer;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuadRenderingUtils {
	public static final float[][] CUBE_VERTICES = new float[][] {

		// NORTH
		{ -1f, 1f, -1f }, { -1f, -1f, -1f }, { 1f, -1f, -1f }, { 1f, 1f, -1f },

		// EAST
		{ 1f, 1f, -1f }, { 1f, -1f, -1f }, { 1f, -1f, 1f }, { 1f, 1f, 1f },

		// WEST
		{ -1f, -1f, 1f }, { -1f, -1f, -1f }, { -1f, 1f, -1f }, { -1f, 1f, 1f },

		// TOP
		{ 1f, 1f, -1f }, { 1f, 1f, 1f }, { -1f, 1f, 1f }, { -1f, 1f, -1f },

		// SOUTH
		{ 1f, 1f, 1f }, { 1f, -1f, 1f }, { -1f, -1f, 1f }, { -1f, 1f, 1f },

		// BOTTOM
		{ -1f, -1f, 1f }, { 1f, -1f, 1f }, { 1f, -1f, -1f }, { -1f, -1f, -1f },

	};

	public static VertexBuffer createBuffer(VertexBuffer b, Consumer<BufferBuilder> r) {
		BufferBuilder bb = Tessellator.getInstance().getBuilder();
		b = new VertexBuffer(DefaultVertexFormats.POSITION_TEX);
		r.accept(bb);
		bb.end();
		b.upload(bb);
		return b;
	}

	public static void makeCube(BufferBuilder buffer, float radius) {
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		for (int i = 0; i < CUBE_VERTICES.length; i++) {
			buffer.vertex(CUBE_VERTICES[i][0] * radius, CUBE_VERTICES[i][1] * radius, CUBE_VERTICES[i][2] * radius)
			.uv(Math.floorDiv(i, 2) % 2 == 0 ? 0 : 1, Math.floorDiv(i + 1, 2) % 2 == 0 ? 0 : 1).endVertex();
		}
	}

	public static void makeCylinder(BufferBuilder buffer, int segments, double height, double radius) {
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for (int i = 0; i < segments; i++) {
			double a1 = i * Math.PI * 2.0 / segments;
			double a2 = (i + 1) * Math.PI * 2.0 / segments;
			double px1 = Math.sin(a1) * radius;
			double pz1 = Math.cos(a1) * radius;
			double px2 = Math.sin(a2) * radius;
			double pz2 = Math.cos(a2) * radius;

			float u0 = (float) i / (float) segments;
			float u1 = (float) (i + 1) / (float) segments;

			buffer.vertex(px1, -height, pz1).uv(u0, 0).endVertex();
			buffer.vertex(px1, height, pz1).uv(u0, 1).endVertex();
			buffer.vertex(px2, height, pz2).uv(u1, 1).endVertex();
			buffer.vertex(px2, -height, pz2).uv(u1, 0).endVertex();
		}
	}

	@SuppressWarnings("deprecation")
	public static void renderBuffer(MatrixStack matrixStackIn, VertexBuffer buffer, VertexFormat format, float r,
			float g, float b, float a) {
		RenderSystem.color4f(r, g, b, a);
		buffer.bind();
		format.setupBufferState(0L);
		buffer.draw(matrixStackIn.last().pose(), GL11.GL_QUADS);
		VertexBuffer.unbind();
		format.clearBufferState();
	}
}
