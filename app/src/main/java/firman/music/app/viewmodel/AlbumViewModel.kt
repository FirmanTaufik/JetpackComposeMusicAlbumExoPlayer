package firman.music.app.viewmodel

import firman.music.app.core.source.ApiService
import firman.music.app.core.state.ScreenState
import firman.music.app.model.album.Entry
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@HiltViewModel
class AlbumViewModel @Inject constructor(
    val apiService: ApiService
) : ViewModel() {
    private var _uiState = MutableStateFlow<ScreenState>(ScreenState.OnLoading)
    val uiState = _uiState.asStateFlow()

    @SuppressLint("MutableCollectionMutableState")
    var listSearch = mutableStateOf (emptyList<Entry>() )
    var listSearchResult = mutableStateOf (emptyList<Entry>() )

    init {
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            try {
                _uiState.value = ScreenState.OnLoading
                val result = apiService.getAllPost()

                _uiState.value = ScreenState.OnSuccess(
                    (result.body()?.feed)
                )
                listSearch .value= result.body()?.feed?.entry ?: emptyList()
                listSearchResult.value = listSearch.value
            } catch (e: Exception) {
                _uiState.value = ScreenState.OnError()
            }
        }
    }

    fun searchList(query:String){
        if (query.isEmpty()) listSearchResult.value =listSearch.value
        else {
            val tempList = listSearch.value.filter { it.title.`$t`.contains(query, true) }
            listSearchResult.value =  tempList
        }
    }
}