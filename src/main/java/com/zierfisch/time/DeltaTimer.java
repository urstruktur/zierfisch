package com.zierfisch.time;

import java.util.concurrent.TimeUnit;

public class DeltaTimer {

	private static final double NANOS_TO_SECS_FACTOR = getConversionFactor(TimeUnit.SECONDS);

	private long originTime;
	private long referenceTime;

	public DeltaTimer() {
		resetOriginTime();
		referenceTime = originTime;
	}

	public void resetOriginTime() {
		originTime = System.nanoTime();
	}

	public void setReference() {
		referenceTime = System.nanoTime();
	}

	/**
	 * <p>
	 * Gets the time since origin time in the given time unit.
	 * </p>
	 * 
	 * <p>
	 * Origin time is originally set to the creation time of this timer,
	 * but can be reset with <code>resetOriginTime()</code>.
	 * </p>
	 * 
	 * @param unit The unit in which to return the time
	 * 
	 * @return time since origin time in the given unit
	 */
	public float getTime(TimeUnit unit) {
		long time = System.nanoTime() - originTime;
		return (float) (getConversionFactor(unit) * time);
	}

	/**
	 *  <p>
	 * Gets the time since origin time in seconds.
	 * </p>
	 * 
	 * <p>
	 * Origin time is originally set to the creation time of this timer,
	 * but can be reset with <code>resetOriginTime()</code>.
	 * </p>
	 * 
	 * @return
	 */
	public float getTime() {
		long time = System.nanoTime() - originTime;
		return (float) (NANOS_TO_SECS_FACTOR * time);
	}

	/**
	 * <p>
	 * Gets the time since reference time in the given time unit.
	 * </p>
	 * 
	 * <p>
	 * Reference time is originally set to the creation time of this timer,
	 * but can be reset at any time with <code>setReference()</code>.
	 * </p>
	 * 
	 * @param unit the unit in which to return the delta time
	 * 
	 * @return Time since reference time in the given time unit
	 */
	public float getDelta(TimeUnit unit) {
		long delta = System.nanoTime() - referenceTime;
		return (float) (getConversionFactor(unit) * delta);
	}

	/**
	 * <p>
	 * Gets the time since reference time in seconds.
	 * </p>
	 * 
	 * <p>
	 * Reference time is originally set to the creation time of this timer,
	 * but can be reset at any time with <code>setReference()</code>.
	 * </p>
	 * 
	 * @return
	 */
	public float getDelta() {
		return (float) ((System.nanoTime() - referenceTime) * NANOS_TO_SECS_FACTOR);
	}
	
	/**
	 * Gets a conversion factor from nanoseconds to the given time unit.
	 * 
	 * @param unit
	 *            time unit to convert to
	 * @return the factor
	 */
	private static double getConversionFactor(TimeUnit unit) {
		long dividend = 1;
	
		switch (unit) {
		case DAYS:
			// falls through, operations add up
			dividend *= 24;
	
		case HOURS:
			// falls through
			dividend *= 60;
	
		case MINUTES:
			// falls through
			dividend *= 60;
	
		case SECONDS:
			// falls through
			dividend *= 1000;
	
		case MILLISECONDS:
			// falls through
			dividend *= 1000;
	
		case MICROSECONDS:
			// falls through
			dividend *= 1000;
	
		case NANOSECONDS:
			return 1f / dividend;
	
		default:
			throw new RuntimeException(
					"Cannot convert to this unit. " + "What is it anyway, I thought I handled all of them?");
		}
	}

}
