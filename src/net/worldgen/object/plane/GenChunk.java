package net.worldgen.object.plane;

import net.worldgen.object.raw.RawModel;
import net.worldgen.util.Loader;

public class GenChunk {
	
	private static final int WIDTH = 32;
	private static final int WIDTHP1 = WIDTH+1;
	
	private static final int INDICES = WIDTH*WIDTH*6;			//	16 * 16 * 6
	private static final int VERTICES = WIDTHP1*WIDTHP1*3;		//	17 * 17 * 3
	private static final int NORMALS = WIDTHP1*WIDTHP1*3;		//	17 * 17 * 3
	private static final int TEXCOORDS = WIDTHP1*WIDTHP1*2;		//	17 * 17 * 2
	
	public static RawModel genRawChunk(float width) {
		
		int[] indices = new int[INDICES];
		float[] vertices = new float[VERTICES];
		float[] normals = new float[NORMALS];
		float[] texCoords = new float[TEXCOORDS];
		
		int i = 0;
		int i2 = 0;
		int i3 = 0;
		for(int z = 0; z <= WIDTH; z++) {
			for(int x = 1; x <= WIDTHP1; x++) {
				i = (z*WIDTHP1+x)*3;
				i2 = (z*WIDTHP1+x)*2;
				
				vertices[i-3] = width/WIDTH*(x-1);
				vertices[i-1] = width/WIDTH*z;
				
				normals[i-2] = 1;
				
				texCoords[i2-2] = 1f/WIDTH*(x-1);
				texCoords[i2-1] = 1f/WIDTH*z;
				
				if(x < WIDTHP1 && z < WIDTH) {
					i3 = (z*WIDTH+x)*6;
					
					int v1 = z*WIDTHP1+x-1;
					int v2 = z*WIDTHP1+x;
					int v3 = (z+1)*WIDTHP1+x-1;
					int v4 = (z+1)*WIDTHP1+x;
					
					indices[i3-6] = v1;
					indices[i3-5] = v3;
					indices[i3-4] = v2;
					indices[i3-3] = v4;
					indices[i3-2] = v2;
					indices[i3-1] = v3;
					
				}
			}
		}
		
		return Loader.loadModel(indices, vertices, normals, texCoords);
	}
}
