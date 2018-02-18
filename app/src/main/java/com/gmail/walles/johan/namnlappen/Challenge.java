package com.gmail.walles.johan.namnlappen;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

class Challenge {
    public final String answer;
    public final String question;
    public final String options[];
    private final Map<String, String> hints = new HashMap<>();

    public Challenge() {
        answer = "MELVIN";
        question = "Tryck på knappen där det står " + MainActivity.capitalize(answer) + "!";
        options = createOptions(answer);
    }

    private String[] createOptions(String base) {
        Set<String> options = new HashSet<>();
        options.add(base);
        while (options.size() < 3) {
            Variant variant = getVariant(base);
            options.add(variant.name);
            hints.put(variant.name, variant.hint);
        }

        List<String> list = new LinkedList<>(options);
        Collections.shuffle(list);

        return list.toArray(new String[3]);
    }

    private static Variant getVariant(String base) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(base);
        int randomIndex = random.nextInt(base.length());
        String charAtIndex = Character.toString(base.charAt(randomIndex));
        switch (random.nextInt(5)) {
            case 0:
                // Reverse
                return new Variant(builder.reverse(), "ordet är åt fel håll");

            case 1:
                // Add extra letter
                char letter = (char)('A' + random.nextInt('z' - 'a' + 1));
                int offset = random.nextInt(base.length() + 1);
                builder.insert(offset, letter);
                return new Variant(builder,
                        "ordet innehåller ett extra \"" + Character.toString(letter) + "\"");

            case 2:
                // Duplicate letter
                builder.insert(randomIndex, base.charAt(randomIndex));
                return new Variant(builder, "ordet innehåller \"" + charAtIndex + "\" två gånger");

            case 3:
                // Remove letter
                builder.deleteCharAt(randomIndex);
                return new Variant(builder, "ordet saknar ett \"" + charAtIndex + "\"");

            case 4:
                // Switch places between two adjacent letters
                int switchIndex0 = random.nextInt(base.length() - 1);
                char first = builder.charAt(switchIndex0);
                char second = builder.charAt(switchIndex0 + 1);
                builder.setCharAt(switchIndex0, second);
                builder.setCharAt(switchIndex0 + 1, first);
                return new Variant(builder,
                        "\"" + Character.toString(first) + "\" och \"" + Character.toString(second) + "\" har bytt plats med varandra");
        }

        throw new InternalError("Varying base string failed");
    }

    public CharSequence getHint(CharSequence wrongAnswer) {
        return hints.get(wrongAnswer.toString());
    }

    private static class Variant {
        public final String name;
        public final String hint;

        public Variant(CharSequence name, String hint) {
            this.name = name.toString();
            this.hint = hint;
        }
    }
}
