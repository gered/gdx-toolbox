package com.blarg.gdx.graphics.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class SimpleTextureShaderProgram extends ShaderProgram {
	public static final String VERTEX_SHADER =
			"attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
			"uniform mat4 u_projectionViewMatrix;\n" +
			"varying vec2 v_texCoords;\n" +
			"\n" +
			"void main()\n" +
			"{\n" +
			"	v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
			"	gl_Position =  u_projectionViewMatrix * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"}\n";

	public static final String FRAGMENT_SHADER =
			"#ifdef GL_ES\n" +
			"	precision mediump float;\n" +
			"#endif\n" +
			"varying vec2 v_texCoords;\n" +
			"uniform sampler2D u_texture;\n" +
			"\n" +
			"void main()\n" +
			"{\n" +
			"	gl_FragColor = texture2D(u_texture, v_texCoords);\n" +
			"}\n";

	public SimpleTextureShaderProgram() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}
}
