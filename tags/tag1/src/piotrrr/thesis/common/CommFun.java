package piotrrr.thesis.common;

import soc.qase.tools.vecmath.Vector3f;

/**
 * Common functions that may be used by bots.
 * @author Piotr Gwizda³a
 */
public class CommFun {
	
	/**
	 * Returns the distance between the given positions.
	 * @param from position from
	 * @param to position to
	 * @return the distance between the given positions.
	 */
	public static float getDistanceBetweenPositions(Vector3f from, Vector3f to) {
		Vector3f distance = new Vector3f();
		distance.sub(from, to);
		return distance.length();
	}
	
	/**
	 * Gets the normalized vector with the root in "from" and directed towards "to"
	 * @param from - the position of the beginning of the vector
	 * @param to - the point at which vector will direct.
	 * @return the directing vector with length = 1 (normalized). 
	 */
	public static Vector3f getNormalizedDirectionVector(Vector3f from, Vector3f to) {
		Vector3f result = new Vector3f(to.x-from.x,	to.y-from.y, to.z - from.z);
		result.normalize();
		return result;
	}
	
	/**
	 * Multiplies the given vector by given scalar
	 * @param v
	 * @param scalar
	 * @return
	 */
	public static Vector3f multiplyVectorByScalar(Vector3f v, float scalar) {
		return new Vector3f(v.x*scalar, v.y*scalar,v.z*scalar);
	}
	
	/**
	 * Gets the predicted movement vector based on two positions.
	 * @param from - beginning of the movement
	 * @param to - actual position of the movement
	 * @return the vector connecting actual position of the movement with the predicted position.
	 */
	public static Vector3f getMovementBetweenVectors(Vector3f from, Vector3f to) {
		return new Vector3f((to.x-from.x), (to.y-from.y), (to.z - from.z));
	}

}
