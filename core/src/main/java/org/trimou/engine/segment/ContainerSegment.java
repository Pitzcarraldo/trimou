package org.trimou.engine.segment;

import java.util.List;

import org.trimou.annotations.Internal;

/**
 * Segment which contains other segments.
 *
 * @author Martin Kouba
 */
@Internal
public interface ContainerSegment extends Segment, Iterable<Segment> {

    /**
     *
     * @return the immutable list of segments
     */
    public List<Segment> getSegments();

    /**
     *
     * @param recursive
     * @return the number of segments
     */
    public int getSegmentsSize(boolean recursive);

}
