package org.beem.tastymap.ui.profile

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.beem.tastymap.ui.profile.health.HealthWizardScreen
import org.beem.tastymap.ui.profile.health.OnBoardingScreen
import org.beem.tastymap.ui.profile.myprofile.editprofile.EditProfileScreen
import org.beem.tastymap.ui.profile.myprofile.notification.NotificationScreen
import org.beem.tastymap.ui.profile.myprofile.settings.PrivacyPolicyScreen
import org.beem.tastymap.ui.profile.myprofile.settings.SettingsScreen
import org.beem.tastymap.ui.profile.myprofile.settings.activedevices.ActiveDevicesScreen
import org.beem.tastymap.ui.profile.myprofile.settings.blockedusers.BlockedUsersScreen
import org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount.AccountDeactivatedScreen
import org.beem.tastymap.ui.profile.myprofile.settings.deleteaccount.DeleteAccountScreen
import org.beem.tastymap.ui.profile.myprofile.settings.edithealth.EditHealthScreen
import org.beem.tastymap.ui.profile.myprofile.visit.VisitScreen
import org.beem.tastymap.ui.profile.otherprofile.ProfileScreen
import org.beem.tastymap.ui.profile.subscribers.SubscriberListNavType
import org.beem.tastymap.ui.profile.subscribers.SubscriberListType
import org.beem.tastymap.ui.profile.subscribers.SubscribersListScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.profileGraph(navController: NavController) {
    composable<HealthWizardRoute> {
        HealthWizardScreen(navController)
    }
    composable<OnBoardingRoute> {
        OnBoardingScreen(navController)
    }
    composable<EditProfileRoute> {
        EditProfileScreen(navController)
    }
    composable<NotificationRoute> {
        NotificationScreen(navController)
    }
    composable<ActiveDevicesRoute> {
        ActiveDevicesScreen(navController)
    }
    composable<BlockedUsersRoute> {
        BlockedUsersScreen(navController)
    }
    composable<AccountDeactivatedRoute> {
        AccountDeactivatedScreen(navController)
    }
    composable<DeleteAccountRoute> {
        DeleteAccountScreen(navController)
    }
    composable<EditHealthRoute> {
        EditHealthScreen(navController)
    }
    composable<PrivacyPolicyRoute> {
        PrivacyPolicyScreen(navController)
    }
    composable<SettingsRoute> {
        SettingsScreen(navController)
    }
    composable<VisitRoute> {
        VisitScreen(navController)
    }
    composable<ProfileRoute> { backStackEntry->
        val route = backStackEntry.toRoute<ProfileRoute>()
        ProfileScreen(
            userId = route.userId,
            navController = navController
        )
    }
    composable<SubscribersListRoute>(
        typeMap = mapOf(typeOf<SubscriberListType>() to SubscriberListNavType)
    ) { backStackEntry ->
        val route = backStackEntry.toRoute<SubscribersListRoute>()
        SubscribersListScreen(
            userId = route.userId,
            initialTab = route.initialTab,
            navController = navController
        )
    }
}