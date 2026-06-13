package com.example.a211082_drnazatul_project1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.a211082_drnazatul_project1.data.OnRoadUiState

class OnRoadViewModel : ViewModel() {
    var uiState by mutableStateOf(OnRoadUiState())
        private set

    fun updateDestination(newDest: String, startRouting: Boolean = false) {
        uiState = uiState.copy(destination = newDest, isRouting = startRouting)
    }

    fun setRouting(status: Boolean) {
        uiState = uiState.copy(isRouting = status)
    }

    fun toggleSavePlace(placeName: String) {
        val currentList = uiState.savedPlaces.toMutableList()
        if (currentList.contains(placeName)) {
            currentList.remove(placeName)
        } else {
            currentList.add(placeName)
        }
        uiState = uiState.copy(savedPlaces = currentList)
    }

    fun addPlace(name: String) {
        if (name.isNotBlank() && !uiState.savedPlaces.contains(name)) {
            uiState = uiState.copy(savedPlaces = uiState.savedPlaces + name)
        }
    }
}