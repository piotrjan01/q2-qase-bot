package piotrrr.thesis.movement;

import soc.qase.tools.vecmath.Vector3f;

public class MoveInstructions {

	public Vector3f moveDir;
	public Vector3f aimDir;
	public float velocity;
	
	/**
	 * These two following fields are set according to PlayerMove 
	 * class from QASE library.
	 */
	public int postureState;
	public int walkState;
	
	public MoveInstructions(Vector3f movDir, Vector3f aimDir,
			float velocity, int postureState, int walkState) {
		this.moveDir = movDir;
		this.aimDir = aimDir;
		this.velocity = velocity;
		this.postureState = postureState;
		this.walkState = walkState;
	}	
}
