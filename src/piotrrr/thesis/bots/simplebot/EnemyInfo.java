package piotrrr.thesis.bots.simplebot;

import piotrrr.thesis.common.CommFun;
import piotrrr.thesis.common.GameObject;
import piotrrr.thesis.tools.Dbg;
import soc.qase.info.Config;
import soc.qase.state.Effects;
import soc.qase.state.Entity;
import soc.qase.state.Events;
import soc.qase.state.Inventory;
import soc.qase.state.Sound;
import soc.qase.tools.vecmath.Vector3f;

public class EnemyInfo implements GameObject {
	
	public static final int MAX_ENEMY_INFO_AGE = 50;
	
	Entity ent;
	
	Vector3f lastPos = null;
	
	long lastUpdateFrame = 0L;
	
	float lastPredictionError = Float.MAX_VALUE;
	
	Vector3f predictedPos = null;
	
	public EnemyInfo(Entity ent, long frame) {
		this.ent = ent.deepCopy();
		this.lastUpdateFrame = frame;
	}

	@Override
	public Vector3f getPosition() {
		return ent.getPosition();
	}

	@Override
	public String toDetailedString() {
		return "Enemy name: "+ent.getName()+"\n"+
				"last update frame: "+lastUpdateFrame+"\n"+
				"position: "+ent.getPosition()+"\n"+
				"last position: "+lastPos+"\n"+
				"predicted position: "+predictedPos+"\n"+
				"last prediction error: "+lastPredictionError+"\n"+
				"movement: "+getMovement()+"\n"+
				"gun: "+CommFun.getGunName(ent.getWeaponInventoryIndex())+"\n"+
				"entity: "+ent.getCategory()+"."+ent.getType()+"."+ent.getSubType()+"\n"+
				"skin: "+ent.getSkin()+"\n"+
				"active: "+ent.getActive()+"\n"+
				"origin: "+ent.getOrigin()+"\n"+
				"old origin: "+ent.getOldOrigin();
//				"effects string: "+getEffectsList();
	}
	
	public boolean updateEnemyInfo(Entity e, long frameNumber) {
		if (ent.getNumber() != e.getNumber()) return false;
		if ( ! ent.getName().equalsIgnoreCase(e.getName())) return false;
		
		if (e.getActive() == ent.getActive() && e.getPosition().equals(ent.getPosition())) return false;
		
		lastPos = ent.getPosition();
		ent = e.deepCopy();
		lastUpdateFrame = frameNumber;
		
		lastPredictionError = getPredictionError();
		
		predictedPos = predictPositionBasingOnMovement();
		
//		if ( ! getSounds().equals("")) {
//			Dbg.err("Sound!\n"+getSounds()+"\n"+toDetailedString());
//		}
//		
		return true;
		
	}
	
	private Vector3f predictPositionBasingOnMovement() {
		Vector3f movement = CommFun.getMovementBetweenVectors(lastPos, getPosition());
		Vector3f ret = CommFun.cloneVector(getPosition());
		ret.add(movement);
		return ret;
	}
	
	private float getPredictionError() {
		if (predictedPos == null) return Float.MAX_VALUE;
		return CommFun.getDifferenceBetweenVectors(getPosition(), predictedPos);
	}
	
	private float getMovement() {
		if (lastPos == null) return Float.MAX_VALUE;
		return CommFun.getDifferenceBetweenVectors(lastPos, getPosition());
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return ent.getName();
	}
	
	public long getInfoAge(long currentFrame) {
		return currentFrame - lastUpdateFrame;
	}
	
	public boolean isOutdated(long currentFrame) {
		return (getInfoAge(currentFrame) > MAX_ENEMY_INFO_AGE);
	}
	
	private String getEffectsList() {
		Effects ef = ent.getEffects();
		return ef.getEffectsString();
	}
	
	private String getSounds() {
		Sound s = ent.getSound();
		if (s == null) return "";
		if (ent.getConfig() == null) return "";
		ent.getConfig().getSoundString(s.getIndex());
		return "nr: "+s.getEntityNumber()+" str: "+ent.getConfig().getSoundString(s.getIndex());
	}
	
	


}
