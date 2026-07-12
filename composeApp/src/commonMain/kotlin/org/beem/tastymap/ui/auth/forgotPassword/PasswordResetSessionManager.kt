package org.beem.tastymap.ui.auth.forgotPassword

class PasswordResetSessionManager {

    data class PasswordResetContext(
        val userId: Long,
        val deviceId: String
    )

    private var context: PasswordResetContext? = null


    fun save(
        userId: Long,
        deviceId: String
    ){
        context = PasswordResetContext(
            userId,
            deviceId
        )
    }


    fun get(): PasswordResetContext? {
        return context
    }


    fun clear(){
        context = null
    }
}