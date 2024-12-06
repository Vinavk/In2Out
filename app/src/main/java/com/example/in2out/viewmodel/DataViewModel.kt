package com.example.in2out.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.in2out.db.NoteData
import com.example.in2out.repository.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(private var repository: DataRepository) : ViewModel() {

    var mutableStateFlow : MutableStateFlow<List<NoteData>> = MutableStateFlow(emptyList())


    fun savedata(intime: String, outime: String, context: Context) {
        viewModelScope.launch {
        repository.datastore(intime,outime)
            repository.scheduleAlarm(context)
        }
    }

    fun getDataRepo() : MutableStateFlow<List<NoteData>> {
        viewModelScope.launch {
            mutableStateFlow.value = repository.getData()
        }
        return mutableStateFlow
    }

    fun StopAlarm(context: Context){
        repository.stopAlarm(context)
    }

}