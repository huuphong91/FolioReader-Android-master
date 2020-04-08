package com.loiphong.truyendammyfull.viewmodel

import androidx.lifecycle.*
import com.loiphong.truyendammyfull.entity.CategoryEntity
import com.loiphong.truyendammyfull.repository.MainRepository
import com.loiphong.truyendammyfull.utils.Resource

class MainViewModel(private val mainRepository: MainRepository) :
    ViewModel() {

    val categoryList: LiveData<Resource<List<CategoryEntity>>> =
        Transformations.switchMap(getCategoriesCount()){
            getCategories()
        }

    private fun getCategories(): LiveData<Resource<List<CategoryEntity>>> {
        return mainRepository.getCategories()
    }

    fun getCategoriesCount(): LiveData<Resource<Int>> {
        return mainRepository.categoriesCount()
    }

    fun downloadEpub(categoryEntity: CategoryEntity): LiveData<Resource<CategoryEntity>> {
        return mainRepository.downloadEpub(categoryEntity)
    }

    fun updateCategoryEntity(categoryEntity: CategoryEntity) {
        mainRepository.updateCategory(categoryEntity)
    }
}