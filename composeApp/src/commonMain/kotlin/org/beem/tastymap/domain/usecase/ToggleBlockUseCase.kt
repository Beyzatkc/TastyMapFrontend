package org.beem.tastymap.domain.usecase

import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.data.repository.BlockRepository
import org.beem.tastymap.domain.model.RelationStatus

class ToggleBlockUseCase(
    private val blockRepository: BlockRepository
) {
    suspend operator fun invoke(
        targetUserId: Long,
        myUserId: Long,
        isCurrentlyBlocked: Boolean,
        currentRelationStatus: RelationStatus = RelationStatus.NOT_FOLLOWING,
        isFollower: Boolean = false
    ): ResultWrapper<Unit> {
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