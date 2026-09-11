package org.beem.tastymap.domain.usecase

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.BlockRepository
import org.beem.tastymap.domain.model.RelationStatus
import org.beem.tastymap.core.local.UserManager
import org.beem.tastymap.core.network.ErrorType


class ToggleBlockUseCase(
    private val blockRepository: BlockRepository,
    private val userManager: UserManager
) {
    suspend operator fun invoke(
        targetUserId: Long,
        isCurrentlyBlocked: Boolean,
        currentRelationStatus: RelationStatus = RelationStatus.NOT_FOLLOWING,
        isFollower: Boolean = false
    ): ResultWrapper<Unit> {
        val myUserId = userManager.getUserId()
            ?: return ResultWrapper.Error("Kullanıcı oturumu bulunamadı.", ErrorType.UNAUTHORIZED)

        return if (isCurrentlyBlocked) {
            blockRepository.unblockUser(targetUserId)
        } else {
            blockRepository.blockUser(
                userId = targetUserId,
                myUserId = myUserId,
                currentRelationStatus = currentRelationStatus,
                isFollower = isFollower
            )
        }
    }
}