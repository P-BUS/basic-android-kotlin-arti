package com.example.arti.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.example.arti.data.database.BooksDatabase.Companion.getDatabase
import com.example.arti.data.model.OpenLibraryBook
import com.example.arti.data.repository.BooksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException


enum class BooksApiStatus { LOADING, ERROR, DONE }

const val TAG = "BooksViewModel"

class BooksViewModel(
    application: Application
//    private val booksDao: BooksDao
) : AndroidViewModel(application) {

    private val booksRepository = BooksRepository(getDatabase(application))

    val books = booksRepository.books

    private val _currentBook = MutableLiveData<OpenLibraryBook>()
    val currentBook: LiveData<OpenLibraryBook> = _currentBook

    /**
    * Store the status of updating database from web service
    */
    private val _status = MutableStateFlow(BooksApiStatus.DONE)
    val status: StateFlow<BooksApiStatus> = _status.asStateFlow()

    init {
        refreshDataFromRepository()
    }

    // TODO: Remake error status instead loading waiting
    private fun refreshDataFromRepository() {
        viewModelScope.launch {
            _status.value = BooksApiStatus.LOADING
            try {
                booksRepository.refreshBooks()
                _status.value = BooksApiStatus.DONE
            }
            catch (networkError: IOException) {
                _status.value = BooksApiStatus.ERROR
                Log.e(TAG, "IO Exception, you might not have internet connection")
            }
        }
    }

    // Updates current book LiveData property
    fun updateCurrentBook(book: OpenLibraryBook) {
        _currentBook.value = book
    }
}

class BooksViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BooksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BooksViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }
}
