package org.beem.tastymap.data.repository


import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.network.safeApiCall
import org.beem.tastymap.core.provider.AuthValidator
import org.beem.tastymap.data.model.auth.CommonRequest
import org.beem.tastymap.data.model.auth.NotificationResponse
import org.beem.tastymap.data.model.auth.PasswordRequest
import org.beem.tastymap.data.model.auth.ResetPassword
import org.beem.tastymap.data.model.auth.ResetPasswordResponse
import org.beem.tastymap.data.remote.UserSecurityDataSource

class UserSecurityRepository(
    private val dataSource: UserSecurityDataSource,
) {
    suspend fun resendEmail(commonRequest: CommonRequest): ResultWrapper<Long>{
        return safeApiCall {
            val response = dataSource.resendMail(commonRequest)
            response
        }
    }
    suspend fun resendSecurityMail(deviceId: String): ResultWrapper<String>{
        return safeApiCall {
            val response = dataSource.resendSecurityMail(deviceId)
            response
        }
    }
    suspend fun verifyEmail(token: String): ResultWrapper<Map<String, String>>{
        return safeApiCall {
            val response = dataSource.verifyEmail(token)
            response
        }
    }
    suspend fun isUsedNotification(deviceId: String): ResultWrapper<NotificationResponse>{
        return safeApiCall {
            dataSource.isUsedNotification(deviceId);
        }
    }

    suspend fun forgotPassword(passwordRequest: PasswordRequest): ResultWrapper<ResetPasswordResponse>{
        return safeApiCall {
            dataSource.forgotPassword(passwordRequest)
        }
    }
    suspend fun resetPassword(passwordRequest: ResetPassword): ResultWrapper<String>{
        return safeApiCall {
            dataSource.resetPassword(passwordRequest)
        }
    }
    suspend fun isEmailUsedByDevice(userId: Long): ResultWrapper<Boolean>{
        return safeApiCall {
            dataSource.isEmailUsedByDevice(userId)
        }
    }
    suspend fun isPasswordUsed(userId: Long): ResultWrapper<Boolean>{
        return safeApiCall {
            dataSource.isPasswordUsedByDevice(userId)
        }
    }


}