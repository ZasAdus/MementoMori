package com.example.mementomori;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConfettiPane extends Pane {
    private static final int CONFETTI_COUNT = 200;
    private final Random random = new Random();
    private final List<Confetti> confetti = new ArrayList<>();
    private AnimationTimer timer;

    public ConfettiPane() {
        for (int i = 0; i < CONFETTI_COUNT; i++) {
            Confetti piece = new Confetti();
            confetti.add(piece);
            getChildren().add(piece.shape);
        }

        timer = new AnimationTimer() {
            private int frameCount = 0;
            private static final int MAX_FRAMES = 120; // ~2 sekundy przy 60 FPS

            @Override
            public void handle(long now) {
                frameCount++;

                for (Confetti piece : confetti) {
                    piece.update();
                }

                if (frameCount >= MAX_FRAMES) {
                    stop();
                    getChildren().clear();
                    confetti.clear();
                }
            }
        };
    }

    public void startAnimation() {
        for (Confetti piece : confetti) {
            piece.reset();
        }
        timer.start();
    }

    private class Confetti {
        private Rectangle shape;
        private double x;
        private double y;
        private double speedY;
        private double rotation;
        private double rotationSpeed;

        public Confetti() {
            shape = new Rectangle(8, 8);
            reset();
            shape.setFill(Color.rgb(
                    random.nextInt(255),
                    random.nextInt(255),
                    random.nextInt(255)
            ));
        }

        public void reset() {
            x = random.nextDouble() * getWidth();
            y = -random.nextDouble() * 50;
            speedY = 3 + random.nextDouble() * 4;
            rotation = random.nextDouble() * 360;
            rotationSpeed = -3 + random.nextDouble() * 6;

            shape.setX(x);
            shape.setY(y);
        }

        public void update() {
            y += speedY;
            rotation += rotationSpeed;
            x += Math.sin(y / 30) * 0.8;

            shape.setX(x);
            shape.setY(y);
            shape.setRotate(rotation);
        }
    }
}