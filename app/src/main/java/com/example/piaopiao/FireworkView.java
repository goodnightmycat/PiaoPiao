package com.example.piaopiao;


import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FireworkView extends View {

    private List<Firework> fireworks;
    private Paint paint;
    private Random random;

    public FireworkView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        fireworks = new ArrayList<>();
        paint = new Paint();
        random = new Random();
        paint.setStyle(Paint.Style.FILL);

        // 创建动画并定时更新
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(50);  // 更新频率为50ms
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updateFireworks();  // 更新所有烟花状态
                invalidate();       // 重绘
            }
        });
        animator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 绘制所有烟花
        for (Firework firework : fireworks) {
            firework.draw(canvas, paint);
        }
    }

    // 更新烟花状态并控制烟花的生成
    private void updateFireworks() {
        List<Firework> activeFireworks = new ArrayList<>();
        for (Firework firework : fireworks) {
            firework.update();
            if (!firework.isFinished()) {
                activeFireworks.add(firework);
            }
        }
        fireworks = activeFireworks;

        // 控制每隔一定时间生成新的烟花
        if (random.nextFloat() < 0.02f) { // 大约每帧有2%几率生成新的烟花
            createFirework();
        }
    }

    // 创建一个新的烟花爆炸
    private void createFirework() {
        int startX = random.nextInt(getWidth());  // 从屏幕底部的随机位置升起
        int startY = getHeight();
        int targetY = random.nextInt(getHeight() / 2);  // 上升到屏幕的中上部爆炸
        fireworks.add(new Firework(startX, startY, targetY, random));
    }

    // 烟花类，包含上升和爆炸阶段
    private static class Firework {
        private List<Particle> particles;
        private boolean finished;
        private boolean isAscending;  // 控制上升状态
        private int x, y;
        private int targetY;  // 上升结束的Y坐标
        private Random random;
        private int ascentSpeed;

        Firework(int startX, int startY, int targetY, Random random) {
            this.x = startX;
            this.y = startY;
            this.targetY = targetY;
            this.random = random;
            this.isAscending = true; // 初始状态为上升阶段
            this.ascentSpeed = random.nextInt(10) + 5; // 上升速度
            this.particles = new ArrayList<>();
            this.finished = false;
        }

        void update() {
            if (isAscending) {
                // 烟花处于上升阶段
                y -= ascentSpeed;
                if (y <= targetY) {
                    // 达到爆炸高度，进入爆炸阶段
                    isAscending = false;
                    createExplosion();
                }
            } else {
                // 爆炸阶段，更新粒子状态
                boolean allFinished = true;
                for (Particle particle : particles) {
                    particle.update();
                    if (particle.life > 0) {
                        allFinished = false;
                    }
                }
                finished = allFinished;
            }
        }

        boolean isFinished() {
            return finished;
        }

        // 在上升到达指定高度时，生成粒子进行爆炸
        private void createExplosion() {
            int particleCount = 100;

            for (int i = 0; i < particleCount; i++) {
                int color = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
                float speed = random.nextFloat() * 4 + 2;  // 粒子的速度
                float angle = (float) (random.nextFloat() * 2 * Math.PI);
                particles.add(new Particle(x, y, speed, angle, color));
            }
        }

        void draw(Canvas canvas, Paint paint) {
            if (isAscending) {
                // 上升阶段画一个亮点
                paint.setColor(Color.YELLOW);
                canvas.drawCircle(x, y, 5, paint); // 烟花上升的轨迹点
            } else {
                // 爆炸阶段画粒子
                for (Particle particle : particles) {
                    if (particle.life > 0) {
                        paint.setColor(particle.color);
                        canvas.drawCircle(particle.x, particle.y, particle.size, paint);
                    }
                }
            }
        }
    }

    // 粒子类
    private static class Particle {
        float x, y;
        float speedX, speedY;
        float size;
        int color;
        int life;
        int initialLife;  // 粒子的初始寿命
        static final float GRAVITY = 0.08f; // 重力加速度

        Particle(float startX, float startY, float size, int color, int life) {
            this.x = startX;
            this.y = startY;
            this.size = size;
            this.color = color;
            this.life = life;
            this.initialLife = life;  // 保存初始寿命
            this.speedX = 0;
            this.speedY = 0;
        }

        Particle(float startX, float startY, float speed, float angle, int color) {
            this.x = startX;
            this.y = startY;
            this.speedX = (float) (speed * Math.cos(angle));
            this.speedY = (float) (speed * Math.sin(angle));
            this.size = 3;
            this.color = color;
            this.life = 200;  // 粒子的寿命
            this.initialLife = life;  // 保存初始寿命
        }

        void update() {
            x += speedX;
            y += speedY;
            speedY += GRAVITY; // 模拟重力作用
            life--;

            // 粒子逐渐减小
            size *= 0.98;

            // 根据寿命调整透明度
            int alpha = (int) (255 * ((float) life / initialLife));
            alpha = Math.max(alpha, 0); // 确保alpha不为负值
            color = (color & 0x00FFFFFF) | (alpha << 24);  // 设置alpha通道
        }
    }
}



