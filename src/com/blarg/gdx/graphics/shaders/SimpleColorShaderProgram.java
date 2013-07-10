package com.blarg.gdx.graphics.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class SimpleColorShaderProgram extends ShaderProgram {
	public static final String VERTEX_SHADER =
			"attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
			"uniform mat4 u_projectionViewMatrix;\n" +
			"varying vec4 v_color;\n" +
			"\n" +
			"void main()\n" +
			"{\n" +
			"	v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
			"	gl_Position =  u_projectionViewMatrix * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
			"}\n";

	public static final String FRAGMENT_SHADER =
			"#ifdef GL_ES\n" +
			"	#define LOWP lowp\n" +
			"	precision mediump float;\n" +
			"#else\n" +
			"	#define LOWP\n" +
			"#endif\n" +
			"varying LOWP vec4 v_color;\n" +
			"\n" +
			"void main()\n" +
			"{\n" +
			"	gl_FragColor = v_color;\n" +
			"}\n";

	public SimpleColorShaderProgram() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}
}
