package de.mwvb.blockpuzzle.musik;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import de.mwvb.blockpuzzle.R;

public class Musik {
    private SoundPool soundPool;
    private int crunchSoundId;

    @SuppressLint("ObsoleteSdkInt") // falls ich das API Level senken sollte
    public void init(Context context) {
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

        crunchSoundId = soundPool.load(context, R.raw.crunch, 0);
    }

    public void playCrunchSound() {
        soundPool.play(crunchSoundId, 1, 1, 0, 0, 1f);
    }

    public void destroy() {
        soundPool.release();
        soundPool = null;
    }
}
