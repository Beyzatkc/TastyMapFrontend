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
@SerialName("activedevices")
data object ActiveDevicesRoute

@Serializable
@SerialName("blockedusers")
data object BlockedUsersRoute

@Serializable
@SerialName("accountdeactivated")
data object AccountDeactivatedRoute

@Serializable
@SerialName("deleteaccount")
data object DeleteAccountRoute

@Serializable
@SerialName("edithealth")
data object EditHealthRoute

@Serializable
@SerialName("privacypolicy")
data object PrivacyPolicyRoute

@Serializable
@SerialName("settings")
data object SettingsRoute

@Serializable
@SerialName("visits")
data object VisitRoute

@Serializable
@SerialName("profile")
data class ProfileRoute(val userId: Long, val username: String)

@Serializable
@SerialName("myprofile")
data object MyProfileFullScreenRoute

@Serializable
@SerialName("subscribers")
data class SubscribersListRoute(val userId: Long, val initialTab: SubscriberListType)