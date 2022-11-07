package edu.cs478.sofialucca.project4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private HashMap<Integer,Integer> positions;
    private ArrayList<TextView> textViewArray;
    static final int PLAYER_MOVE_1 = 1;
    static final int PLAYER_MOVE_2 = 2;
    static final int FIRST_MOVE = 0;
    static final int TERMINATED = 3;
    static final String TIE_MSG = "GAME OVER: TIE";
    static final String PLAYER1_MSG = "GAME OVER: PLAYER 1 WINS";
    static final String PLAYER2_MSG = "GAME OVER: PLAYER 2 WINS";

    private ThreadP1 t1;
    private ThreadP2 t2;
    private TextView textWinner;
    Runnable terminatedRunnable1,terminatedRunnable2;
    public int nGame;
    private final Handler uiHandler1 = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int what = msg.what ;
            boolean winner = false;
            int count = 0;
            Message newMsg;

            Log.i("UI", "Move received "+ msg.arg1);
            switch (what) {
                case PLAYER_MOVE_1:
                        if((int) msg.obj == nGame){
                            positions.put(msg.arg1,1);
                            textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_1);
                            for(int i = 0; i<9;i++) {

                                if (positions.get(i) != 0) {
                                    count++;
                                    if (((i == 0 || i % 3 == 0) && positions.get(i) == 1 && positions.get(i + 1) == 1 && positions.get(i + 2) == 1) || ((i <= 2) && positions.get(i) == 1 && positions.get(i + 3) == 1 && positions.get(i + 6) == 1)) {
                                        winner = true;
                                        break;
                                    }
                                }
                            }
                            if(winner){
                                textWinner.setText(PLAYER1_MSG);
                                textWinner.setTextColor(getResources().getColor(R.color.purple_700));
                                /*t1.myHandler1.obtainMessage(TERMINATED).sendToTarget();
                                t2.myHandler2.obtainMessage(TERMINATED).sendToTarget();*/
                                postRunnables();
                            }else if(count == positions.size()) {
                                textWinner.setText(TIE_MSG);
                                textWinner.setTextColor(getResources().getColor(R.color.black));
                                /*t1.myHandler1.obtainMessage(TERMINATED).sendToTarget();
                                t2.myHandler2.obtainMessage(TERMINATED).sendToTarget();*/
                                postRunnables();
                            }else{
                                newMsg = t2.myHandler2.obtainMessage(PLAYER_MOVE_1);
                                newMsg.arg1 = msg.arg1;
                                newMsg.obj = msg.obj;

                                newMsg.sendToTarget();

                            }
                        }





                    break;
                case FIRST_MOVE:
                    if((int) msg.obj == nGame){
                        positions.put(msg.arg1,1);
                        textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_1);
                        newMsg = t2.myHandler2.obtainMessage(FIRST_MOVE);
                        newMsg.arg1 = msg.arg1;
                        newMsg.obj = msg.obj;
                        newMsg.sendToTarget();
                    }

                    break;
            }

        }
    };
    private final Handler uiHandler2= new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int what = msg.what ;
            boolean winner = false;
            int count = 0;
            Message newMsg;
            switch (what) {
                case PLAYER_MOVE_2:
                    if((int) msg.obj == nGame){
                        positions.put(msg.arg1,2);
                        textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_2);

                        for(int i = 0; i<9;i++){

                            if(positions.get(i) != 0){
                                count++;
                                if(((i == 0 || i%3 == 0) && positions.get(i+1) == 2 && positions.get(i+2) == 2 && positions.get(i) == 2) || ((i <=2 ) && positions.get(i) == 2 && positions.get(i+3) == 2 && positions.get(i+6) == 2)){
                                    winner = true;
                                    break;
                                }
                            }

                        }
                        if(winner){
                            textWinner.setText(PLAYER2_MSG);
                            textWinner.setTextColor(getResources().getColor(R.color.red));
                            /*t1.myHandler1.obtainMessage(TERMINATED).sendToTarget();
                            t2.myHandler2.obtainMessage(TERMINATED).sendToTarget();*/
                            postRunnables();
                        }else if(count == positions.size()) {
                            textWinner.setText(TIE_MSG);
                            textWinner.setTextColor(getResources().getColor(R.color.black));
                            /*t1.myHandler1.obtainMessage(TERMINATED).sendToTarget();
                            t2.myHandler2.obtainMessage(TERMINATED).sendToTarget();*/
                            postRunnables();
                        }else{
                            newMsg = t1.myHandler1.obtainMessage(PLAYER_MOVE_2);
                            newMsg.arg1 = msg.arg1;
                            newMsg.obj = msg.obj;
                            newMsg.sendToTarget();

                        }

                    }



                    break;
                case FIRST_MOVE:
                    if((int) msg.obj == nGame){
                        positions.put(msg.arg1,2);
                        textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_2);

                        newMsg = t1.myHandler1.obtainMessage(PLAYER_MOVE_2);
                        newMsg.arg1 = msg.arg1;
                        newMsg.obj = msg.obj;
                        newMsg.sendToTarget();
                    }

                    break;

            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bttnNewGame = findViewById(R.id.btnNewGame);

        terminatedRunnable1 = new Runnable() {
            @Override
            public void run() {
                //Thread.currentThread().interrupt();
                t1.positions = new ArrayList<>(9);
                for(int i =0; i< 9;i++){
                    t1.positions.add(i);
                }
            }
        };
        terminatedRunnable2 = new Runnable() {
            @Override
            public void run() {
                //Thread.currentThread().interrupt();
                //t1.positions = new ArrayList<>(9);
                for(int i =0; i< 9;i++){
                    t2.positions.put(i,0);
                }
            }
        };
        positions = new HashMap<>();
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
        t2 = new ThreadP2();
        nGame = 0;
        bttnNewGame.setOnClickListener(v -> {
            Log.i("UIThread", "Click on button");
            nGame++;
            if((t1.isAlive() || t2.isAlive()) && !(t1.isInterrupted() && t2.isInterrupted())){

                Log.i("UIThread", "Click on button alive threads");
                for(int i = 0; i<9; i++){
                    textViewArray.get(i).setBackgroundResource(R.drawable.border);
                    positions.put(i,0);
                }

                textWinner.setText("");


                t1.myHandler1.obtainMessage(FIRST_MOVE).sendToTarget();

            }else{
                t1.start();
                t2.start();
            }


        });

    }
    public void postRunnables(){
        t1.myHandler1.postAtFrontOfQueue(terminatedRunnable1);
        t2.myHandler2.postAtFrontOfQueue(terminatedRunnable2);
    }
    public class ThreadP1 extends Thread {

        public Handler myHandler1;
        private ArrayList<Integer> positions;
        Random random;
        public void run(){
            Log.i("THREAD1", "STARTED");
            Looper.prepare();
            positions = new ArrayList<>(9);
            random = new Random();


            myHandler1 = new Handler(){
                public void handleMessage(Message msg){
                    int pos;
                    Message newMsg;
                    switch (msg.what){
                        case PLAYER_MOVE_2:
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Log.i("THREAD1", "Woken up"+msg.arg1+msg.obj);
                                break;
                            }

                            positions.remove(positions.indexOf(msg.arg1));

                            pos = random.nextInt(positions.size());

                            newMsg = uiHandler1.obtainMessage(PLAYER_MOVE_1);
                            newMsg.arg1 = positions.get(pos);
                            newMsg.obj = msg.obj;

                            Log.i("THREAD 1","Move " +newMsg.arg1);
                            positions.remove(pos);
                            newMsg.sendToTarget();
                            break;
                        case FIRST_MOVE:
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Log.i("THREAD1","Thread interrupted");
                            }
                            positions = new ArrayList<>(9);
                            for(int i =0; i< 9;i++){
                                positions.add(i);
                            }
                            pos = random.nextInt(9);
                            positions.remove(pos);
                            newMsg = uiHandler1.obtainMessage(FIRST_MOVE);
                            newMsg.arg1 = pos;
                            newMsg.obj = nGame;

                            newMsg.sendToTarget();
                            break;
                        case TERMINATED:
                            positions = new ArrayList<>(9);
                            for(int i =0; i< 9;i++){
                                positions.add(i);
                            }
                            break;
                    }

                }
            };

            for(int i =0; i< 9;i++){
                positions.add(i);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.i("THREAD1","Thread interrupted");
            }
            int pos = random.nextInt(9);
            positions.remove(pos);
            Message newMsg = uiHandler1.obtainMessage(FIRST_MOVE);
            newMsg.arg1 = pos;
            newMsg.obj = nGame;
            newMsg.sendToTarget();
            Looper.loop();

        }
    }
    public class ThreadP2 extends Thread {

        public Handler myHandler2;
        HashMap<Integer,Integer> positions;
        public void run(){
            Looper.prepare();
            positions = new HashMap<>();


            myHandler2 = new Handler(){
                public void handleMessage(Message msg){
                    boolean empty=true;
                    int firstFree=-1;
                    int newPos = -1;
                    Message newMsg;
                    switch (msg.what){
                        case PLAYER_MOVE_1:
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Log.i("THREAD2", "Woken up");
                                break;
                            }
                            //if already filled check for adjacents of it's side
                            positions.put(msg.arg1,1);
                            for(int i = 0; i<9;i++){
                                if(positions.get(i) == 2){
                                    empty = false;
                                }
                                if(positions.get(i) == 0){
                                    if(firstFree == -1){
                                        firstFree = i;
                                    }

                                    if((i-1>0 && i%3 != 0 && positions.get(i-1) == 2 )|| (i+1 <9 && i%2!=0 && positions.get(i+1) == 2) || (i+3<9 && positions.get(i+3)== 2)||(i-3>0 && positions.get(i-3) == 2)){
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
                            newMsg = uiHandler2.obtainMessage(PLAYER_MOVE_2);
                            newMsg.arg1 = newPos;
                            newMsg.obj = msg.obj;
                            newMsg.sendToTarget();
                            break;
                        case FIRST_MOVE:
                            for(int i =0; i< 9;i++){
                                positions.put(i,0);
                            }
                            positions.put(msg.arg1,1);
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Log.i("THREAD2", "Woken up");
                            }
                            for(int i = 0; i<9;i++){
                                if(positions.get(i) == 0){
                                    if(firstFree == -1){
                                        firstFree = i;
                                        break;
                                    }

                                }

                            }

                            positions.put(firstFree,2);
                            newMsg = uiHandler2.obtainMessage(FIRST_MOVE);
                            newMsg.arg1 = firstFree;
                            newMsg.obj = msg.obj;
                            newMsg.sendToTarget();
                            break;
                        case TERMINATED:
                            for(int i =0; i< 9;i++){
                                positions.put(i,0);
                            }
                            break;
                    }

                }
            };
            Looper.loop();



        }
    }

}

