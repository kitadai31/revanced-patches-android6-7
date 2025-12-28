package app.revanced.extension.shared.patches.litho;

import java.nio.ByteBuffer;

public class LithoBufferFix {
    public static ByteBuffer validateBuffer(int index, ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        if (index > buffer.limit() - 4) {
            return null;
        }
        return buffer;
    }
}
