package io.element.android.x

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo
import io.element.android.x.core.di.bindings
import io.element.android.x.destinations.BugReportScreenNavigationDestination
import io.element.android.x.destinations.ChangeServerScreenNavigationDestination
import io.element.android.x.destinations.LoginScreenNavigationDestination
import io.element.android.x.destinations.MessagesScreenNavigationDestination
import io.element.android.x.destinations.OnBoardingScreenNavigationDestination
import io.element.android.x.destinations.PreferencesScreenNavigationDestination
import io.element.android.x.destinations.RoomListScreenNavigationDestination
import io.element.android.x.di.AppBindings
import io.element.android.x.features.login.LoginScreen
import io.element.android.x.features.login.changeserver.ChangeServerScreen
import io.element.android.x.features.messages.MessagesScreen
import io.element.android.x.features.onboarding.OnBoardingScreen
import io.element.android.x.features.preferences.PreferencesScreen
import io.element.android.x.features.rageshake.bugreport.BugReportScreen
import io.element.android.x.features.roomlist.RoomListScreen
import io.element.android.x.matrix.core.RoomId

@Destination
@Composable
fun OnBoardingScreenNavigation(navigator: DestinationsNavigator) {
    OnBoardingScreen(
        onSignUp = {
            // TODO
        },
        onSignIn = {
            navigator.navigate(LoginScreenNavigationDestination)
        }
    )
}

@Destination
@Composable
fun LoginScreenNavigation(navigator: DestinationsNavigator) {
    val sessionComponentsOwner = LocalContext.current.bindings<AppBindings>().sessionComponentsOwner()
    LoginScreen(
        onChangeServer = {
            navigator.navigate(ChangeServerScreenNavigationDestination)
        },
        onLoginWithSuccess = {
            sessionComponentsOwner.create(it)
            navigator.navigate(RoomListScreenNavigationDestination) {
                popUpTo(OnBoardingScreenNavigationDestination) {
                    inclusive = true
                }
            }
        }
    )
}

// TODO Create a subgraph in Login module
@Destination
@Composable
fun ChangeServerScreenNavigation(navigator: DestinationsNavigator) {
    ChangeServerScreen(
        onChangeServerSuccess = {
            navigator.popBackStack()
        }
    )
}

@RootNavGraph(start = true)
@Destination
@Composable
fun RoomListScreenNavigation(navigator: DestinationsNavigator) {
    val sessionComponentsOwner = LocalContext.current.bindings<AppBindings>().sessionComponentsOwner()
    RoomListScreen(
        onRoomClicked = { roomId: RoomId ->
            navigator.navigate(MessagesScreenNavigationDestination(roomId = roomId.value))
        },
        onOpenSettings = {
            navigator.navigate(PreferencesScreenNavigationDestination())
        },
        onSuccessLogout = {
            sessionComponentsOwner.releaseActiveSession()
            navigator.navigate(OnBoardingScreenNavigationDestination) {
                popUpTo(RoomListScreenNavigationDestination) {
                    inclusive = true
                }
            }
        },
        // Tmp entry point
        onOpenRageShake = {
            navigator.navigate(BugReportScreenNavigationDestination)
        }
    )
}

@Destination
@Composable
fun MessagesScreenNavigation(roomId: String, navigator: DestinationsNavigator) {
    MessagesScreen(roomId = roomId, onBackPressed = navigator::navigateUp)
}

@Destination
@Composable
fun BugReportScreenNavigation(navigator: DestinationsNavigator) {
    BugReportScreen(
        onDone = navigator::popBackStack
    )
}

@Destination
@Composable
fun PreferencesScreenNavigation(navigator: DestinationsNavigator) {
    PreferencesScreen(
        onBackPressed = navigator::navigateUp
    )
}

