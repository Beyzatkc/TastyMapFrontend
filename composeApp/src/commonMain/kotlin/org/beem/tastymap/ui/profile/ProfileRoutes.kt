package org.beem.tastymap.ui.profile

import kotlinx.serialization.Serializable
import org.beem.tastymap.ui.profile.subscribers.SubscriberListType

@Serializable data object HealthWizardRoute

@Serializable data object OnBoardingRoute

@Serializable data object EditProfileRoute
@Serializable data object NotificationRoute
@Serializable data object ActiveDevicesRoute
@Serializable data object BlockedUsersRoute
@Serializable data object AccountDeactivatedRoute
@Serializable data object DeleteAccountRoute
@Serializable data object EditHealthRoute
@Serializable data object PrivacyPolicyRoute
@Serializable data object SettingsRoute
@Serializable data object VisitRoute
@Serializable data class ProfileRoute(val userId: Long)
@Serializable
data class SubscribersListRoute(val userId: Long, val initialTab: SubscriberListType)