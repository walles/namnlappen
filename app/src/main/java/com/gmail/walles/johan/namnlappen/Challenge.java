package com.gmail.walles.johan.namnlappen;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

class Challenge {
    public final String answer;
    public final String question;
    public final String options[];

    public Challenge() {
        answer = "MELVIN";
        question = "Tryck på knappen där det står " + answer + "!";
        options = createOptions(answer);
    }

    private static String[] createOptions(String base) {
        Set<String> options = new HashSet<>();
        options.add(base);
        while (options.size() < 3) {
            options.add(getVariant(base));
        }

        List<String> list = new LinkedList<>(options);
        Collections.shuffle(list);

        return list.toArray(new String[3]);
    }

    private static String getVariant(String base) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder(base);
        int randomIndex = random.nextInt(base.length());
        switch (random.nextInt(5)) {
            case 0:
                // Reverse
                return builder.reverse().toString();

            case 1:
                // Add extra letter
                char letter = (char)('A' + random.nextInt('z' - 'a' + 1));
                int offset = random.nextInt(base.length() + 1);
                builder.insert(offset, letter);
                return builder.toString();

            case 2:
                // Duplicate letter
                builder.insert(randomIndex, base.charAt(randomIndex));
                return builder.toString();

            case 3:
                // Remove letter
                builder.deleteCharAt(randomIndex);
                return builder.toString();

            case 4:
                // FIXME: Switch places between two adjacent letters
                int switchIndex0 = random.nextInt(base.length() - 1);
                char first = builder.charAt(switchIndex0);
                char second = builder.charAt(switchIndex0 + 1);
                builder.setCharAt(switchIndex0, second);
                builder.setCharAt(switchIndex0 + 1, first);
                return builder.toString();
        }

        throw new InternalError("Varying base string failed");
    }
}
