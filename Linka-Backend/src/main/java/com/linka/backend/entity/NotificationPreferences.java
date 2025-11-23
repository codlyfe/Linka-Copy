package com.linka.backend.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class NotificationPreferences {
    
    @JsonProperty("email_notifications")
    private boolean emailNotifications = true;
    
    @JsonProperty("sms_notifications")
    private boolean smsNotifications = true;
    
    @JsonProperty("push_notifications")
    private boolean pushNotifications = true;
    
    @JsonProperty("marketing_emails")
    private boolean marketingEmails = true;
    
    @JsonProperty("transaction_updates")
    private boolean transactionUpdates = true;
    
    @JsonProperty("new_messages")
    private boolean newMessages = true;
    
    @JsonProperty("price_alerts")
    private boolean priceAlerts = true;
    
    @JsonProperty("listing_updates")
    private boolean listingUpdates = true;
    
    @JsonProperty("security_alerts")
    private boolean securityAlerts = true;
    
    @JsonProperty("weekly_digest")
    private boolean weeklyDigest = true;
    
    @JsonProperty("quiet_hours_start")
    private String quietHoursStart = "22:00";
    
    @JsonProperty("quiet_hours_end")
    private String quietHoursEnd = "08:00";
    
    @JsonProperty("preferred_language")
    private String preferredLanguage = "en";
    
    @JsonProperty("notification_frequency")
    private NotificationFrequency notificationFrequency = NotificationFrequency.IMMEDIATE;
    
    @JsonProperty("category_preferences")
    private Map<String, Boolean> categoryPreferences;
    
    public enum NotificationFrequency {
        IMMEDIATE,
        HOURLY_DIGEST,
        DAILY_DIGEST,
        WEEKLY_DIGEST,
        NEVER
    }
    
    // Constructors
    public NotificationPreferences() {}
    
    // Getters and Setters
    public boolean isEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(boolean emailNotifications) { this.emailNotifications = emailNotifications; }
    
    public boolean isSmsNotifications() { return smsNotifications; }
    public void setSmsNotifications(boolean smsNotifications) { this.smsNotifications = smsNotifications; }
    
    public boolean isPushNotifications() { return pushNotifications; }
    public void setPushNotifications(boolean pushNotifications) { this.pushNotifications = pushNotifications; }
    
    public boolean isMarketingEmails() { return marketingEmails; }
    public void setMarketingEmails(boolean marketingEmails) { this.marketingEmails = marketingEmails; }
    
    public boolean isTransactionUpdates() { return transactionUpdates; }
    public void setTransactionUpdates(boolean transactionUpdates) { this.transactionUpdates = transactionUpdates; }
    
    public boolean isNewMessages() { return newMessages; }
    public void setNewMessages(boolean newMessages) { this.newMessages = newMessages; }
    
    public boolean isPriceAlerts() { return priceAlerts; }
    public void setPriceAlerts(boolean priceAlerts) { this.priceAlerts = priceAlerts; }
    
    public boolean isListingUpdates() { return listingUpdates; }
    public void setListingUpdates(boolean listingUpdates) { this.listingUpdates = listingUpdates; }
    
    public boolean isSecurityAlerts() { return securityAlerts; }
    public void setSecurityAlerts(boolean securityAlerts) { this.securityAlerts = securityAlerts; }
    
    public boolean isWeeklyDigest() { return weeklyDigest; }
    public void setWeeklyDigest(boolean weeklyDigest) { this.weeklyDigest = weeklyDigest; }
    
    public String getQuietHoursStart() { return quietHoursStart; }
    public void setQuietHoursStart(String quietHoursStart) { this.quietHoursStart = quietHoursStart; }
    
    public String getQuietHoursEnd() { return quietHoursEnd; }
    public void setQuietHoursEnd(String quietHoursEnd) { this.quietHoursEnd = quietHoursEnd; }
    
    public String getPreferredLanguage() { return preferredLanguage; }
    public void setPreferredLanguage(String preferredLanguage) { this.preferredLanguage = preferredLanguage; }
    
    public NotificationFrequency getNotificationFrequency() { return notificationFrequency; }
    public void setNotificationFrequency(NotificationFrequency notificationFrequency) { this.notificationFrequency = notificationFrequency; }
    
    public Map<String, Boolean> getCategoryPreferences() { return categoryPreferences; }
    public void setCategoryPreferences(Map<String, Boolean> categoryPreferences) { this.categoryPreferences = categoryPreferences; }
    
    public boolean isQuietHoursActive() {
        // Simple implementation - in reality this would be more sophisticated
        String currentTime = java.time.LocalTime.now().toString();
        return currentTime.compareTo(quietHoursStart) >= 0 && currentTime.compareTo(quietHoursEnd) <= 0;
    }
}