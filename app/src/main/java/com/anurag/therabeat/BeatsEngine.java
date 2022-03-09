package com.anurag.therabeat;

/**
 * Created by mrrussell on 2/13/16.
 */
public interface BeatsEngine
{
    void start();
    void stop();
    void release();
    boolean getIsPlaying();
    void setVolume(float volume);

    float getFrequency();

}
