package xyz.phongtoanhuu.danmei.viewmodel

import androidx.lifecycle.*
import xyz.phongtoanhuu.danmei.entity.CategoryEntity
import xyz.phongtoanhuu.danmei.repository.MainRepository
import xyz.phongtoanhuu.danmei.utils.Resource

class MainViewModel(private val mainRepository: MainRepository) :
    ViewModel() {

    fun getCategories(): LiveData<Resource<List<CategoryEntity>>> {
        return mainRepository.getCategories()
    }

    fun getCategoriesCount() : LiveData<Resource<Int>>{
        return mainRepository.categoriesCount()
    }

    fun downloadEpub(categoryEntity: CategoryEntity) : LiveData<Resource<CategoryEntity>> {
        return mainRepository.downloadEpub(categoryEntity)
    }
}