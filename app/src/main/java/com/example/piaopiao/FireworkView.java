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
                        particle.draw(canvas, paint);
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
        float gravity = 0.05f;  // 重力加速度因子，控制下落的速度

        // 记录粒子历史轨迹点
        List<float[]> trailPoints;  // 保存粒子的历史位置
        int maxTrailLength = 20;    // 拖尾的最大长度

        Particle(float startX, float startY, float speed, float angle, int color) {
            this.x = startX;
            this.y = startY;
            this.speedX = (float) (speed * Math.cos(angle));
            this.speedY = (float) (speed * Math.sin(angle));
            this.size = 10;  // 粒子初始大小
            this.color = color;
            this.life = 100;  // 粒子的寿命
            this.initialLife = life;  // 保存初始寿命
            this.trailPoints = new ArrayList<>();  // 初始化拖尾轨迹
        }

        void update() {
            // 更新位置前，记录当前坐标
            trailPoints.add(new float[]{x, y});

            // 如果拖尾点数量超过最大长度，移除最早的点
            if (trailPoints.size() > maxTrailLength) {
                trailPoints.remove(0);
            }

            // 水平速度逐渐衰减，模拟空气阻力
            speedX *= 0.99;

            // 垂直方向增加重力效果，使得粒子逐渐下落
            speedY += gravity;

            // 更新粒子的位置
            x += speedX;
            y += speedY;
            life--;

            // 粒子逐渐减小
            size *= 0.98;

            // 根据寿命调整透明度
            int alpha = (int) (255 * ((float) life / initialLife));
            color = (color & 0x00FFFFFF) | (alpha << 24);  // 设置alpha通道
        }

        void draw(Canvas canvas, Paint paint) {
            // 绘制拖尾效果
            int trailAlphaStep = 255 / maxTrailLength;  // 每段拖尾的透明度变化

            for (int i = 0; i < trailPoints.size(); i++) {
                // 根据历史点的顺序，逐渐减少透明度
                int alpha = (int) (trailAlphaStep * i);
                paint.setColor((color & 0x00FFFFFF) | (alpha << 24));

                // 绘制历史点，半径逐渐变小，模拟拖尾效果
                float[] point = trailPoints.get(i);
//                float trailSize = size * (1f - (float) i / maxTrailLength);  // 尾迹逐渐变小
                float trailSize = size * 0.8f;  // 尾迹大小固定
                canvas.drawCircle(point[0], point[1], trailSize, paint);
            }

//            // 最后绘制当前的粒子位置
//            paint.setColor(color);
//            canvas.drawCircle(x, y, size * 0.5f, paint);
        }
    }

}



