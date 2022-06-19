package lr1;

import java.util.Objects;

/**
 * @program: lr1.LRGenerator
 * @description:
 * @author: 3ummerW1nd
 **/

public class Action {
    private final ActionType type;
    private final int operand;

    Action(ActionType type, int operand) {
        this.type = type;
        this.operand = operand;
    }

    public ActionType getType() {
        return type;
    }

    public int getOperand() {
        return operand;
    }

    public ActionType type() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Action that = (Action) obj;
        return Objects.equals(this.type, that.type) &&
                this.operand == that.operand;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, operand);
    }

    @Override
    public String toString() {
        return "lr1.Action[" +
                "type=" + type + ", " +
                "operand=" + operand + ']';
    }


    enum ActionType {
        ACCEPT, SHIFT, REDUCE
    }
}
