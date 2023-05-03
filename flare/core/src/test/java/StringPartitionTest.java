import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import space.maxus.flare.util.FlareUtil;

import java.util.Collections;
import java.util.List;

@Slf4j
class StringPartitionTest {
    @Test
    void testNoTagStringPartition() {
        List<String> partitioned = FlareUtil.partitionString("This is a normal string without any tags, will this work? Who knows!");
        Assertions.assertEquals(List.of(
                "This is a normal string without any",
                "tags, will this work? Who knows!"
        ), partitioned);
    }

    @Test
    void testSmallTagStringPartition() {
        List<String> partitioned = FlareUtil.partitionString("This is a small <red>string!");
        Assertions.assertEquals(Collections.singletonList("This is a small <red>string!"), partitioned);
    }

    @Test
    void testTagStringPartition() {
        List<String> partitioned = FlareUtil.partitionString("This is a <gold>string</gold> with tags. Contains <rainbow>several<gray> tags.");
        Assertions.assertEquals(List.of(
                "This is a <gold>string</gold> with tags. Contains",
                "<rainbow>several<gray> tags."
        ), partitioned);
    }
}
