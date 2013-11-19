package ca.blarg.gdx.graphics.shaders;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class DebugShader extends ShaderProgram {
	public static final String VERTEX_SHADER =
			"attribute vec4 a_position;\n" +
			"attribute vec4 a_color;\n" +
			"uniform mat4 u_projModelView;\n" +
			"varying vec4 v_color;\n" +
			"\n" +
			"void main()\n" +
			"{\n" +
			"    v_color = a_color;\n" +
			"    gl_PointSize = 4.0;\n" +
			"    gl_Position =  u_projModelView * a_position;\n" +
			"}";

	public static final String FRAGMENT_SHADER =
			"#ifdef GL_ES\n" +
			"    #define LOWP lowp\n" +
			"    precision mediump float;\n" +
			"#else\n" +
			"    #define LOWP\n" +
			"#endif\n" +
			"varying LOWP vec4 v_color;\n" +
			"\n" +
			"void main()\n" +
			"{\n" +
			"    gl_FragColor = v_color;\n" +
			"}\n";

	public DebugShader() {
		super(VERTEX_SHADER, FRAGMENT_SHADER);
	}
}
