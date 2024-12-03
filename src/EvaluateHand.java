package SWEFinalProject;

import java.util.*;

public class EvaluateHand {

    // Evaluate the rank of a hand
    public static HandRank evaluateHand(List<CardClass> hand) {
        if (isStraightFlush(hand)) return HandRank.STRAIGHT_FLUSH;
        if (isFourOfAKind(hand)) return HandRank.FOUR_OF_A_KIND;
        if (isFullHouse(hand)) return HandRank.FULL_HOUSE;
        if (isFlush(hand)) return HandRank.FLUSH;
        if (isStraight(hand)) return HandRank.STRAIGHT;
        if (isThreeOfAKind(hand)) return HandRank.THREE_OF_A_KIND;
        if (isTwoPair(hand)) return HandRank.TWO_PAIR;
        if (isOnePair(hand)) return HandRank.ONE_PAIR;
        return HandRank.HIGH_CARD;
    }

    // Check for Straight Flush
    private static boolean isStraightFlush(List<CardClass> hand) {
        return isFlush(hand) && isStraight(hand);
    }

    // Check for Four of a Kind
    private static boolean isFourOfAKind(List<CardClass> hand) {
        Map<Integer, Integer> rankCounts = getRankCounts(hand);
        return rankCounts.containsValue(4);
    }

    // Check for Full House
    private static boolean isFullHouse(List<CardClass> hand) {
        Map<Integer, Integer> rankCounts = getRankCounts(hand);
        return rankCounts.containsValue(3) && rankCounts.containsValue(2);
    }

    // Check for Flush
    private static boolean isFlush(List<CardClass> hand) {
        Map<String, Integer> suitCounts = getSuitCounts(hand);
        return suitCounts.containsValue(5);
    }

    // Check for Straight
    private static boolean isStraight(List<CardClass> hand) {
        Set<Integer> ranks = new TreeSet<>();
        for (CardClass card : hand) {
            ranks.add(card.getRank());
        }

        List<Integer> rankList = new ArrayList<>(ranks);
        for (int i = 0; i <= rankList.size() - 5; i++) {
            if (rankList.get(i + 4) - rankList.get(i) == 4) return true;
        }

        // Special case for Ace-low straight (A, 2, 3, 4, 5)
        return ranks.contains(14) && ranks.contains(2) && ranks.contains(3) && ranks.contains(4) && ranks.contains(5);
    }

    // Check for Three of a Kind
    private static boolean isThreeOfAKind(List<CardClass> hand) {
        Map<Integer, Integer> rankCounts = getRankCounts(hand);
        return rankCounts.containsValue(3);
    }

    // Check for Two Pair
    private static boolean isTwoPair(List<CardClass> hand) {
        Map<Integer, Integer> rankCounts = getRankCounts(hand);
        int pairCount = 0;
        for (int count : rankCounts.values()) {
            if (count == 2) pairCount++;
        }
        return pairCount == 2;
    }

    // Check for One Pair
    private static boolean isOnePair(List<CardClass> hand) {
        Map<Integer, Integer> rankCounts = getRankCounts(hand);
        return rankCounts.containsValue(2);
    }

    // Get the count of each rank
    public static Map<Integer, Integer> getRankCounts(List<CardClass> hand) {
        Map<Integer, Integer> rankCounts = new HashMap<>();
        for (CardClass card : hand) {
            rankCounts.put(card.getRank(), rankCounts.getOrDefault(card.getRank(), 0) + 1);
        }
        return rankCounts;
    }

    // Get the count of each suit
    private static Map<String, Integer> getSuitCounts(List<CardClass> hand) {
        Map<String, Integer> suitCounts = new HashMap<>();
        for (CardClass card : hand) {
            suitCounts.put(card.getSuit(), suitCounts.getOrDefault(card.getSuit(), 0) + 1);
        }
        return suitCounts;
    }
}
