package xyz.phongtoanhuu.danmei.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import xyz.phongtoanhuu.danmei.base.BaseViewModel
import xyz.phongtoanhuu.danmei.repository.MainRepository
import xyz.phongtoanhuu.danmei.utils.AbsentLiveData
import xyz.phongtoanhuu.danmei.utils.DataState
import xyz.phongtoanhuu.danmei.utils.Loading
import xyz.phongtoanhuu.danmei.view.MainStateEvent
import xyz.phongtoanhuu.danmei.view.MainViewState

class MainViewModel (private val mainRepository: MainRepository): BaseViewModel<MainStateEvent, MainViewState>() {

    override fun handleStateEvent(stateEvent: MainStateEvent): LiveData<DataState<MainViewState>> {
        return when (stateEvent) {
            is MainStateEvent.GetCategories -> {
                return mainRepository.getCategories()
            }
            is MainStateEvent.RestoreCategoriesFromCache -> {
                return AbsentLiveData.create()
            }
            is MainStateEvent.None -> {
                return liveData {
                    emit(
                        DataState(
                            null,
                            Loading(false),
                            null
                        )
                    )
                }
            }
        }
    }


    override fun initNewViewState(): MainViewState {
        return MainViewState()
    }
}