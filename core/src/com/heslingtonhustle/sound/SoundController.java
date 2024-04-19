package com.heslingtonhustle.sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

/**
 * A class to manage the playing, switching, loading and disposing of sounds and music in the game
 */
public class SoundController implements Disposable {
    private Music gameMusic, menuMusic, currentMusic;
    private final HashMap<Sounds, Music> musicTypes = new HashMap<>();
    private float musicVolume = 0.8f, sfxVolume = 0.8f;


    /**
     * Call to load sounds and initialise a class to switch sounds
     */
    public SoundController() {
        load();
    }

    /**
     * Loads game music and sound effects
     */
    private void load() {
        menuMusic = Gdx.audio.newMusic(Gdx.files.internal("Sound/Music/music_zapsplat_easy_cheesy.mp3"));
        menuMusic.setLooping(true);
        menuMusic.setVolume(musicVolume);



        musicTypes.put(Sounds.MENU, menuMusic);
//        musicTypes.put(Sounds.GAME, gameMusic);
    }


    /**
     * Resumes playback of the current music track
     */
    public void playMusic() {

    }

    /**
     * Pauses the current music track
     */
    public void pauseMusic() {

    }

    /**
     * Switches the current music track to the one provided
     * @param sound An enum value from Sounds
     */
    public void setMusic(Sounds sound) {
        if (musicTypes.containsKey(sound)) {
            if (currentMusic == null) {
                // No music set yet
                currentMusic = musicTypes.get(sound);
                currentMusic.play();
            } else if (currentMusic != musicTypes.get(sound)) {
                // Switch music playing
                currentMusic.stop();
                currentMusic = musicTypes.get(sound);
                currentMusic.play();
            }
        } else if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }

    /**
     * Sets the volume of in game music
     * @param volume The float value to set
     */
    public void setMusicVolume(float volume) {
        musicVolume = volume;
    }

    /**
     * Sets the volume of game sound effects
     * @param volume The float value to set
     */
    public void setSfxVolume(float volume) {
        sfxVolume = volume;
    }


    @Override
    public void dispose() {
        menuMusic.dispose();
    }


}
