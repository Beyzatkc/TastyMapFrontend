package org.beem.tastymap.domain.usecase

import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ErrorType
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.SubscribersRepository
import org.beem.tastymap.domain.model.RelationStatus

class ToggleFollowUseCase(
    private val subscribersRepository: SubscribersRepository,
    private val userManager: UserManager
) {
    sealed interface Action {
        data class ToggleFollow(val currentStatus: RelationStatus) : Action
        data object AcceptRequest : Action
        data object RejectRequest : Action
    }

    suspend operator fun invoke(targetUserId: Long, action: Action): ResultWrapper<Unit> {
        val myId = userManager.getUserId()
            ?: return ResultWrapper.Error("Kullanıcı oturumu bulunamadı.", ErrorType.UNAUTHORIZED)

        return when (action) {
            is Action.ToggleFollow -> {
                when (action.currentStatus) {
                    RelationStatus.FOLLOWING, RelationStatus.PENDING -> {
                        subscribersRepository.unSubscribe(targetUserId = targetUserId, myUserId = myId)
                    }
                    RelationStatus.NOT_FOLLOWING, RelationStatus.FOLLOW_BACK -> {
                        when (val result = subscribersRepository.subscribe(targetUserId = targetUserId, myUserId = myId)) {
                            is ResultWrapper.Success -> ResultWrapper.Success(Unit)
                            is ResultWrapper.Error -> ResultWrapper.Error(result.message, result.type)
                        }
                    }
                    RelationStatus.SELF -> ResultWrapper.Success(Unit)
                }
            }
            is Action.AcceptRequest -> {
                subscribersRepository.acceptSubscribeRequest(requesterId = targetUserId, myUserId = myId)
            }
            is Action.RejectRequest -> {
                subscribersRepository.rejectSubscribeRequest(requesterId = targetUserId)
            }
        }
    }
}