package org.beem.tastymap.ui.post

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.beem.tastymap.core.network.ResultWrapper
import org.beem.tastymap.core.util.ToastManager
import org.beem.tastymap.data.repository.PostRepository
import org.beem.tastymap.ui.auth.common.AuthEffect

class PostScreenModel(
    private val repository: PostRepository
): ScreenModel{

    private val _effect = Channel<AuthEffect>()
    val effect = _effect.receiveAsFlow()

    fun getPosts() {
        screenModelScope.launch {

            when (val result = repository.getPosts()) {
                is ResultWrapper.Success -> {
                    ToastManager.show("Kayıt başarılı!")
                    _effect.send(AuthEffect.NavigateToLogin)
                }

                is ResultWrapper.Error -> {
                    ToastManager.show(result.message ?: "Kayıt başarısız.")
                }
            }
        }
    }

}