package io.codemojo.sdk.facades;

import io.codemojo.sdk.models.GamificationAchievement;

import java.util.Map;

/**
 * Created by shoaib on 24/06/16.
 */
public interface GamificationEarnedEvent {

    void newBadgeUnlocked(int totalPoints, String badgeName);

    void newAchievementUnlocked(int totalAchievements, String achievementName, GamificationAchievement achievement);

    void updatedAchievemenstAvailable(Map<String, GamificationAchievement> achievements);
}
