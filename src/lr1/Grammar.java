package lr1;

import java.util.*;

/**
 * @program: lr1.LRGenerator
 * @description:
 * @author: 3ummerW1nd
 **/

public class Grammar {
    private final ArrayList<Rule> rules;
    private final HashSet<String> terminals;
    private final HashSet<String> variables;
    private String startVariable;
    private HashMap<String, HashSet<String>> firstSets;

    public Grammar(String s) {
        rules = new ArrayList<>();
        terminals = new HashSet<>();
        variables = new HashSet<>();
        int line = 0;
        for (String st : s.split("\n")) {
            String[] sides = st.split("->");
            String leftSide = sides[0].trim();
            variables.add(leftSide);
            String[] rulesRightSide = sides[1].trim().split("\\|");
            for (String rule : rulesRightSide) {
                String[] rightSide = rule.trim().split("\\s+");
                for (String terminal : rightSide) {
                    if (!"E".equals(terminal)) {
                        terminals.add(terminal);
                    }
                }

                if (line == 0) {
                    startVariable = leftSide;
                    rules.add(new Rule("S'", new String[]{startVariable}));
                }
                rules.add(new Rule(leftSide, rightSide));
                line++;
            }
        }
        for (String variable : variables) {
            terminals.remove(variable);
        }
        computeFirstSets();
        computeFollowSet();
    }

    public ArrayList<Rule> getRules() {
        return rules;
    }

    public int findRuleIndex(Rule rule) {
        for (int i = 0; i < rules.size(); i++) {
            if (rules.get(i).equals(rule)) {
                return i;
            }
        }
        return -1;
    }

    private void computeFirstSets() {
        firstSets = new HashMap<>();

        for (String s : variables) {

            HashSet<String> temp = new HashSet<>();
            firstSets.put(s, temp);
        }
        while (true) {
            boolean isChanged = false;
            for (String variable : variables) {
                HashSet<String> firstSet = new HashSet<>();
                for (Rule rule : rules) {
                    if (rule.getLeftSide().equals(variable)) {
                        HashSet<String> addAll = computeFirst(rule.getRightSide(), 0);
                        firstSet.addAll(addAll);
                    }
                }
                if (!firstSets.get(variable).containsAll(firstSet)) {
                    isChanged = true;
                    firstSets.get(variable).addAll(firstSet);
                }

            }
            if (!isChanged) {
                break;
            }
        }

        firstSets.put("S'", firstSets.get(startVariable));
    }

    private void computeFollowSet() {
        HashMap<String, HashSet<String>> followSets = new HashMap<>();
        for (String s : variables) {
            HashSet<String> temp = new HashSet<>();
            followSets.put(s, temp);
        }
        HashSet<String> start = new HashSet<>();
        start.add("$");
        followSets.put("S'", start);

        while (true) {
            boolean isChange = false;
            for (String variable : variables) {
                for (Rule rule : rules) {
                    for (int i = 0; i < rule.getRightSide().length; i++) {
                        if (rule.getRightSide()[i].equals(variable)) {
                            HashSet<String> first;
                            if (i == rule.getRightSide().length - 1) {
                                first = followSets.get(rule.getLeftSide());
                            } else {
                                first = computeFirst(rule.getRightSide(), i + 1);
                                if (first.contains("E")) {
                                    first.remove("E");
                                    first.addAll(followSets.get(rule.getLeftSide()));
                                }
                            }
                            if (!followSets.get(variable).containsAll(first)) {
                                isChange = true;
                                followSets.get(variable).addAll(first);
                            }
                        }
                    }
                }
            }
            if (!isChange) {
                break;
            }
        }
    }

    public HashSet<String> computeFirst(String[] string, int index) {
        HashSet<String> first = new HashSet<>();
        if (index == string.length) {
            return first;
        }
        if (terminals.contains(string[index]) || "E".equals(string[index])) {
            first.add(string[index]);
            return first;
        }

        if (variables.contains(string[index])) {
            first.addAll(firstSets.get(string[index]));
        }

        if (first.contains("E")) {
            if (index != string.length - 1) {
                first.remove("E");
                first.addAll(computeFirst(string, index + 1));
            }
        }
        return first;
    }

    public HashSet<Rule> getRuledByLeftVariable(String variable) {
        HashSet<Rule> variableRules = new HashSet<>();
        for (Rule rule : rules) {
            if (rule.getLeftSide().equals(variable)) {
                variableRules.add(rule);
            }
        }
        return variableRules;
    }

    public boolean isVariable(String s) {
        return variables.contains(s);
    }

    public HashSet<String> getTerminals() {
        return terminals;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.rules);
        hash = 37 * hash + Objects.hashCode(this.terminals);
        hash = 37 * hash + Objects.hashCode(this.variables);
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
        final Grammar other = (Grammar) obj;
        if (!Objects.equals(this.rules, other.rules)) {
            return false;
        }
        if (!Objects.equals(this.terminals, other.terminals)) {
            return false;
        }
        return Objects.equals(this.variables, other.variables);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Rule rule : rules) {
            str.append(rule).append("\n");
        }
        return str.toString();
    }
}
