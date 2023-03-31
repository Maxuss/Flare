package space.maxus.flare.ui;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Slot {
    private int row;
    private int column;

    public int rawSlot() {
        return row * 9 + column;
    }
}
