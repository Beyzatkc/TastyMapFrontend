package org.beem.tastymap.ui.profile

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.beem.tastymap.ui.profile.subscribers.SubscriberListType

@Serializable
@SerialName("healthwizard")
data object HealthWizardRoute

@Serializable
@SerialName("onboarding")
data object OnBoardingRoute

@Serializable
@SerialName("editprofile")
data object EditProfileRoute

@Serializable
@SerialName("notifications")
data object NotificationRoute

@Serializable
@SerialName("settings/activedevices")
data object ActiveDevicesRoute

@Serializable
@SerialName("settings/blockedusers")
data object BlockedUsersRoute

@Serializable
@SerialName("settings/accountdeactivated")
data object AccountDeactivatedRoute

@Serializable
@SerialName("settings/deleteaccount")
data object DeleteAccountRoute

@Serializable
@SerialName("settings/edithealth")
data object EditHealthRoute

@Serializable
@SerialName("settings/privacypolicy")
data object PrivacyPolicyRoute

@Serializable
@SerialName("settings/visits")
data object VisitRoute

@Serializable
@SerialName("settings")
data object SettingsRoute


@Serializable
@SerialName("profile")
data class ProfileRoute(val userId: Long, val username: String?)

@Serializable
@SerialName("myprofilee")
data object MyProfileFullScreenRoute


@Serializable
@SerialName("subscribers")
data class SubscribersListRoute(val userId: Long, val initialTab: SubscriberListType)