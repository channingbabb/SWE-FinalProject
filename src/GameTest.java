import java.util.ArrayList;

public class GameTest {
    public static void main(String[] args) {
        // players
        User player1 = new User("Player 1", 1000);
        User player2 = new User("Player 2", 1000);

        // game components
        Game game = new Game();
        Dealer dealer = new Dealer();
        ArrayList<User> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);

        // game
        game.setName("Test Poker Game");
        dealer.dealInitialCards(players);

        // initial hands
        System.out.println("Initial Hands:");
        for (User player : players) {
            System.out.println(player.getUsername() + " - " + player.getHand().getCards());
        }

        // Pre-flop betting
        System.out.println("\n--- Pre-Flop Betting ---");
        initializeBlinds(players, game);
        simulateBettingRound(players, game);

        // Deal the flop
        ArrayList<CardClass> communityCards = new ArrayList<>();
        dealer.dealFlop(communityCards);
        System.out.println("\nCommunity Cards (Flop): " + communityCards);

        // Post-flop betting
        System.out.println("\n--- Post-Flop Betting ---");
        simulateBettingRound(players, game);
        if (checkForImmediateWinner(players, game)) return;

        // Deal the turn
        dealer.dealTurn(communityCards);
        System.out.println("\nCommunity Cards (Turn): " + communityCards);

        // Post-turn betting
        System.out.println("\n--- Post-Turn Betting ---");
        simulateBettingRound(players, game);
        if (checkForImmediateWinner(players, game)) return;

        // Deal the river
        dealer.dealRiver(communityCards);
        System.out.println("\nCommunity Cards (River): " + communityCards);

        // Post-river betting
        System.out.println("\n--- Post-River Betting ---");
        simulateBettingRound(players, game);
        if (checkForImmediateWinner(players, game)) return;

        // Evaluate hands -- determine winner
        System.out.println("\n--- Evaluating Hands ---");
        User winner = determineWinner(players, communityCards, game);
        if (winner != null) {
            winner.updateBalance(game.getPot());
            game.setPot(0);
            System.out.println("Winner: " + winner.getUsername());
        } else {
            System.out.println("It's a tie!");
        }

        // final game state
        System.out.println("\n--- Final Game State ---");
        for (User player : players) {
            System.out.println(player);
        }
    }

    private static void initializeBlinds(ArrayList<User> players, Game game) {
        int smallBlind = 10;
        int bigBlind = 20;

        // Small blind
        User smallBlindPlayer = players.get(0);
        smallBlindPlayer.placeBet(smallBlind);
        game.setPot(game.getPot() + smallBlind);
        game.setCurrentBet(smallBlind);

        // Big blind
        User bigBlindPlayer = players.get(1);
        bigBlindPlayer.placeBet(bigBlind);
        game.setPot(game.getPot() + bigBlind);
        game.setCurrentBet(bigBlind);

        System.out.println(smallBlindPlayer.getUsername() + " posts small blind of $" + smallBlind);
        System.out.println(bigBlindPlayer.getUsername() + " posts big blind of $" + bigBlind);
    }

    private static void simulateBettingRound(ArrayList<User> players, Game game) {
        for (User player : players) {
            if (!player.isActive()) continue;

            int action = (int) (Math.random() * 2); // 0 = Call, 1 = Raise (No player can fold)

            switch (action) {
                case 0: // Call
                    int callAmount = game.getCurrentBet() - player.getCurrentBet();
                    if (player.getBalance() >= callAmount) {
                        player.placeBet(callAmount);
                        game.setPot(game.getPot() + callAmount);
                        System.out.println(player.getUsername() + " calls $" + callAmount);
                    } else {
                        System.out.println(player.getUsername() + " can't call and is forced to all-in!");
                        int allInAmount = player.getBalance();
                        player.placeBet(allInAmount);
                        game.setPot(game.getPot() + allInAmount);
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
                        System.out.println(player.getUsername() + " can't raise and is forced to all-in!");
                        int allInAmount = player.getBalance();
                        player.placeBet(allInAmount);
                        game.setPot(game.getPot() + allInAmount);
                    }
                    break;
            }
        }

        // Losercase: Check if only one player remains active
        if (checkForImmediateWinner(players, game)) return;
    }

    private static boolean checkForImmediateWinner(ArrayList<User> players, Game game) {
        int activePlayers = 0;
        User remainingPlayer = null;

        for (User player : players) {
            if (player.isActive()) {
                activePlayers++;
                remainingPlayer = player;
            }
        }

        if (activePlayers == 1 && remainingPlayer != null) {
            System.out.println("\nAll other players folded. " + remainingPlayer.getUsername() + " wins the pot of $" + game.getPot() + "!");
            remainingPlayer.updateBalance(game.getPot());
            game.setPot(0);
            return true; // Immediate winner
        }

        return false;
    }

    private static User determineWinner(ArrayList<User> players, ArrayList<CardClass> communityCards, Game game) {
        User winner = null;
        HandRank bestRank = null;
        ArrayList<User> winners = new ArrayList<>();

        for (User player : players) {
            if (player.isActive()) {
                ArrayList<CardClass> fullHand = new ArrayList<>(player.getHand().getCards());
                fullHand.addAll(communityCards);

                HandRank rank = EvaluateHand.evaluateHand(fullHand);
                System.out.println(player.getUsername() + " - Hand: " + rank);

                if (bestRank == null || rank.ordinal() > bestRank.ordinal()) {
                    bestRank = rank;
                    winners.clear();
                    winners.add(player);
                } else if (rank.ordinal() == bestRank.ordinal()) {
                    winners.add(player);
                }
            }
        }

        if (winners.isEmpty()) {
            System.out.println("No active players remaining.");
            return null; // No winner
        }

        if (winners.size() == 1) {
            return winners.get(0);
        } else {
            System.out.println("It's a tie!");
            int splitPot = game.getPot() / winners.size();
            for (User winnerPlayer : winners) {
                winnerPlayer.updateBalance(splitPot);
            }
            game.setPot(0);
            return null;
        }
    }
}
