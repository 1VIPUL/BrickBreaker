package com.example.brickbreaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;


import androidx.annotation.NonNull;

import java.util.Random;


public class GameView extends View {
    Context context;
    float ballX,ballY;
    Velocity velocity=new Velocity(25,32);
    Handler handler;
    final long UPDATE_MILLIS=60;
    Runnable runnable;
    Paint textPaint=new Paint();
    Paint healthAPaint=new Paint();
    Paint brickPaint=new Paint();
    float TEXT_SIZE=120;
    float paddleX,paddleY;
    int points=0;
    int life=3;
    Bitmap ball,paddle;
    int dwidth,dheight;

    int ballwidth,ballheight;
    MediaPlayer mphit,mpmiss,mpbreak;
    Random random;
    Brick[] bricks=new Brick[30];
    int numbricks=0;
    int brokenbbricks=0;
    boolean gameOver=false;
    private float oldpaddleX;
    private float olDX;


    public GameView(Context context) {
        super(context);
        this.context=context;
        ball= BitmapFactory.decodeResource(getResources(),R.drawable.ball);
        paddle=BitmapFactory.decodeResource(getResources(),R.drawable.paddle);
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };

//      Soundtrack assign
        mphit=MediaPlayer.create(context,R.raw.hit);
        mpmiss=MediaPlayer.create(context,R.raw.miss);
        mpbreak=MediaPlayer.create(context,R.raw.dropped);
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        healthAPaint.setColor(Color.GREEN);
        brickPaint.setColor(Color.argb(255,249,129,0));
        Display display=((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        dwidth=size.x;
        dheight=size.y;
        random=new Random();
        ballX=random.nextInt(dwidth-50);
        ballY=dheight/3;
        paddleY=(dheight*4)/5;
        paddleX=dwidth/2-paddle.getWidth()/2;
        ballwidth=ball.getWidth();
        ballheight=ball.getHeight();
        createbricks();


    }

    private void createbricks() {
        int brickwidth=dwidth/8;
        int brickheight=dheight/16;
        for(int column=0;column<8;column++){
            for(int row=0;row<3;row++){
                bricks[numbricks]=new Brick(row,column,brickwidth,brickheight);
                numbricks++;
            }
        }
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        ballX += velocity.getX();
        ballY += velocity.getY();
        if ((ballX >= dwidth - ball.getWidth()) || ballX <= 0) {
            velocity.setX(velocity.getX() * -1);
        }
        if (ballY <= 0) {
            velocity.setY(velocity.getY() * -1);
        }
        if (ballY > paddleY + paddle.getHeight()) {
            ballX = 1 + random.nextInt(dwidth - ball.getWidth() - 1);
            ballY = dheight / 3;
            if (mpmiss != null) {
                mpmiss.start();
            }
            velocity.setX(xVelocity());
            velocity.setY(32);
            life--;
            if (life == 0) {
                gameOver = true;
                launchGameOver();
            }
        }
            if((ballX +ball.getWidth()>=paddleX)
            &&(ballX <=paddleX +paddle.getWidth())
            &&(ballY + ball.getHeight() >=paddleY)
            && (ballY +ball.getHeight()<=paddleY + paddle.getHeight())){
                if(mphit !=null){
                    mphit.start();
                }
                //increasing difficulty
                velocity.setX(velocity.getX() +1);
                velocity.setY((velocity.getY()+1)* -1);
            }
            canvas.drawBitmap(ball,ballX,ballY,null);
            canvas.drawBitmap(paddle,paddleX,paddleY,null);
            for(int i=0;i<numbricks;i++){
                if(bricks[i].getVisibility()){
                    canvas.drawRect(bricks[i].column * bricks[i].width +1, bricks[i].row *bricks[i].height +1,bricks[i].column *bricks[i].width +bricks[i].width -1,bricks[i].row *bricks[i].height +bricks[i].height -1,brickPaint);
                }
            }
            canvas.drawText(""+ points,20,TEXT_SIZE,textPaint);
            if(life==2){
                healthAPaint.setColor(Color.YELLOW);
            } else if (life==1) {
                healthAPaint.setColor(Color.RED);
            }
            canvas.drawRect(dwidth-200,30,dwidth-200 +60*life,80,healthAPaint);

            for(int i=0;i<numbricks;i++){
                if(bricks[i].getVisibility()){
                    if (ballX +ballwidth >= bricks[i].column * bricks[i].width
                    && ballX <=bricks[i].column * bricks[i].width +bricks[i].width
                    && ballY <=bricks[i].row *bricks[i].height +bricks[i].height
                    && ballY >=bricks[i].row * bricks[i].height){
                        if(mpbreak != null){
                            mpbreak.start();
                        }
                        velocity.setY((velocity.getY()+ 1)* -1);
                        bricks[i].setVisible(false);
                        points +=10;
                        brokenbbricks++;
                        if(brokenbbricks ==24){
                            launchGameOver();
                        }
                    }
                }
            }
            if(brokenbbricks==numbricks){
                gameOver=true;
            }
            if(!gameOver){
                handler.postDelayed(runnable, UPDATE_MILLIS);
            }

        }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX=event.getX();
        float touchY=event.getY();
        if(touchY >=paddleY){
            int action=event.getAction();
            if(action ==MotionEvent.ACTION_DOWN){
                 olDX = event.getX();
                oldpaddleX=paddleX;
            }
            //below block performs as long as user moving his cursor
            if(action==MotionEvent.ACTION_MOVE){
                float shift=olDX-touchX;
                float newPaddleX=oldpaddleX-shift;
                if(newPaddleX <=0)
                    paddleX=0;
                else if (newPaddleX >=dwidth-paddle.getWidth()) {
                    paddleX=dwidth-paddle.getWidth();
                }
                else
                    paddleX=newPaddleX;
            }
        }
        return true;

    }

    private void launchGameOver() {
        handler.removeCallbacksAndMessages(null);
        Intent intent=new Intent(context,GameOver.class);
        intent.putExtra("points",points);
        context.startActivity(intent);
        ((Activity)context ).finish();
    }

    private int xVelocity() {
        int[] values={-35,-30,-25,25,30,35};
        int index=random.nextInt(6);
        return values[index];

    }
}
