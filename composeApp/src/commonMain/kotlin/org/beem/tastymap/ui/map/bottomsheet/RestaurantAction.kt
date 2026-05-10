package org.beem.tastymap.ui.map.bottomsheet

sealed class RestaurantAction {
    object GetDirections : RestaurantAction()
    object ToggleFavorite : RestaurantAction()
    object Share : RestaurantAction()
}