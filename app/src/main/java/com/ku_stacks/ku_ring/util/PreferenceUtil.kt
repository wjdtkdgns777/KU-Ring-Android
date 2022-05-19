package com.ku_stacks.ku_ring.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext

class PreferenceUtil(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("ku_ring_prefs", Context.MODE_PRIVATE)

    var firstRunFlag: Boolean
        get() = prefs.getBoolean(FIRST_RUN, true)
        set(value) = prefs.edit().putBoolean(FIRST_RUN, value).apply()

    var startDate: String?
        get() = prefs.getString(START_DATE, "")
        set(value) = prefs.edit().putString(START_DATE, value).apply()

    var fcmToken: String?
        get() = prefs.getString(FCM_TOKEN, "")
        set(value) = prefs.edit().putString(FCM_TOKEN, value).apply()

    var subscription: Set<String>?
        get() = prefs.getStringSet(SUBSCRIPTION, emptySet())
        set(stringSet) = prefs.edit().putStringSet(SUBSCRIPTION, stringSet).apply()

    var extNotificationAllowed: Boolean
        get() = prefs.getBoolean(DEFAULT_NOTIFICATION, true)
        set(value) = prefs.edit().putBoolean(DEFAULT_NOTIFICATION, value).apply()

    var campusUserId: String
        get() = prefs.getString(CAMPUS_USER_ID, null) ?: ""
        set(value) = prefs.edit().putString(CAMPUS_USER_ID, value).apply()

    fun deleteStartDate() {
        prefs.edit().remove(START_DATE).apply()
    }

    companion object {
        const val FIRST_RUN = "FIRST_RUN"
        const val START_DATE = "START_DATE"
        const val FCM_TOKEN = "FCM_TOKEN"
        const val SUBSCRIPTION = "SUBSCRIPTION"
        const val DEFAULT_NOTIFICATION = "DEFAULT_NOTIFICATION"
        const val CAMPUS_USER_ID = "CAMPUS_USER_ID"
    }
}