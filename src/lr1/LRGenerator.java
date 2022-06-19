package lr1;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: lr1.LRGenerator
 * @description:
 * @author: 3ummerW1nd
 **/

public class LRGenerator {
    private final Deque<Rule> reduce = new LinkedList<>();
    private List<HashMap<String, Integer>> goToTable;
    private List<HashMap<String, Action>> actionTable;
    private final Grammar grammar;
    private ArrayList<LR1State> canonicalCollection;

    public LRGenerator(Grammar grammar) {
        this.grammar = grammar;
    }

    public static void main(String[] args) {
        String grammarText = new Scanner(System.in).nextLine();
        Grammar grammar = new Grammar(grammarText);
        LRGenerator lrGenerator = new LRGenerator(grammar);
        lrGenerator.generate();
        List<Rule> rules = grammar.getRules();
        rules.forEach(rule -> {
            System.out.print("rules.add(new lr1.Rule(\"" + rule.getLeftSide() + "\", new String[]{");
            for (int i = 0; i < rule.getRightSide().length; i ++) {
                if (i != rule.getRightSide().length - 1) {
                    System.out.print("\"" + rule.getRightSide()[i] + "\", ");
                } else {
                    System.out.println("\"" + rule.getRightSide()[i] + "\"}));");
                }
            }
        });
        AtomicInteger cnt = new AtomicInteger();
        AtomicInteger finalCnt = cnt;
        lrGenerator.actionTable.forEach(a -> {
            System.out.println("Map<String, lr1.Action> map" + finalCnt + " = new HashMap<>();");
            a.forEach((s, action) -> {
                System.out.println("map" + finalCnt + ".put(\"" + s + "\", new lr1.Action(ActionType." + action.getType() + ", " + action.getOperand() + "));");
            });
            System.out.println("actionTable.add(map" + finalCnt + ");");
            finalCnt.getAndIncrement();
        });
        cnt = new AtomicInteger(0);
        AtomicInteger finalCnt1 = cnt;
        lrGenerator.goToTable.forEach(stringIntegerHashMap -> {
            System.out.println("Map<String, Integer> map" + finalCnt1 + " = new HashMap<>();");
            stringIntegerHashMap.forEach((s, integer) -> {
                System.out.println("map" + finalCnt1 + ".put(\"" + s + "\", " + integer + ");");
            });
            System.out.println("goToTable.add(map" + finalCnt1 + ");");
            finalCnt1.getAndIncrement();
        });
    }

    protected void generateCanonical() {
        canonicalCollection = new ArrayList<>();
        HashSet<LR1Item> start = new HashSet<>();
        Rule startRule = grammar.getRules().get(0);
        HashSet<String> startLockahead = new HashSet<>();
        startLockahead.add("$");
        start.add(new LR1Item(startRule.getLeftSide(), startRule.getRightSide(), 0, startLockahead));

        LR1State startState = new LR1State(grammar, start);
        canonicalCollection.add(startState);

        for (int i = 0; i < canonicalCollection.size(); i++) {
            HashSet<String> stringWithDot = new HashSet<>();
            for (LR1Item item : canonicalCollection.get(i).getItems()) {
                if (item.getCurrent() != null) {
                    stringWithDot.add(item.getCurrent());
                }
            }
            for (String str : stringWithDot) {
                HashSet<LR1Item> nextStateItems = new HashSet<>();
                for (LR1Item item : canonicalCollection.get(i).getItems()) {

                    if (item.getCurrent() != null && item.getCurrent().equals(str)) {
                        LR1Item temp = new LR1Item(item.getLeftSide(), item.getRightSide(), item.getDotPointer() + 1, item.getLookahead());
                        nextStateItems.add(temp);
                    }
                }
                LR1State nextState = new LR1State(grammar, nextStateItems);
                boolean isExist = false;
                for (LR1State lr1State : canonicalCollection) {
                    if (lr1State.getItems().containsAll(nextState.getItems())
                            && nextState.getItems().containsAll(lr1State.getItems())) {
                        isExist = true;
                        canonicalCollection.get(i).getTransition().put(str, lr1State);
                    }
                }
                if (!isExist) {
                    canonicalCollection.add(nextState);
                    canonicalCollection.get(i).getTransition().put(str, nextState);
                }
            }
        }

    }

    public void generate() {
        generateCanonical();
        generateGoto();
        generateAction();
    }

    private void generateGoto() {
        goToTable = new ArrayList<>();
        for (int i = 0; i < canonicalCollection.size(); i++) {
            goToTable.add(new HashMap<>());
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (String s : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.isVariable(s)) {
                    goToTable.get(i).put(s, findStateIndex(canonicalCollection.get(i).getTransition().get(s)));
                }
            }
        }
    }

    private int findStateIndex(LR1State state) {
        for (int i = 0; i < canonicalCollection.size(); i++) {
            if (canonicalCollection.get(i).equals(state)) {
                return i;
            }
        }
        return -1;
    }

    private void generateAction() {
        actionTable = new ArrayList<>();
        for (int i = 0; i < canonicalCollection.size(); i++) {
            actionTable.add(new HashMap<>());
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (String s : canonicalCollection.get(i).getTransition().keySet()) {
                if (grammar.getTerminals().contains(s)) {
                    actionTable.get(i).put(s, new Action(Action.ActionType.SHIFT, findStateIndex(canonicalCollection.get(i).getTransition().get(s))));
                }
            }
        }
        for (int i = 0; i < canonicalCollection.size(); i++) {
            for (LR1Item item : canonicalCollection.get(i).getItems()) {
                if (item.getDotPointer() == item.getRightSide().length) {
                    if ("S'".equals(item.getLeftSide())) {
                        actionTable.get(i).put("$", new Action(Action.ActionType.ACCEPT, 0));
                    } else {
                        Rule rule = new Rule(item.getLeftSide(), item.getRightSide().clone());
                        int index = grammar.findRuleIndex(rule);
                        Action action = new Action(Action.ActionType.REDUCE, index);
                        for (String str : item.getLookahead()) {
                            actionTable.get(i).putIfAbsent(str, action);
                        }
                    }
                }
            }
        }
    }
}
