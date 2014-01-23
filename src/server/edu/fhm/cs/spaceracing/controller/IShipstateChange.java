package edu.fhm.cs.spaceracing.controller;

/**
 * Dieses Interface enthaelt alle Aenderungen, die ein Schiff durchlaufen kann.
 * 
 * @author Sebastian Gift
 */
public interface IShipstateChange
{
	public int getForwardThrustChange();
	public void setForwardThrustChange(int forwardThrustChange);
	public int getSidewaysThrustChange();
	public void setSidewaysThrustChange(int sidewaysThrustChange);
	public boolean isFired();
	public void setFired(boolean fired);
	public double getXRotation();
	public void setXRotation(double xAngle);
	public double getYRotation();
	public void setYRotation(double yAngle);
	public int getRollingThrustChange();
	public void setRollingThrustChange(int rollingThrustChange);
	public int getPlayerId();
}