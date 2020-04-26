package com.prime.app.xo;

import io.reactivex.rxjava3.subjects.PublishSubject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.prime.app.xo.XO.Position.*;


public class XO {
    private XO instance;
    private Mode gameMode;
    private Player currentPlayer;
    private Map<Position, Player> moves = new HashMap<>();
    private EventListener listener;
    private Player winner;

    private PublishSubject nextPlayerEvent, moveEvent, gameCompleteEvent;


    public interface EventListener {
        void onNextPlayer(Player player);
        void onMoveEvent(Position position);
        void onGameComplete(Player player);
    }

    private XO(){
        //nextPlayerEvent = new PublishSubject<Player>.create();
    }

    public void registerEventListener(EventListener listener) {
        this.listener = listener;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public PublishSubject getNextPlayerEvent() {
        return nextPlayerEvent;
    }

    public PublishSubject getMoveEvent() {
        return moveEvent;
    }

    public PublishSubject getGameCompleteEvent() {
        return gameCompleteEvent;
    }

    public enum Mode {
        SINGLE,
        MULTI
    }

    public enum Position {
        A1, B1, C1,
        A2, B2, C2,
        A3, B3, C3,
        INVALID
    }

    public enum MoveStatus {
        SUCCESS,
        FAIL,
        TERMINAL
    }


    public enum Player {
        X, O
    }


    private void switchPlayer() {
        currentPlayer = currentPlayer==Player.X ?
                Player.O : Player.X;
        //nextPlayerEvent.onNext(currentPlayer);
        if(listener!=null) listener.onNextPlayer(currentPlayer);
    }


    private void generateAndMakeMove() {
        //TODO make a random move
    }

    private boolean isWinMove(List<Position> markedPositions) {
        if(markedPositions.containsAll(Arrays.asList(A1, A2, A3))
        || markedPositions.containsAll(Arrays.asList(B1, B2, B3))
        || markedPositions.containsAll(Arrays.asList(C1, C2, C3))
        || markedPositions.containsAll(Arrays.asList(A1, B1, C1))
        || markedPositions.containsAll(Arrays.asList(A2, B2, C2))
        || markedPositions.containsAll(Arrays.asList(A3, B3, C3))
        || markedPositions.containsAll(Arrays.asList(A1, B2, C3))
        || markedPositions.containsAll(Arrays.asList(C1, B2, A3)))
            return true;
        return false;
    }

    /**checks if there is a winner or if its a tie
     * @return true if the game has ended, false otherwise
     */
    private boolean isGameEnded() {
        Set<Position> positions = moves.keySet();
        List playerXMoves = positions.stream()
                .filter(this::isPlayerXMove)
                .collect(Collectors.toList());
        List playerOMoves = positions.stream()
                .filter(this::isPlayerOMove)
                .collect(Collectors.toList());

        if(isWinMove(playerXMoves)) {
            winner = Player.X; return true;
        } else if(isWinMove(playerOMoves)) {
            winner = Player.O; return true;
        }
        else if(moves.keySet().containsAll(Arrays.asList(A1, A2, A3, B1, B2, B3, C1, C2, C3))) {
            //moves exhausted and no winner, just return
            return true;
        }
        return false;
    }


    protected boolean isPlayerXMove(Position pos) {
        return moves.get(pos).equals(Player.X);
    }

    protected boolean isPlayerOMove(Position pos) {
        return moves.get(pos).equals(Player.O);
    }


    protected boolean isValidMove(Position pos) {
        return !moves.containsKey(pos);
    }


    public MoveStatus makeMove(Position pos){
        if(!isValidMove(pos)) {
            //moveEvent.onNext(Position.INVALID);
            if(listener!=null) listener.onMoveEvent(INVALID);
            return MoveStatus.FAIL;
        }
        //record move
        moves.put(pos, currentPlayer);
        //moveEvent.onNext(pos);
        if(listener!=null) listener.onMoveEvent(pos);

        //if game has ended
        if(isGameEnded()) {
            if(listener!=null) listener.onGameComplete(winner);
            //gameCompleteEvent.onNext(winner);
            return MoveStatus.TERMINAL;
        }

        switchPlayer();

        //pc move
        if(gameMode == Mode.SINGLE && currentPlayer == Player.O)
            generateAndMakeMove();

        return MoveStatus.SUCCESS;
    }



    public static XO build(Mode mode){
        XO instance  = new XO();
        instance.gameMode = mode;
        instance.currentPlayer = Player.X;
        return instance;
    }
}
