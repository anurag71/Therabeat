package com.anurag.therabeat;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.util.Timer;
import java.util.TimerTask;

public class Binaural implements BeatsEngine {

    private final int SAMPLE_RATE = 44100;
    private int sampleCount;
    private boolean doRelease;
    private AudioTrack mAudio;
    private boolean isPlaying;
    private float factor;
    private float frequency;

    public Binaural(float frequency, float isoBeat, float factor) {
        this.frequency = frequency;
        int amplitudeMax = Helpers.getAdjustedAmplitudeMax(frequency);
        this.factor = factor;
        float freqLeft = frequency - (isoBeat / 2);
        float freqRight = frequency + (isoBeat / 2);

        //period of the sine waves
        int sCountLeft = (int) ((float) SAMPLE_RATE / freqLeft);
        int sCountRight = (int) ((float) SAMPLE_RATE / freqRight);

        sampleCount = Helpers.getLCM(sCountLeft, sCountRight) * 2;
		int buffSize = sampleCount * 4;

		mAudio = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				buffSize, AudioTrack.MODE_STATIC);

		short samples[] = new short[sampleCount];
		int amplitude = amplitudeMax;
		double twopi = 8. * Math.atan(1.);
		double leftPhase = 0.0;
		double rightPhase = 0.0;

		for (int i = 0; i < sampleCount; i = i + 2) {

			samples[i] = (short) (amplitude * Math.sin(leftPhase));
			samples[i + 1] = (short) (amplitude * Math.sin(rightPhase));

			if (i/2 % sCountLeft == 0) {
				leftPhase = 0.0;
			}
			leftPhase += twopi * freqLeft / SAMPLE_RATE;
			if (i/2 % sCountRight == 0) {
				rightPhase = 0.0;
			}
			rightPhase += twopi * freqRight / SAMPLE_RATE;
		}
		mAudio.write(samples, 0, sampleCount);
		mAudio.setVolume(0.0f);
		Helpers.napThread();
	}

	public void release() {
		doRelease = true;
		stop();
	}

	float volume = 0;

	public void start() {
		mAudio.reloadStaticData();
		mAudio.setLoopPoints(0, sampleCount / 2, -1);
		isPlaying = true;
		Helpers.napThread();
		mAudio.play();
//		mAudio.setVolume(1);
		startFadeIn();

	}

	private void startFadeIn() {
		volume = 0;
		final int FADE_DURATION = 3000; //The duration of the fade
		//The amount of time between volume changes. The smaller this is, the smoother the fade
		final int FADE_INTERVAL = 1;
		final float MAX_VOLUME = factor / 100; //The volume will increase from 0 to 1
		int numberOfSteps = FADE_DURATION / FADE_INTERVAL; //Calculate the number of fade steps
		//Calculate by how much the volume changes each step
		final float deltaVolume = MAX_VOLUME / (float) numberOfSteps;

		//Create a new Timer and Timer task to run the fading outside the main UI thread
		final Timer timer = new Timer(true);
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				fadeInStep(deltaVolume); //Do a fade step
				//Cancel and Purge the Timer if the desired volume has been reached
				if (volume >= MAX_VOLUME) {
					timer.cancel();
					timer.purge();
				}
			}
		};

		timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL);
	}

	private void fadeInStep(float deltaVolume) {
		mAudio.setVolume(volume);
		volume += deltaVolume;

	}

	public void stop() {
		mAudio.setVolume(0.0f);
		Helpers.napThread();
		mAudio.stop();
		isPlaying = false;
		if (doRelease) {
			mAudio.flush();
			mAudio.release();
		}

	}

    public boolean getIsPlaying() {
        return isPlaying;
    }

    @Override
    public void setVolume(float volume) {
        if (mAudio != null) {
            mAudio.setVolume(volume / 100);
        }
        factor = volume;
    }

    @Override
    public float getFrequency() {
        return this.frequency;
    }


}
