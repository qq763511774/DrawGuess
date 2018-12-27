package DrawGuess.Server;

import java.util.Timer;
import java.util.TimerTask;

public class GameThread {

    public static void startGame(int number) {

        for (int i = 0; i < number; i++) {
            // 安排玩家

            // 发送词语及提示

            // 本轮游戏倒计时
            Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                int cnt = 60;

                @Override
                public void run() {
                    cnt--;
                    if (cnt <= 0) {
                        cancel();
                    }
                }
            }, 0, 1000);
        }
    }

    public static String judge(String guessString) {
        // 返回的字符串为 "YES guessString" 或者 "NOP guessString"
        // 最好能把guessString里面跟答案一样的字替换成特殊的符号

        return "YES " + guessString;
    }
}
