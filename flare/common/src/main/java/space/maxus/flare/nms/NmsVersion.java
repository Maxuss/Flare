package space.maxus.flare.nms;


/**
 * Contains different NMS versions
 */
public enum NmsVersion {
    v1_19_R1,
    v1_19_R2,
    v1_19_R3, // current latest supported
    /**
     * Undetermined version, reflection adapter will be used instead
     */
    UNKNOWN,
    /**
     * Undetermined version, but it does not contain version infix (e.g. 1_19_R3)
     */
    UNPREFIXED,
}
