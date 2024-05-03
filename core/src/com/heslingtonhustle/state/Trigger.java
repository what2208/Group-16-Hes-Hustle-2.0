package com.heslingtonhustle.state;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;

/**
 * A trigger is an object that the player can interact with.
 * This class contains information about a specific trigger loaded from the Tiled
 * map editor.
 * This class contains getters for attributes that Triggers often have,
 * like score, duration, prompt message, etc.
 * Also contains getters to get new map coordinates
 */
public class Trigger {
    // Store the raw map properties
    private final MapProperties mapProperties;

    /**
     * Create a new trigger from a set of mapProperties
     * @param mapProperties A set of mapProperties loaded from a map
     */
    public Trigger(MapProperties mapProperties) {
        this.mapProperties = mapProperties;
    }

    /**
     * A method to get raw map properties from an object's mapProperties
     * @param key Key of the property to get
     * @return The object, null if not found
     */
    public Object get(String key) {
        if (mapProperties.containsKey(key)) {
            return mapProperties.get(key);
        }
        return null;
    }

    /**
     * Forwards the mapProperties.containsKey method forward
     * @param key The key of the property to check for
     * @return True if contained
     */
    public boolean containsKey(String key) {
        return mapProperties.containsKey(key);
    }

    /**
     * @return The name of the activity to perform. Null if none specified.
     */
    public String getActivityName() {
        if (mapProperties.containsKey("activity")) {
            return (String) mapProperties.get("activity");
        }
        return "";
    }

    /**
     * @return The energy cost of the activity, 0 if not specified
     */
    public int getEnergyCost() {
        if (mapProperties.containsKey("energy_cost")) {
            return (int) mapProperties.get("energy_cost");
        }
        return 0;
    }

    /**
     * @return The score that doing the activity gives
     */
    public int getScore() {
        if (mapProperties.containsKey("score")) {
            return (int) mapProperties.get("score");
        }
        return 0;
    }

    /**
     * @return The type of the activity that should be triggered
     */
    public String getType() {
        if (mapProperties.containsKey("type")) {
            return (String) mapProperties.get("type");
        }
        return  null;
    }

    /**
     * @return True if the user is able to sleep using this trigger
     */
    public boolean canSleep() {
        if (mapProperties.containsKey("sleep")) {
            return (boolean) mapProperties.get("sleep");
        }
        return false;
    }


    /**
     * @return The message to display upon successful completion of the activity
     */
    public String getSuccessMessage() {
        if (mapProperties.containsKey("success_message")) {
            return (String) mapProperties.get("success_message");
        }
        return null;
    }

    /**
     * @return The prompt message that should appear before a user
     * completes an action
     */
    public String getPromptMessage() {
        if (mapProperties.containsKey("prompt_message")) {
            return (String)mapProperties.get("prompt_message");
        }
        return String.format("Do you want to %s?", getActivityName());
    }

    /**
     * @return The name of the new map if this trigger contains
     * the "new_map" property
     */
    public String getNewMap() {
        if (mapProperties.containsKey("new_map")) {
            return (String) mapProperties.get("new_map");
        }
        return null;
    }

    /**
     * @return A vector containing the new map coordinates if two
     * new map coordinates are listed in mapProperties
     */
    public Vector2 getNewMapCoords() {
        if (mapProperties.containsKey("new_map_x") && mapProperties.containsKey("new_map_y")) {
            return new Vector2((int) mapProperties.get("new_map_x"), (int)mapProperties.get("new_map_y"));
        }
        return null;
    }

    /**
     * Creates a new instance of Activity from the information stored in this
     * trigger
     * @return The new activity class
     */
    public Activity toActivity() {
        return new Activity(
                getActivityName(),
                getType(),
                (int) mapProperties.get("score"),
                (int) mapProperties.get("energy_cost"),
                (int) mapProperties.get("hours"),
                (int) mapProperties.get("limit")
        );
    }
}
