package Project.Server;

import Project.Common.Move;



//aa2984:2026-July-18
//Summray: Server side config holds one round of rps
//updateMove locks in a move and rejects second one
//playerWins just checks the three winning combos after each turn used



public class RPSGame {
    private final long gameId;
    private final long playerA;
    private final long playerB;
    private Move playerAMove;
    private Move playerBMove;

    public RPSGame(long gameId, long playerA, long playerB)
    {
        this.gameId = gameId;
        this.playerA = playerA;
        this.playerB = playerB;
    }

    public long getGameId() {
        return gameId;
    }

    public long getPlayerA() {
        return playerA;
    }

    public long getPlayerB() {
        return playerB;
    }

    public boolean updateMove(long playerId, Move move) {
        if (playerId == playerA) {
            if(playerAMove!=null) {
                return false;
            }
            playerAMove = move;
            return true;
        }

        if (playerId == playerB) {
            if(playerBMove != null) {
                return false;
            }
            playerBMove = move;
            return true;
        }

        return false;
    }
    public boolean isComplete() {
        return playerAMove!= null && playerBMove != null;

    }
    public long getOpponentId(long playerId) {

        return playerId == playerA ? playerB : playerA;

    }

    public Move getOpponentMove(long playerId) {

        return playerId == playerA ? playerBMove : playerAMove;

    }

    public Move getMoveFor(long playerId) {

        return playerId == playerA ? playerAMove : playerBMove;

    }

    public boolean isParticipant(long clientId) {

        return clientId == playerA || clientId == playerB;

    }

    public boolean isTie() {

        return playerAMove == playerBMove;

    }

    public boolean playerWins(long playerId) {

        Move myMove1 = getMoveFor(playerId);

        Move theirMove1 = getOpponentMove(playerId);

        if (myMove1 == Move.ROCK && theirMove1 == Move.SCISSORS) {

            return true;

        }

        if (myMove1 == Move.PAPER && theirMove1 == Move.ROCK) {

            return true;

        }

        if (myMove1 == Move.SCISSORS && theirMove1 == Move.PAPER) {

            return true;

        }

        return false;

    }

}

