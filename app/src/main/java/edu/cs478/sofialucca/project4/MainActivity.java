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
    private HashMap<Integer, Integer> positions;
    private ArrayList<TextView> textViewArray;
    static final int PLAYER_MOVE = 1;
    static final int FIRST_MOVE = 0;
    static final int TERMINATED = 3;
    static final String TIE_MSG = "GAME OVER: TIE";
    static final String PLAYER1_MSG = "GAME OVER: PLAYER 1 WINS";
    static final String PLAYER2_MSG = "GAME OVER: PLAYER 2 WINS";
    static final int MAX_MOVES = 20;

    private ThreadP1 t1;
    private ThreadP2 t2;
    private TextView textWinner;
    Runnable terminatedRunnable1, terminatedRunnable2;
    private int nGame;
    private int nMoves;
    private final Handler uiHandler1 = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int what = msg.what;
            boolean winner = false;
            Message newMsg;

            Log.i("UI", "Move received " + msg.arg1);
            if ((int) msg.obj == nGame) {
                nMoves++;
                switch (what) {
                    case PLAYER_MOVE:
                        positions.put(msg.arg1, 1);
                        textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_1);
                        if(msg.arg2 >-1){
                            positions.put(msg.arg2, 0);
                            textViewArray.get(msg.arg2).setBackgroundResource(R.drawable.border);

                        }
                        for (int i = 0; i < 9; i++) {
                            if (positions.get(i) != 0) {
                                if (((i == 0 || i % 3 == 0) && positions.get(i) == 1 && positions.get(i + 1) == 1 && positions.get(i + 2) == 1) || ((i <= 2) && positions.get(i) == 1 && positions.get(i + 3) == 1 && positions.get(i + 6) == 1)) {
                                    winner = true;
                                    break;
                                }
                            }
                        }
                        if (winner) {
                            textWinner.setText(PLAYER1_MSG);
                            textWinner.setTextColor(getResources().getColor(R.color.purple_700));
                                /*t1.myHandler1.obtainMessage(TERMINATED).sendToTarget();
                                t2.myHandler2.obtainMessage(TERMINATED).sendToTarget();*/
                            postRunnables();
                        } else if (nMoves == MAX_MOVES) {
                            textWinner.setText(TIE_MSG);
                            textWinner.setTextColor(getResources().getColor(R.color.black));
                                /*t1.myHandler1.obtainMessage(TERMINATED).sendToTarget();
                                t2.myHandler2.obtainMessage(TERMINATED).sendToTarget();*/
                            postRunnables();
                        } else {
                            newMsg = t2.myHandler2.obtainMessage(PLAYER_MOVE);
                            newMsg.arg1 = msg.arg1;
                            newMsg.obj = msg.obj;
                            newMsg.arg2 = msg.arg2;
                            newMsg.sendToTarget();

                        }


                        break;

                    case FIRST_MOVE:
                        positions.put(msg.arg1, 1);
                        textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_1);
                        newMsg = t2.myHandler2.obtainMessage(FIRST_MOVE);
                        newMsg.arg1 = msg.arg1;
                        newMsg.obj = msg.obj;
                        newMsg.arg2 = msg.arg2;
                        newMsg.sendToTarget();


                        break;
                }

            }

        }
    };
    private final Handler uiHandler2 = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            int what = msg.what;
            boolean winner = false;
            Message newMsg;
            if ((int) msg.obj == nGame) {
                nMoves++;
                switch (what) {
                    case PLAYER_MOVE:
                        positions.put(msg.arg1, 2);
                        if(msg.arg2 >-1){
                            positions.put(msg.arg2, 0);
                            textViewArray.get(msg.arg2).setBackgroundResource(R.drawable.border);

                        }
                        textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_2);

                        for (int i = 0; i < 9; i++) {

                            if (positions.get(i) != 0) {
                                if (((i == 0 || i % 3 == 0) && positions.get(i + 1) == 2 && positions.get(i + 2) == 2 && positions.get(i) == 2) || ((i <= 2) && positions.get(i) == 2 && positions.get(i + 3) == 2 && positions.get(i + 6) == 2)) {
                                    winner = true;
                                    break;
                                }
                            }

                        }
                        if (winner) {
                            textWinner.setText(PLAYER2_MSG);
                            textWinner.setTextColor(getResources().getColor(R.color.red));
                            /*t1.myHandler1.obtainMessage(TERMINATED).sendToTarget();
                            t2.myHandler2.obtainMessage(TERMINATED).sendToTarget();*/
                            postRunnables();
                        } else if (nMoves == MAX_MOVES) {
                            textWinner.setText(TIE_MSG);
                            textWinner.setTextColor(getResources().getColor(R.color.black));
                            /*t1.myHandler1.obtainMessage(TERMINATED).sendToTarget();
                            t2.myHandler2.obtainMessage(TERMINATED).sendToTarget();*/
                            postRunnables();
                        } else {
                            newMsg = t1.myHandler1.obtainMessage(PLAYER_MOVE);
                            newMsg.arg1 = msg.arg1;
                            newMsg.obj = msg.obj;
                            newMsg.arg2 = msg.arg2;
                            newMsg.sendToTarget();

                        }

                        break;

                    case FIRST_MOVE:
                        positions.put(msg.arg1, 2);
                        textViewArray.get(msg.arg1).setBackgroundResource(R.drawable.player_2);

                        newMsg = t1.myHandler1.obtainMessage(PLAYER_MOVE);
                        newMsg.arg1 = msg.arg1;
                        newMsg.arg2 = msg.arg2;

                        newMsg.obj = msg.obj;
                        newMsg.sendToTarget();


                        break;
                }


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
                Thread.currentThread().interrupt();
                Looper.myLooper().quit();
            }
        };
        terminatedRunnable2 = new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().interrupt();
                Looper.myLooper().quit();
                //Thread.currentThread().Looper.getMainLooper().quit();
            }
        };
        positions = new HashMap<>();
        for (int i = 0; i < 9; i++) {
            positions.put(i, 0);

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
        /*t1 = new ThreadP1();
        t2 = new ThreadP2();*/
        nGame = 0;
        bttnNewGame.setOnClickListener(v -> {
            Log.i("UIThread", "Click on button");
            Log.i("UIThread"," "+t1 );
            nGame++;
            nMoves = 0;

            if (t1!= null && t2!= null &&(t1.isAlive() || t2.isAlive())) {

                Log.i("UIThread", "Click on button alive threads");
                for (int i = 0; i < 9; i++) {
                    textViewArray.get(i).setBackgroundResource(R.drawable.border);
                    positions.put(i, 0);
                }

                textWinner.setText("");


                //t1.myHandler1.obtainMessage(FIRST_MOVE).sendToTarget();
                postRunnables();
                /*t1 = new ThreadP1();
                t2 = new ThreadP2();*/
            }else if(nGame > 1){
                Log.i("UIThread", "Click on button interrupted threads");
                for (int i = 0; i < 9; i++) {
                    textViewArray.get(i).setBackgroundResource(R.drawable.border);
                    positions.put(i, 0);
                }

                textWinner.setText("");
            }
                t1 = new ThreadP1();
                t2 = new ThreadP2();
             //else {
                t1.start();
                t2.start();
            //}


        });

    }

    public void postRunnables() {
        t1.myHandler1.postAtFrontOfQueue(terminatedRunnable1);
        t2.myHandler2.postAtFrontOfQueue(terminatedRunnable2);
    }

    public class ThreadP1 extends Thread {

        public Handler myHandler1;
        private ArrayList<Integer> positions1;
        private ArrayList<Integer> currentPositions1;
        Random random;

        public void run() {
            Log.i("THREAD1", "STARTED");
            Looper.prepare();
            positions1 = new ArrayList<>(9);
            currentPositions1 = new ArrayList<>();
            random = new Random();


            myHandler1 = new Handler() {
                public void handleMessage(Message msg) {
                    int pos;
                    int posRemove = -1;
                    Message newMsg;
                    switch (msg.what) {
                        case PLAYER_MOVE:
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Log.i("THREAD1", "Woken up" + msg.arg1 + msg.obj);
                                break;
                            }
                            Log.i("THREAD 1 msg", "Move " + msg.arg2);
                            positions1.remove(positions1.indexOf(msg.arg1));
                            if(msg.arg2 >-1){
                                positions1.add(msg.arg2);

                            }
                            pos = random.nextInt(positions1.size());

                            newMsg = uiHandler1.obtainMessage(PLAYER_MOVE);
                            newMsg.arg1 = positions1.get(pos);
                            newMsg.obj = msg.obj;
                            positions1.remove(pos);
                            if (currentPositions1.size() == 3) {
                                posRemove = random.nextInt(currentPositions1.size());
                                posRemove = currentPositions1.remove(posRemove);
                                positions1.add(posRemove);

                            }
                            newMsg.arg2 = posRemove;
                            currentPositions1.add(newMsg.arg1);

                            newMsg.sendToTarget();
                            break;

                        case FIRST_MOVE:
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Log.i("THREAD1", "Thread interrupted");
                            }
                            positions1 = new ArrayList<>(9);
                            currentPositions1 = new ArrayList<>();
                            for (int i = 0; i < 9; i++) {
                                positions1.add(i);
                            }
                            pos = random.nextInt(9);
                            currentPositions1.add(positions1.remove(pos));
                            newMsg = uiHandler1.obtainMessage(FIRST_MOVE);
                            newMsg.arg1 = pos;
                            newMsg.arg2 = posRemove;
                            newMsg.obj = nGame;

                            newMsg.sendToTarget();
                            break;
                        case TERMINATED:
                            positions1 = new ArrayList<>(9);
                            currentPositions1 = new ArrayList<>();
                            for (int i = 0; i < 9; i++) {
                                positions1.add(i);
                            }
                            break;
                    }

                }
            };

            for (int i = 0; i < 9; i++) {
                positions1.add(i);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Log.i("THREAD1", "Thread interrupted");
            }
            int pos = random.nextInt(9);
            currentPositions1.add(positions1.remove(pos));
            Message newMsg = uiHandler1.obtainMessage(FIRST_MOVE);
            newMsg.arg1 = pos;
            newMsg.arg2 = -1;
            newMsg.obj = nGame;
            newMsg.sendToTarget();
            Looper.loop();

        }
    }

    public class ThreadP2 extends Thread {

        public Handler myHandler2;
        private HashMap<Integer, Integer> positions2;
        private ArrayList<Integer> currentPositions2;
        public void run() {
            Looper.prepare();
            positions2 = new HashMap<>();
            currentPositions2 = new ArrayList<>();

            myHandler2 = new Handler() {
                public void handleMessage(Message msg) {
                    boolean empty = true;
                    int firstFree = -1;
                    int newPos = -1;
                    int posRemove = -1;
                    Message newMsg;
                    switch (msg.what) {
                        case PLAYER_MOVE:
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Log.i("THREAD2", "Woken up");
                                break;
                            }
                            //if already filled check for adjacents of it's side
                            positions2.put(msg.arg1, 1);
                            if(msg.arg2 >-1){
                                positions2.put(msg.arg2,0);
                            }
                            for (int i = 0; i < 9; i++) {
                                if (positions2.get(i) == 2) {
                                    empty = false;
                                }
                                if (positions2.get(i) == 0) {
                                    if (firstFree == -1) {
                                        firstFree = i;
                                    }
                                    Log.i("T2", "free " + i);
                                    if ((i - 1 > -1 && i % 3 != 0 && positions2.get(i - 1) == 2) || (i + 1 < 9 && i != 2 && i != 5 && positions2.get(i + 1) == 2) || (i + 3 < 9 && positions2.get(i + 3) == 2) || (i - 3 > -1 && positions2.get(i - 3) == 2)) {
                                        newPos = i;
                                        Log.i("T2", "new pos" + i);
                                        break;
                                    }
                                }
                            }
                            if (!empty) {
                                if (newPos == -1) {

                                    newPos = firstFree;
                                }
                            } else {
                                newPos = firstFree;
                            }
                            positions2.put(newPos, 2);
                            newMsg = uiHandler2.obtainMessage(PLAYER_MOVE);
                            newMsg.arg1 = newPos;
                            newMsg.obj = msg.obj;
                            if(currentPositions2.size() == 3){
                                posRemove = currentPositions2.get(0);
                                positions2.put(currentPositions2.remove(0),0);
                            }
                            newMsg.arg2 = posRemove;
                            currentPositions2.add(newPos);
                            newMsg.sendToTarget();
                            break;

                        case FIRST_MOVE:
                            for (int i = 0; i < 9; i++) {
                                positions2.put(i, 0);
                            }
                            positions2.put(msg.arg1, 1);
                            currentPositions2 = new ArrayList<>();
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                Log.i("THREAD2", "Woken up");
                            }
                            for (int i = 0; i < 9; i++) {
                                if (positions2.get(i) == 0) {
                                    Log.i("T2", "first free first move" + i);
                                    if (firstFree == -1) {
                                        firstFree = i;
                                        break;
                                    }

                                }

                            }

                            positions2.put(firstFree, 2);
                            currentPositions2.add(firstFree);
                            newMsg = uiHandler2.obtainMessage(FIRST_MOVE);
                            newMsg.arg1 = firstFree;
                            newMsg.obj = msg.obj;
                            newMsg.arg2 = posRemove;
                            newMsg.sendToTarget();
                            break;
                        case TERMINATED:
                            currentPositions2 = new ArrayList<>();

                            for (int i = 0; i < 9; i++) {
                                positions2.put(i, 0);
                            }
                            break;
                    }

                }
            };
            Looper.loop();


        }
    }

}

