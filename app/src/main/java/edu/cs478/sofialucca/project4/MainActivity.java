package edu.cs478.sofialucca.project4;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private HashMap<Integer,Integer> positions;
    private ArrayList<TextView> textViewArray;
    static final int PLAYER_MOVE_1 = 1;
    static final int PLAYER_MOVE_2 = 2;
    private ThreadP1 t1;
    private ThreadP2 t2;
    private TextView textWinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bttnNewGame = findViewById(R.id.btnNewGame);
        for(int i =0; i< 9;i++){
            positions.put(i,0);

        }
        textViewArray = new ArrayList<>(8);
        textViewArray.add((TextView) findViewById(R.id.text_0));
        textViewArray.add((TextView) findViewById(R.id.text_1));
        textViewArray.add((TextView) findViewById(R.id.text_2));
        textViewArray.add((TextView) findViewById(R.id.text_3));
        textViewArray.add((TextView) findViewById(R.id.text_4));
        textViewArray.add((TextView) findViewById(R.id.text_5));
        textViewArray.add((TextView) findViewById(R.id.text_6));
        textViewArray.add((TextView) findViewById(R.id.text_7));
        textViewArray.add((TextView) findViewById(R.id.text_8));
        textWinner = findViewById(R.id.textWinner);
        t1 = new ThreadP1();
        t1.start();
        t2 = new ThreadP2();
        t2.start();
    }
    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int what = msg.what ;
            boolean winner = false;
            int count = 0;

            switch (what) {
                case PLAYER_MOVE_1:
                    positions.put(msg.arg1,1);
                    //TODO change interface
                    textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_1);
                    for(int i = 0; i<9;i++) {

                        if (positions.get(i) != 0) {
                            count++;
                            if (((i == 0 || i % 3 == 0) && positions.get(i + 1) == 1 && positions.get(i + 2) == 1) || ((i <= 2) && positions.get(i + 3) == 1 && positions.get(i + 6) == 1)) {
                                //Toast.makeText(this,"Player "+ player +" is the winner",Toast.LENGTH_LONG).show();
                                winner = true;
                                break;
                            }
                        }
                    }
                        if(winner){
                            textWinner.setText("GAME OVER: PLAYER 1 WINNER");
                        }else if(count == positions.size()) {
                            textWinner.setText("GAME OVER: TIE");
                        }else{
                            Message newMsg;
                            newMsg = t2.myHandler2.obtainMessage(PLAYER_MOVE_1);
                            newMsg.arg1 = msg.arg1;
                            newMsg.sendToTarget();

                        }


                    break;
                case PLAYER_MOVE_2:
                    positions.put(msg.arg1,2);
                    //TODO change interface
                    for(int i = 0; i<9;i++){
                        textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_2);

                        if(positions.get(i) != 0){
                            count++;
                            if(((i == 0 || i%3 == 0) && positions.get(i+1) == 2 && positions.get(i+2) == 2) || ((i <=2 ) && positions.get(i+3) == 2 && positions.get(i+6) == 2)){
                                //Toast.makeText(this,"Player "+ player +" is the winner",Toast.LENGTH_LONG).show();
                                winner = true;
                                break;
                            }
                        }

                    }
                    if(winner){
                        textWinner.setText("GAME OVER: PLAYER 1 WINNER");
                    }else if(count == positions.size()) {

                        textWinner.setText("GAME OVER: TIE");
                    }else{
                        Message newMsg;
                        newMsg = t1.myHandler1.obtainMessage(PLAYER_MOVE_1);
                        newMsg.arg1 = msg.arg1;
                        newMsg.sendToTarget();

                    }
                    break;
            }

        }
    };
    public class ThreadP1 extends Thread {

        public Handler myHandler1;
        private HashMap<Integer,Integer> positions;
        public void run(){
            Looper.prepare();
            for(int i =0; i< 9;i++){
                positions.put(i,0);
            }

            myHandler1 = new Handler(){
                public void handleMessage(Message msg){
                    switch (msg.what){
                        case PLAYER_MOVE_2:
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Log.i("THREAD1", "Woken up");
                            }
                            positions.put(msg.arg1,2);
                            int i;
                            for(i =0; i< 9;i++){
                                if(positions.get(i) == 0){
                                    break;
                                }
                            }

                            positions.put(i,1);
                            Message msgMove = Message.obtain();
                            Message newMsg = mHandler.obtainMessage(PLAYER_MOVE_1);
                            newMsg.arg1 = i;
                            newMsg.arg2 = 1;
                            break;
                    }

                }
            };


            Looper.loop();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.i("THREAD1", "Woken up");
            }



        }
    }
    public class ThreadP2 extends Thread {

        public Handler myHandler2;
        HashMap<Integer,Integer> positions;
        public void run(){
            Looper.prepare();
            for(int i =0; i< 9;i++){
                positions.put(i,0);
            }

            myHandler2 = new Handler(){
                public void handleMessage(Message msg){
                    boolean empty=true;
                    int firstFree=-1;
                    int newPos = -1;
                    switch (msg.what){
                        case PLAYER_MOVE_1:
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Log.i("THREAD2", "Woken up");
                            }
                            //if already filled check for adjacents of it's side
                            positions.put(msg.arg1,1);
                            for(int i = 0; i<9;i++){
                                if(positions.get(i) == 2){
                                    empty = false;
                                }
                                if(positions.get(i) == 0){
                                    firstFree = i;
                                    if((i-1>0 && positions.get(i-1) == 2 )|| (i+1 <9 && positions.get(i+1) == 2) || (i+3<9 && positions.get(i+3)== 2)||(i-3>0 && positions.get(i-3) == 2)){
                                        newPos = i;
                                        break;
                                    }

                                }

                            }
                            if(!empty){
                                if(newPos ==-1){

                                    newPos = firstFree;
                                }
                            }else{
                                newPos = firstFree;
                            }
                            positions.put(newPos,2);
                            Message newMsg = mHandler.obtainMessage(PLAYER_MOVE_2);
                            newMsg.arg1 = newPos;
                            newMsg.sendToTarget();
                            break;
                    }

                }
            };

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.i("THREAD1", "Woken up");
            }

            Looper.loop();





        }
    }

}

