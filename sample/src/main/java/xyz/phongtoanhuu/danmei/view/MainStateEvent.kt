package xyz.phongtoanhuu.danmei.view

sealed class MainStateEvent {

    class GetCategories : MainStateEvent()

    class RestoreCategoriesFromCache : MainStateEvent()

    class None: MainStateEvent()
}