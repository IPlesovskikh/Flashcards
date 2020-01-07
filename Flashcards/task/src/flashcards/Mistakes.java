package flashcards;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Mistakes {
    private Map<String, Integer> mistakes = new LinkedHashMap<>();

    public void addCount(String card) {
        if(mistakes.containsKey(card)){
            int prevValue = mistakes.get(card);
            prevValue++;
            mistakes.replace(card, prevValue);
        } else {
            mistakes.put(card, 1);
        }
    }

    public ArrayList<String> hardestCard() {
        if (mistakes == null) {
            return null;
        }
        int max = 0;
        ArrayList<String> keys = new ArrayList<>();
        for(var entry: mistakes.entrySet()) {
            if(entry.getValue() > max) {
                max = entry.getValue();
            }
        }
        if(max == 0) {
            return null;
        }
        for(var entry: mistakes.entrySet()) {
            if(entry.getValue() == max) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public Integer getNumberOfMistakes(String card) {
        if (mistakes.containsKey(card)) {
            return mistakes.get(card);
        }
        return 0;
    }

    public void reset(){
        mistakes.clear();
    }

    public void updateAfterImport(String card, int count) {
        if (mistakes.containsKey(card)) {
            if(mistakes.get(card) != count) {
                mistakes.replace(card, count);
            }
        } else {
            addCount(card);
        }
    }
}
