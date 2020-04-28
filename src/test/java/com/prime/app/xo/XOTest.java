package com.prime.app.xo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static com.prime.app.xo.XO.*;
import static com.prime.app.xo.XO.Position.*;
import static org.junit.jupiter.api.Assertions.*;

class XOTest {
    private XO game;

    @BeforeEach
    void createGame(){
        game = build(Mode.MULTI);
    }

    @Test
    void shouldChangePlayer(){
        Player firstPlayer = game.getCurrentPlayer();
        game.makeMove(Position.A1);
        assertNotEquals(firstPlayer, game.getCurrentPlayer());
    }


    @Test
    void shouldInvalidateSameMove(){
        //make move
        game.makeMove(Position.A3);
        assertFalse(game.isValidMove(Position.A3));
    }

    @Test
    void shouldPermitMove(){
        game.makeMove(Position.C2); //make move
        assertTrue(game.isValidMove(Position.B3));
    }


    @Test
    void shouldEndGameOnExhaustedMoves(){
        Arrays.asList(A1, A2, A3, B1, B2, B3, C1, C2, C3)
                .forEach( pos -> game.makeMove(pos));
        assertEquals(MoveStatus.FAIL, game.makeMove(C3));
    }

    @Test
    void lastMoveShouldEndGame(){
        Arrays.asList(A1, A2, A3, B1, B3, C1, C2)
                .forEach( pos -> game.makeMove(pos));
        assertTrue(game.isValidMove(Position.C3));
        assertEquals(MoveStatus.SUCCESS, game.makeMove(C3));
        assertEquals(MoveStatus.FAIL, game.makeMove(A2));
        assertEquals(MoveStatus.TERMINAL, game.makeMove(B2));
    }

    @Test
    void shouldChangePlayerOnEachMove(){
        System.out.println("first: "+ game.getCurrentPlayer());
        Arrays.asList(A1, A2, A3, B1, B2, B3, C2, C1, C3)
                .forEach( pos -> {
                    game.makeMove(pos);
                    System.out.println(pos);
                    System.out.println("next: " + game.getCurrentPlayer());
                });
        assertTrue(game.isPlayerXMove(A1));
        assertTrue(game.isPlayerOMove(A2));
        assertTrue(game.isPlayerXMove(A3));
        assertTrue(game.isPlayerXMove(C2));
        assertTrue(game.isPlayerOMove(C1));
        assertTrue(game.isPlayerXMove(C3));
    }


    @Test
    void gen(){
        Object[] pos =
                Arrays.stream(Position.values())
                .filter( p -> p!= INVALID).toArray();

        int n = new Random().nextInt(pos.length);
        System.out.println(pos[n]);
    }


    @Test
    void singlePlayer() {
        XO single = XO.build(Mode.SINGLE);

        Thread t = new Thread(()->{
            single.getMoveEvent().subscribe( event ->{
                System.out.println(event);
            });
        });
        t.start();

        single.getMoveEvent().subscribe();

        single.makeMove(A3);
        //should be player x's turn again
        assertEquals(Player.X, single.getCurrentPlayer());
    }
}