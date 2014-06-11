/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pacman_infd;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import pacman_infd.Elements.Cherry;
import pacman_infd.Elements.Ghost;
import pacman_infd.Elements.Pacman;
import pacman_infd.Elements.Pellet;
import pacman_infd.Elements.SuperPellet;

/**
 *
 * @author Marinus
 */
public class GameController implements GameEventListener {

    private GameWorld gameWorld;
    private View view;
    private ScorePanel scorePanel;
    private boolean cherrySpawned; //TODO: dit moet hier weg
    private GameState gameState;
    private Timer gameTimer;
    private StopWatch stopWatch;
    private ResourceManager resourceManager;
    private SoundManager soundManager;

    public GameController(View view, ScorePanel scorePanel) {

        this.view = view;
        this.scorePanel = scorePanel;
        cherrySpawned = false;
        gameState = GameState.PREGAME;
        resourceManager = new ResourceManager();
        soundManager = new SoundManager();

        ActionListener gameTimerAction = new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gameTimerActionPerformed(evt);
            }
        };

        gameTimer = new Timer(10, gameTimerAction);
        stopWatch = new StopWatch();

    }

    @Override
    public void gameElementPerfomedAction(GameElement e) {
        //drawGame();
        view.requestFocus();
    }

    @Override
    public void pacmanActionPerformed(Pacman p) {
        //checkCollisions(p.getCell());
        //drawGame();
        view.requestFocus();
    }

    public void pacmanFoundPellet() {
        scorePanel.addScore(5);
        scorePanel.repaint();
        soundManager.playSFXPellet();
        if (!cherrySpawned) {
            if (gameWorld.countPellets() <= gameWorld.getNumberOfPelletsAtStart() / 2) {
                gameWorld.placeCherryOnRandomEmptyCell();
                cherrySpawned = true;
            }
        }
        if (gameWorld.countPellets() == 0) {
            drawGame();
            try {
                soundManager.playSFXIntermission();
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            nextLevel();
        }
        //soundManager.playWaka();
    }

    public void pacmanDied() {
        scorePanel.looseLife();
        scorePanel.repaint();
        soundManager.playSFXDeath();
        if (scorePanel.getLives() <= 0) {
            gameOver();
        } else {

            try {

                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            gameWorld.getPacman().resetPacman();
            for (Ghost ghost : gameWorld.getGhosts()) {
                ghost.resetGhost();
            }

        }
    }

    public void pacmanFoundSuperPellet() {
        scorePanel.addScore(50);
        scorePanel.repaint();
        soundManager.playSFXPellet();
        for (Ghost ghost : gameWorld.getGhosts()) {
            ghost.runFromPacman();
        }
    }

    public void pacmanEatsGhost() {
        scorePanel.addScore(500);
        scorePanel.repaint();
        soundManager.playSFXPacmanEatsGhost();
    }

    private void drawGame() {

        Graphics g = view.getGameWorldGraphics();

        if (g != null && gameWorld != null) {
            gameWorld.draw(g);

            view.drawGameWorld();
        }
    }

    public View getView() {
        return view;
    }

    public void newGame() {
//        if (gameState == GameState.PREGAME) {
        gameWorld = null;
        gameWorld = new GameWorld(this, resourceManager.getFirstLevel());
        scorePanel.resetStats();
        gameState = GameState.RUNNING;
        drawGame();
        gameTimer.start();
        stopWatch.reset();
        stopWatch.start();
//        }
    }

    public void nextLevel() {
        pauseGame();
        JOptionPane.showMessageDialog(
                null,
                "Well done!\nGet ready for the next level!",
                "Level Complete",
                JOptionPane.ERROR_MESSAGE
        );

        gameWorld = null;
        gameWorld = new GameWorld(this, resourceManager.getNextLevel());
    }

    public void pauseGame() {
        if (gameState == GameState.RUNNING) {
            for (Ghost ghost : gameWorld.getGhosts()) {
                ghost.stopTimer();
            }
            gameWorld.getPacman().stopTimer();
            gameTimer.stop();
            stopWatch.stop();
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            for (Ghost ghost : gameWorld.getGhosts()) {
                ghost.startTimer();
            }
            gameWorld.getPacman().startTimer();
            gameTimer.start();
            stopWatch.start();
            gameState = GameState.RUNNING;
        }
    }

    public void gameTimerActionPerformed(ActionEvent e) {
        checkCollisions(gameWorld.getPacman().getCell());
        drawGame();
        scorePanel.setTime(stopWatch.getElepsedTimeMinutesSeconds());
        scorePanel.repaint();

    }

//    public void updateTimerActionPerformed(ActionEvent e){
//        
//        drawGame();
//        
//    }
    private void checkCollisions(Cell cell) {

        //Cell cell = gameWorld.getPacman().getCell();
        GameElement gameElement = cell.getStaticElement();

        if (gameElement instanceof Pellet) {
            cell.setStaticElement(null);
            pacmanFoundPellet();

        } else if (gameElement instanceof SuperPellet) {
            cell.setStaticElement(null);
            pacmanFoundSuperPellet();
            // soundManager.playSFXSuperWaka();
        } else if (gameElement instanceof Cherry) {
            cell.setStaticElement(null);
            pacmanFoundCherry();

        }

        for (GameElement element : cell.getElements()) {
            if (element instanceof Ghost) {
                Ghost ghost = (Ghost) element;
                if (ghost.getState() == Ghost.GhostState.VULNERABLE) {
                    ghost.dead();
                    pacmanEatsGhost();

                } else if (ghost.getState() == Ghost.GhostState.NORMAL) {
                    pacmanDied();

                }
                break;
            }
        }

    }

    public void pacmanFoundCherry() {
        scorePanel.addScore(100);
        scorePanel.repaint();
        soundManager.playSFXCherry();
    }

    private void gameOver() {
        pauseGame();
        view.repaint();
        drawGame();
        JOptionPane.showMessageDialog(
                null,
                "Game over!\nYour score: " + scorePanel.getScore(),
                "Game over!",
                JOptionPane.ERROR_MESSAGE
        );
        gameWorld = null;
        gameState = GameState.PREGAME;

//        gameTimer.stop();
//        stopWatch.stop();
    }

    public GameState getGameState() {
        return gameState;
    }

}
