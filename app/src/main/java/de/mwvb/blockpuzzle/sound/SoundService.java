package de.mwvb.blockpuzzle.sound;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import de.mwvb.blockpuzzle.R;

public class SoundService implements ISoundService {
    private Context context;
    private SoundPool soundPool;
    private int crunch;
    private int money;
    private int explosion;
    private MediaPlayer laughter;
    private MediaPlayer jeqa;

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
        explosion = soundPool.load(context, R.raw.explosion, 0);
        laughter = MediaPlayer.create(context, R.raw.laughter);
        jeqa = MediaPlayer.create(context, R.raw.jeqa);
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
        play(big ? explosion : crunch);
    }

    @Override
    public void firstGravitation() {
        jeqa.start();
    }

    @Override
    public void gameOver() {
        laughter.start();
    }

    @Override
    public void backPressed(boolean gameOver) {
        if (gameOver) {
            gameOver();
        } else {
            clear(false);
        }
    }

    @Override
    public void oneColor() {
        play(money);
    }

    @Override
    public void doesNotWork() {
        // TODO
    }
}
