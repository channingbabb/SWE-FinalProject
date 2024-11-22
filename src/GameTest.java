import java.util.ArrayList;

public class GameTest {
    public static void main(String[] args) {
        //  players
        User player1 = new User("Player 1", 1000);
        User player2 = new User("Player 2", 1000);

        //  game components
        Game game = new Game();
        Dealer dealer = new Dealer();
        ArrayList<User> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // game
        game.setName("Test Poker Game");
        dealer.dealInitialCards(players);

        //  initial hands
        System.out.println("Initial Hands:");
        for (User player : players) {
            System.out.println(player.getUsername() + " - " + player.getHand().getCards());
        }

        // Pre-flop betting
        System.out.println("\n--- Pre-Flop Betting ---");
        simulateBettingRound(players, game);

        // Deal the flop
        ArrayList<CardClass> communityCards = new ArrayList<>();
        dealer.dealFlop(communityCards);
        System.out.println("\nCommunity Cards (Flop): " + communityCards);

        // Post-flop betting
        System.out.println("\n--- Post-Flop Betting ---");
        simulateBettingRound(players, game);

        // Deal the turn
        dealer.dealTurn(communityCards);
        System.out.println("\nCommunity Cards (Turn): " + communityCards);

        // Post-turn betting
        System.out.println("\n--- Post-Turn Betting ---");
        simulateBettingRound(players, game);

        // Deal the river
        dealer.dealRiver(communityCards);
        System.out.println("\nCommunity Cards (River): " + communityCards);

        // Post-river betting
        System.out.println("\n--- Post-River Betting ---");
        simulateBettingRound(players, game);

        // Evaluate hands -- determine winner
        System.out.println("\n--- Evaluating Hands ---");
        User winner = determineWinner(players, communityCards);
        if (winner != null) {
            winner.updateBalance(game.getPot());
            game.setPot(0);
            System.out.println("Winner: " + winner.getUsername());
        } else {
            System.out.println("It's a tie!");
        }

        //  final game state
        System.out.println("\n--- Final Game State ---");
        for (User player : players) {
            System.out.println(player);
        }
    }

    private static void simulateBettingRound(ArrayList<User> players, Game game) {
        for (User player : players) {
            if (!player.isActive()) continue;

            // Simulate random bets/actions
            int action = (int) (Math.random() * 3); // 0 = Call, 1 = Raise, 2 = Fold
            switch (action) {
                case 0: // Call
                    int callAmount = game.getCurrentBet() - player.getCurrentBet();
                    if (player.getBalance() >= callAmount) {
                        player.placeBet(callAmount);
                        game.setPot(game.getPot() + callAmount);
                        System.out.println(player.getUsername() + " calls $" + callAmount);
                    } else {
                        System.out.println(player.getUsername() + " can't call and folds!");
                        player.fold();
                    }
                    break;
                case 1: // Raise
                    int raiseAmount = (int) (Math.random() * 50 + 50); // Random raise between 50 and 100
                    if (player.getBalance() >= (game.getCurrentBet() - player.getCurrentBet() + raiseAmount)) {
                        int totalBet = game.getCurrentBet() - player.getCurrentBet() + raiseAmount;
                        player.placeBet(totalBet);
                        game.setPot(game.getPot() + totalBet);
                        game.setCurrentBet(game.getCurrentBet() + raiseAmount);
                        System.out.println(player.getUsername() + " raises by $" + raiseAmount);
                    } else {
                        System.out.println(player.getUsername() + " can't raise and folds!");
                        player.fold();
                    }
                    break;
                case 2: // Fold
                    System.out.println(player.getUsername() + " folds.");
                    player.fold();
                    break;
            }
        }
    }

    private static User determineWinner(ArrayList<User> players, ArrayList<CardClass> communityCards) {
        User winner = null;
        HandRank bestRank = null;

        for (User player : players) {
            if (player.isActive()) {
                ArrayList<CardClass> fullHand = new ArrayList<>(player.getHand().getCards());
                fullHand.addAll(communityCards);

                HandRank rank = EvaluateHand.evaluateHand(fullHand);
                System.out.println(player.getUsername() + " - Hand: " + rank);

                if (bestRank == null || rank.ordinal() > bestRank.ordinal()) {
                    bestRank = rank;
                    winner = player;
                } else if (rank.ordinal() == bestRank.ordinal()) {
                    // Handle ties (optional: split the pot)
                }
            }
        }

        return winner;
    }
}
