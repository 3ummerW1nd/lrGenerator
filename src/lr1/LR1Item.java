package lr1;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;

/**
 * @program: lr1.LRGenerator
 * @description:
 * @author: 3ummerW1nd
 * @create: 2022-06-20 00:19
 **/

public class LR1Item {
    private final String leftSide;
    private final String[] rightSide;
    private final int dotPointer;
    private final HashSet<String> lookahead;

    LR1Item(String leftSide, String[] rightSide, int dotPointer,
            HashSet<String> lookahead) {
        this.leftSide = leftSide;
        this.rightSide = rightSide;
        this.dotPointer = dotPointer;
        this.lookahead = lookahead;
    }

    public String getCurrent() {
        if (dotPointer == rightSide.length) {
            return null;
        }
        return rightSide[dotPointer];
    }

    public int getDotPointer() {
        return dotPointer;
    }

    public String[] getRightSide() {
        return rightSide;
    }

    public HashSet<String> getLookahead() {
        return lookahead;
    }

    public String getLeftSide() {
        return leftSide;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LR1Item lr1Item = (LR1Item) o;
        return dotPointer == lr1Item.dotPointer &&
                Objects.equals(lookahead, lr1Item.lookahead) &&
                Objects.equals(leftSide, lr1Item.leftSide) &&
                Arrays.equals(rightSide, lr1Item.rightSide);
    }

    public boolean equalLR0(LR1Item item) {
        return leftSide.equals(item.getLeftSide()) && Arrays.equals(rightSide, item.getRightSide()) && dotPointer == item.getDotPointer();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.dotPointer;
        hash = 31 * hash + Objects.hashCode(this.leftSide);
        hash = 31 * hash + Arrays.deepHashCode(this.rightSide);
        hash = 31 * hash + Objects.hashCode(this.lookahead);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(leftSide + " -> ");
        for (int i = 0; i < rightSide.length; i++) {
            if (i == dotPointer) {
                str.append(".");
            }
            str.append(rightSide[i]);
            if (i != rightSide.length - 1) {
                str.append(" ");
            }
        }
        if (rightSide.length == dotPointer) {
            str.append(".");
        }
        str.append(" , ").append(lookahead);
        return str.toString();
    }
}
