package de.mwvb.blockpuzzle.sound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.provider.MediaStore;

import de.mwvb.blockpuzzle.R;

public class SoundService implements ISoundService {
    private Context context;
    private SoundPool soundPool;
    private int crunch;
    private int money;
    private MediaPlayer explosion;
    private MediaPlayer laughter;
    private MediaPlayer jeqa;
    private MediaPlayer applause;
    private int brickdrop2;

    /** init SoundService */
    @SuppressLint("ObsoleteSdkInt") // falls ich das API Level senken sollte
    public void init(Context context) {
        this.context = context;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(20)
                    .build();
        } else {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }

        crunch = soundPool.load(context, R.raw.crunch, 0);
        money = soundPool.load(context, R.raw.money, 0);
        explosion = MediaPlayer.create(context, R.raw.explosion);
        laughter = MediaPlayer.create(context, R.raw.laughter);
        jeqa = MediaPlayer.create(context, R.raw.jeqa);
        applause = MediaPlayer.create(context, R.raw.applause);
        brickdrop2 = soundPool.load(context, R.raw.brickdrop2, 0);
    }

    /** destroy SoundService */
    public void destroy() {
        soundPool.release();
        soundPool = null;
    }

    private void play(int soundId) {
        soundPool.play(soundId, 1, 1, 0, 0, 1f);
    }

    @Override
    public void clear(boolean big) {
        if (big) {
            explosion.start();
        } else {
            play(crunch);
        }
    }

    @Override
    public void firstGravitation() {
        jeqa.start();
    }

    @Override
    public void gameOver() {
        quiet();
        laughter.start();
    }

    @Override
    public void youWon() {
        quiet();
        applause.start();
    }

    @Override
    public void oneColor() {
        play(money);
    }

    @Override
    public void doesNotWork() {
        // no sound, maybe in the future
    }

    @Override
    public void shake() {
        play(brickdrop2);
    }

    private void quiet() {
        jeqa.stop();
        explosion.stop();
    }
}
