package chat.rocket.core.internal.realtime

import chat.rocket.common.model.UserStatus
import chat.rocket.core.RocketChatClient
import chat.rocket.core.internal.realtime.message.defaultStatusMessage
import chat.rocket.core.internal.realtime.message.temporaryStatusMessage
import chat.rocket.core.internal.realtime.message.userDataChangesMessage
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.withContext

suspend fun RocketChatClient.setDefaultStatus(status: UserStatus) = withContext(CommonPool) {
    socket.send(defaultStatusMessage(socket.generateId(), status))
}

suspend fun RocketChatClient.setTemporaryStatus(status: UserStatus) = withContext(CommonPool) {
    when {
        (status is UserStatus.Online || status is UserStatus.Away) -> {
            socket.send(temporaryStatusMessage(socket.generateId(), status))
        }
        else -> {
            logger.warn { "Only \"UserStatus.Online\" and \"UserStatus.Away\" are accepted as temporary status" }
        }
    }
}

fun RocketChatClient.subscribeUserDataChanges(callback: (Boolean, String) -> Unit): String {
    with(socket) {
        val id = generateId()
        send(userDataChangesMessage(id))
        subscriptionsMap[id] = callback
        return id
    }
}