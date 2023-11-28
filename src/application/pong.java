package application;

//---------------------------------IMPORTS---------------------------------\\

import javafx.application.Application;

import java.nio.file.Paths;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.util.Duration;

import javafx.scene.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

import javafx.scene.input.KeyCode;

import javafx.scene.layout.*;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javafx.scene.paint.Color;

import javafx.scene.shape.Rectangle;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import javafx.stage.Stage;



/**
 * PONG CLASS
 */

public class pong extends Application{

    //---------------------------------VARIABLES---------------------------------\\

    private static final int width = 800;       // Window width
    private static final int height = 600;      // Window height

    private static final int PLAYER_HEIGHT = 100;   // racket height
    private static final int PLAYER_WIDTH = 15;     // racket width
    private static final double BALL_R = 20;        // ball radius

    private int ballYSpeed = 2;     // Ball speed up/down
    private int ballXSpeed = 2;     // Ball speed left/right

    private double playerOneYPos = height / 2;  // left player starting position (Y-axis)
    private double playerTwoYPos = height / 2;  // right player starting position (Y-axis)

    private int playerOneXPos = 0;                          // left player position on X-axis
    private double playerTwoXPos = width - PLAYER_WIDTH;    // right player position on Y-axis

    private double ballXPos = width / 2;        // ball starting position on X axis
    private double ballYPos = height / 2;       // ball starting position on Y axis

    private int scoreP1 = 0;    // left player score
    private int scoreP2 = 0;    // right player score
    private int bounce = 0;     // bounce counter
    private int level = 1;      // level counter

    private boolean gameStarted;    // has the game started? true/false

    Color backgroundColor = Color.BLACK;    // Background Color
    Color fontColor = Color.WHITE;          // Font Color
    Color themeRec = Color.RED;             // Theme checkbox color

    MediaPlayer mediaPlayer;                // Background Music



    /**
     * Start Method -> Settings for all Scenes
     * @param primaryStage  -> current Stage
     * @throws Exception
     */

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("u21_Pong");

        // Sets Background Music
        music();


        //--------------------------------------------MENU WINDOW--------------------------------------------\\

        // Canvas
        Canvas canvasMenu = new Canvas(width, height);
        GraphicsContext gcMenu = canvasMenu.getGraphicsContext2D();
        menu(gcMenu);

        // StackPane
        StackPane menuLayout = new StackPane();

        // add canvas to Layout
        menuLayout.getChildren().add(canvasMenu);

        // Scene
        Scene menuScene = new Scene(menuLayout, width,height);


        // Primary Stage -> Menu
        primaryStage.setScene(menuScene);
        primaryStage.show();





        //--------------------------------------------SETTINGS WINDOW--------------------------------------------\\

        // Canvas
        Canvas canvasSettings = new Canvas(width, height);
        GraphicsContext gcSettings = canvasSettings.getGraphicsContext2D();
        settings(gcSettings);

        // Group
        Group settingsLayout = new Group();

        // add canvas to Layout
        settingsLayout.getChildren().add(canvasSettings);

        // Scene
        Scene settingsScene = new Scene(settingsLayout, width, height);


        //::::::::::: Change Theme Option :::::::::::\\

        // Label
        Text theme = new Text("Light");
        theme.setStroke(fontColor);
        theme.setFont(Font.font(30));

        // Label Position
        theme.setLayoutY(height/6);
        theme.setLayoutX(width - theme.getBoundsInParent().getWidth() - 20);

        // Adds Label to Layout
        settingsLayout.getChildren().add(theme);

        // Checkbox
        javafx.scene.shape.Rectangle themeCheck = new javafx.scene.shape.Rectangle(width - theme.getBoundsInParent().getWidth() - 20, height/5, theme.getBoundsInParent().getWidth(), 20);

        // Checkbox Color
        themeCheck.setFill(themeRec);

        // Adds checkbox to Layout
        settingsLayout.getChildren().add(themeCheck);

        // Functionality
        changeTheme(themeCheck, primaryStage);










        //--------------------------------------------GAME WINDOW--------------------------------------------\\

        // Canvas
        Canvas canvasGame = new Canvas(width, height);
        GraphicsContext gcGame = canvasGame.getGraphicsContext2D();
        play(gcGame);

        // Layout
        StackPane gameSP = new StackPane();

        // Scene
        Scene gameScene = new Scene(gameSP, width,height);

        // Adds Canvas to Layout
        gameSP.getChildren().addAll(canvasGame);


        //::::::::::: Timeline :::::::::::\\

        // JavaFX Timeline = Free form animation defined by KeyFrames and their duration
        Timeline tl = new Timeline(new KeyFrame(Duration.millis(10), e -> play(gcGame)));

        // number of cycles in animation INDEFINITE = repeat indefinitely
        tl.setCycleCount(Timeline.INDEFINITE);






        //--------------------------------------------MENU CONTROLS--------------------------------------------\\

        menuScene.setOnKeyPressed(e -> {

            // Start game on Enter
            if(e.getCode() == KeyCode.ENTER){       // If "enter" pressed
                primaryStage.setScene(gameScene);   // Switch Scene to GameScene
                tl.play();                          // Start Animation
            }

            // Go to Settings on S
            if(e.getCode() == KeyCode.S){               // If "S" pressed
                primaryStage.setScene(settingsScene);   // Switch Scene to SettingsScene
            }
        });


        //--------------------------------------------SETTINGS CONTROLS--------------------------------------------\\

        settingsScene.setOnKeyPressed(e -> {
            // Go to Menu on Escape
            if(e.getCode() == KeyCode.ESCAPE){      // If "Escape" Pressed
                primaryStage.setScene(menuScene);   // Switch Scene to MenuScene
            }
        });


        //--------------------------------------------GAME CONTROLS--------------------------------------------\\

        // Controlling the racket
        gameControls(canvasGame);

        // Pausing / Leaving Game
        pauseGame(gameScene, tl, gcGame, primaryStage, menuScene);

    }




    /**
     * Plays Background Music
     */

    public void music(){
        String s = "resources/background.mp3";          // Music file location
        String h = Paths.get(s).toUri().toString();     // Convert to URI

        mediaPlayer = new MediaPlayer(new Media(h));        // mediaPlayer -> Selected Music File
        mediaPlayer.setVolume(0.1);                         // change volume
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);  // play in loop

        mediaPlayer.play();     // Start Playing
    }




    /**
     * changeTheme Method -> Changes the Theme
     * @param checkbox  -> clickable rectangle / checkbox
     * @param stage     -> PrimaryStage
     */

    public void changeTheme(Rectangle checkbox, Stage stage){

        checkbox.setOnMouseClicked(e -> {       // If checkbox clicked

            if(themeRec == Color.RED){          // If checkbox color = Red (Disabled)  ->  If current theme is Dark
                themeRec = Color.GREEN;         // Change checkbox color to green
                backgroundColor = Color.WHITE;  // Change background Color to White
                fontColor = Color.BLACK;        // Change Font colors to Black
                mediaPlayer.stop();             // Stop Music

                // Restart Application with new Settings
                try {
                    start(stage);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

            }else{                              // If checkbox color is not Red (Enabled)  ->  If current theme is Light
                themeRec = Color.RED;           // Change checkbox color to red
                backgroundColor = Color.BLACK;  // Change background color to black
                fontColor = Color.WHITE;        // Change Font colors to White
                mediaPlayer.stop();             // Stop Music

                // Restart Application with new Settings
                try {
                    start(stage);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            changeTheme(checkbox, stage);   // Recursion -> Check if clicked
        });
    }




    /**
     * pauseGame Method -> Allows to Pause the Game
     * @param gameScene     -> Game Scene
     * @param tl            -> Timeline
     * @param gc            -> Graphics Context of Game
     */

    public void pauseGame(Scene gameScene, Timeline tl, GraphicsContext gc, Stage primaryStage, Scene menuScene){

        gameScene.setOnKeyPressed(e -> {

            // Pause Game when "P" Pressed
            if(e.getCode() == KeyCode.P){   // If "P" Pressed

                tl.stop();    // Stop Timeline/Animation


                //::::::::::: Pause Menu :::::::::::\\

                gc.strokeText("PAUSED", width/2, height/2);
                gc.strokeText("Press P to resume", width/2, height/2 + height/6);
                gc.strokeText("Press Esc to leave", width/2, height/2 + height/4);


                gameScene.setOnKeyPressed(el -> {

                    // Continue Game when P Pressed
                    if(el.getCode() == KeyCode.P){      // If "P" pressed again

                        tl.play();      // Continue Timeline/Animation

                        pauseGame(gameScene, tl, gc, primaryStage, menuScene);      // Recursion -> Check if pressed again
                    }

                    //Leave Game on Esc with Pause
                    if(el.getCode() == KeyCode.ESCAPE){         // If "Escape" pressed
                        primaryStage.setScene(menuScene);       // Switch Scene to menuScene
                        gameStarted = false;                    // Restart Game
                        scoreP1 = 0;                            // Clear Player 1 score
                        scoreP2 = 0;                            // Clear Player 2 score
                    }
                });
            }




            // Leave Game on Esc without Pause
            if(e.getCode() == KeyCode.ESCAPE){          // If "Escape" pressed
                primaryStage.setScene(menuScene);       // Switch Scene to menuScene
                gameStarted = false;                    // Restart Game
                scoreP1 = 0;                            // Clear Player 1 score
                scoreP2 = 0;                            // Clear Player 2 score
            }
        });
    }






    /**
     * Defines the Way how you control the racket
     * @param canvasGame -> Game Canvas
     */

    public void gameControls(Canvas canvasGame){
        // mouse control (move and click)
        canvasGame.setOnMouseMoved(e ->  playerOneYPos  = e.getY() - PLAYER_HEIGHT/2);  // Move mouse to control racket
        canvasGame.setOnMouseClicked(e ->  gameStarted = true);                         // Click mouse to start round
    }





//--------------------------------------------MENU CANVAS--------------------------------------------\\

    /**
     * Canvas Menu
     * @param gc graphics context for Menu
     */

    private void menu(GraphicsContext gc){

        //::::::::::: Background :::::::::::\\

        gc.setFill(backgroundColor);                // Set background color
        gc.fillRect(0, 0, width, height);    // Draw background



        //::::::::::: Text :::::::::::\\

        gc.setFill(fontColor);          // Set font color
        gc.setFont(Font.font(25));      // Set font size
        gc.setStroke(fontColor);        // Set font color for Stroke

        gc.strokeText("Press S for Settings", 20, 30);


        gc.setFont(Font.font(40));                  // Set font size
        gc.setTextAlign(TextAlignment.CENTER);      // Align text to center

        gc.strokeText("Press Enter to Play", width / 2, height / 2);
    }







//--------------------------------------------SETTINGS CANVAS--------------------------------------------\\

    /**
     * Canvas Settings
     * @param gc -> Graphics Context for settings
     */

    private void settings(GraphicsContext gc){

        //::::::::::: Background :::::::::::\\

        gc.setFill(backgroundColor);                // Set background color
        gc.fillRect(0, 0, width, height);    // draw background



        //::::::::::: Text :::::::::::\\

        gc.setFill(fontColor);      // Set Font color
        gc.setFont(Font.font(25));  // Set Font size
        gc.setStroke(fontColor);    // Set Stroke color

        gc.strokeText("Escape", 20, 30);


        gc.setFont(Font.font(40));              // Set Font Size
        gc.setTextAlign(TextAlignment.CENTER);  // Align text to center

        gc.strokeText("CONTROLS", width / 2, height / 2);


        gc.setFont(Font.font(25));      // Set Font Size

        gc.strokeText("Move mouse up and down to control", width/2, height / 2 + height / 6);
        gc.strokeText("Press \"P\" to Pause", width/2, height / 2 + height / 4);
    }





//--------------------------------------------GAME CANVAS--------------------------------------------\\

    /**
     * Game Canvas
     * @param gc -> Graphics Context for Game
     */

    private void play(GraphicsContext gc){

        //::::::::::: Background :::::::::::\\

        gc.setFill(backgroundColor);                // Set Background color
        gc.fillRect(0, 0, width, height);    // draw background



        //::::::::::: Text :::::::::::\\

        gc.setFill(fontColor);      // Set Font Color
        gc.setFont(Font.font(50));  // Set Font Size




    if(gameStarted) {               // When the round starts


            //::::::::::: Ball Movement :::::::::::\\

            ballXPos+=ballXSpeed;
            ballYPos+=ballYSpeed;

            gc.fillOval(ballXPos, ballYPos, BALL_R, BALL_R);    // draw ball


            //::::::::::: Computer Opponent :::::::::::\\
            playerTwoYPos = ballYPos - PLAYER_HEIGHT/2;

        } else {                // When the round ends

            //::::::::::: Start Text :::::::::::\\

            gc.setStroke(fontColor);                // Set Stroke color
            gc.setTextAlign(TextAlignment.CENTER);  // Set Text align

            gc.strokeText("CLICK", width / 2, height / 2);



            //::::::::::: Reset Ball :::::::::::\\

            // Position
            ballXPos = width / 2;
            ballYPos = height / 2;

            // Speed and Direction
            ballXSpeed = 2;
            ballYSpeed = 2;



            //::::::::::: Reset Level :::::::::::\\
            bounce = 0;     // Reset bounce counter
            level = 1;      // Reset Level
        }



        //::::::::::: Limit Field for Ball :::::::::::\\

        if(ballYPos + BALL_R > height){     // If Ball hits bottom
            ballYSpeed *= -1;               // Change direction
        }
        if(ballYPos < 0){                   // If Ball hits top
            ballYSpeed *= -1;               // Change direction
        }


        // Change Ball movement when hitting a racket
        if( ((ballXPos + BALL_R > playerTwoXPos) && ballYPos >= playerTwoYPos && ballYPos <= playerTwoYPos + PLAYER_HEIGHT)                     // Player 1 (left)
                || ((ballXPos < playerOneXPos + PLAYER_WIDTH) && ballYPos >= playerOneYPos && ballYPos <= playerOneYPos + PLAYER_HEIGHT)) {     // Player 2 (right)

            ballXSpeed *= -1;   // Change direction
            bounce += 1;        // count bounce
        }





        //::::::::::: Points :::::::::::\\

        // If Player 1 misses, Player 2 gets point
        if(ballXPos < playerOneXPos - PLAYER_WIDTH) {
            scoreP2++;              // Point for Player 2
            gameStarted = false;    // round ended
        }


        // If Player 2 misses, Player 1 gets point
        if(ballXPos > playerTwoXPos + PLAYER_WIDTH) {
            scoreP1++;              // Point for Player 1
            gameStarted = false;    // round ended
        }





        //::::::::::: Level Up :::::::::::\\

        // Every 10 bounces -> Next Level -> Higher Speed
        if(bounce == 10){
            ballYSpeed += 1 * Math.signum(ballYSpeed);      // Add Speed on Y-Axis
            ballXSpeed += 1 * Math.signum(ballXSpeed);      // Add Speed on X-Axis
            bounce = 0;                                     // Reset bounce counter
            level += 1;                                     // Next Level
        }




        //::::::::::: Limiting Player :::::::::::\\

        // Limiting Player 1
        if(playerOneYPos <= 0){
            playerOneYPos = 0;                          // Racket cannot go above field
        }

        if(playerOneYPos >= height - PLAYER_HEIGHT){
            playerOneYPos = height - PLAYER_HEIGHT;     // Racket cannot go below field
        }



        // Limiting Player 2
        if(playerTwoYPos <= 0){
            playerTwoYPos = 0;                          // Racket cannot go above field
        }
        if(playerTwoYPos >= height - PLAYER_HEIGHT){
            playerTwoYPos = height - PLAYER_HEIGHT;     // Racket cannot go below field
        }





        //::::::::::: Scoreboard and Level :::::::::::\\

        gc.setStroke(fontColor);        // Set Stroke color
        gc.setFont(Font.font(25));      // Set font size


        // draw "Player" text
        gc.strokeText("Player", 100, 50);


        // draw "AI" text
        gc.strokeText("AI", width-100, 50);

        // draw level
        gc.strokeText("Level " + level, width/2, 50);

        //draw score
        gc.fillText(scoreP1 + "\t\t\t\t" + bounce + "\t\t\t\t" + scoreP2, width / 2, 100);





        //::::::::::: Player :::::::::::\\

        gc.fillRect(playerOneXPos, playerOneYPos, PLAYER_WIDTH, PLAYER_HEIGHT);     // draw Player 1

        gc.fillRect(playerTwoXPos, playerTwoYPos, PLAYER_WIDTH, PLAYER_HEIGHT);     // draw Player 2

    }




    /**
     * Main Method -> Start the Application
     * @param args
     */

    public static void main(String[] args) {
        launch(args);
    }
}
