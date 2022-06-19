package lr1;

import java.util.Arrays;
import java.util.Objects;

/**
 * @program: lr1.LRGenerator
 * @description:
 * @author: 3ummerW1nd
 **/

public class Rule {
    private String leftSide;
    private String[] rightSide;

    public Rule(String leftSide, String[] rightSide) {
        this.rightSide = rightSide;
        this.leftSide = leftSide;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public String[] getRightSide() {
        return rightSide;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + Objects.hashCode(this.leftSide);
        hash = 29 * hash + Arrays.deepHashCode(this.rightSide);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rule other = (Rule) obj;
        if (!Objects.equals(this.leftSide, other.leftSide)) {
            return false;
        }
        return Arrays.deepEquals(this.rightSide, other.rightSide);
    }
}