package com.cy4.machina.client;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class RenderingUtils {
	public static final float[][] CUBE_VERTICES = new float[][] {
			// SOUTH
			{ -1f, 1f, 1f }, { -1f, -1f, 1f }, { 1f, -1f, 1f }, { 1f, 1f, 1f },

			// NORTH
			{ -1f, 1f, -1f }, { -1f, -1f, -1f }, { 1f, -1f, -1f }, { 1f, 1f, -1f },

			// EAST
			{ 1f, 1f, -1f }, { 1f, -1f, -1f }, { 1f, -1f, 1f }, { 1f, 1f, 1f },

			// WEST
			{ -1f, -1f, 1f }, { -1f, -1f, -1f }, { -1f, 1f, -1f }, { -1f, 1f, 1f },

			// TOP
			{ 1f, 1f, -1f }, { 1f, 1f, 1f }, { -1f, 1f, 1f }, { -1f, 1f, -1f },

			// BOTTOM
			{ 1f, -1f, -1f }, { 1f, -1f, 1f }, { -1f, -1f, 1f }, { -1f, -1f, -1f } };

	public static void makeCube(BufferBuilder buffer, double radius) {
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

		for (int i = 0; i < CUBE_VERTICES.length; i++) {
			buffer.vertex(CUBE_VERTICES[i][0] * radius, CUBE_VERTICES[i][1] * radius, CUBE_VERTICES[i][2] * radius)
					.uv(0, 0).endVertex();
		}
	}

	public static void makeCylinder(BufferBuilder buffer, int segments, double height, double radius) {
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
		for (int i = 0; i < segments; i++) {
			double a1 = (double) i * Math.PI * 2.0 / (double) segments;
			double a2 = (double) (i + 1) * Math.PI * 2.0 / (double) segments;
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
}
