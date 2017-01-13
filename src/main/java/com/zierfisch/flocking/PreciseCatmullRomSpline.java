package com.zierfisch.flocking;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;


/**
 * @see http://badlogicgames.com/forum/viewtopic.php?f=11&t=14466&p=63828#p63828
 */
public class PreciseCatmullRomSpline<T extends Vector<T>> extends CatmullRomSpline<T> {

    public T[] precisionPoints;
    private int precisionPointsCount;

    public PreciseCatmullRomSpline(Class<T> type, final T[] controlPoints, final boolean continuous, float maxDistance) {
        set(controlPoints, continuous);
        calculatePrecisionPoints(controlPoints, maxDistance, type);
    }

    private void calculatePrecisionPoints(T[] controlPoints, double maxDistance, Class<T> type) {
        List<Vector<T>> precisionPointsList = new ArrayList<Vector<T>>();
        precisionPointsList.add(controlPoints[1 % spanCount]);
        for (int lastPoint = 1 % spanCount; lastPoint < spanCount + 1; lastPoint++) {
            int nextPoint = lastPoint + 1;
            final T previous = controlPoints[lastPoint];
            final T next = controlPoints[nextPoint];
            final float dstNext = previous.dst(next);

            if (dstNext > maxDistance) {
                int splitLineIntoNumberOfPoints = (int) (dstNext / maxDistance);
                for (int j = 0; j < splitLineIntoNumberOfPoints - 1; j++) {
                    T precisionPoint = newVector(type);
                    double distanceOnSpan = maxDistance + maxDistance * j;
                    valueAt(precisionPoint, lastPoint - 1, (float) distanceOnSpan / dstNext);
                    precisionPointsList.add(precisionPoint);
                }
            }
            precisionPointsList.add(next);
        }

        precisionPoints = (T[]) Array.newInstance(type, precisionPointsList.size());
        precisionPoints = precisionPointsList.toArray(precisionPoints);
        precisionPointsCount = precisionPoints.length;
    }

    private T newVector(Class<T> type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public int nearestPrecisionPoint(final T in) {
        return nearestPrecisionPoint(in, 0, precisionPointsCount);
    }

    /**
     * @return The span closest to the specified value, restricting to the specified spans.
     */
    private int nearestPrecisionPoint(final T in, int start, final int count) {
        while (start < 0) {
            start += precisionPointsCount;
        }
        int result = start % precisionPointsCount;
        float dst = in.dst2(precisionPoints[result]);
        for (int i = 1; i < count; i++) {
            final int idx = (start + i) % precisionPointsCount;
            final float d = in.dst2(precisionPoints[idx]);
            if (d < dst) {
                dst = d;
                result = idx;
            }
        }
        return result;
    }

    @Override
    public float locate(T v) {
        return locate(v, nearestPrecisionPoint(v));
    }

    private float locate(T in, int near) {
        int n = near;
        final T nearest = precisionPoints[n];
        final T previous = precisionPoints[n > 0 ? n - 1 : precisionPointsCount - 1];
        final T next = precisionPoints[(n + 1) % precisionPointsCount];
        final float dstPrev2 = in.dst2(previous);
        final float dstNext2 = in.dst2(next);
        T P1, P2, P3;
        if (dstNext2 < dstPrev2) {
            P1 = nearest;
            P2 = next;
            P3 = in;
        } else {
            P1 = previous;
            P2 = nearest;
            P3 = in;
            n = n > 0 ? n - 1 : precisionPointsCount - 1;
        }
        float L1Sqr = P1.dst2(P2);
        float L2Sqr = P3.dst2(P2);
        float L3Sqr = P3.dst2(P1);
        float L1 = (float) Math.sqrt(L1Sqr);
        float s = (L2Sqr + L1Sqr - L3Sqr) / (2f * L1);
        float u = MathUtils.clamp((L1 - s) / L1, 0f, 1f);
        return ((float) n + u) / precisionPointsCount;
    }
}