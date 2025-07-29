package com.pqt.arkham.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pqt.arkham.data.entity.ExpansionEntity
import com.pqt.arkham.data.repository.ExpansionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ExpansionViewModel @Inject constructor(
    private val expansionRepository: ExpansionRepository
) : ViewModel() {
    
    private val _expansions = MutableStateFlow<List<ExpansionEntity>>(emptyList())
    val expansions: StateFlow<List<ExpansionEntity>> = _expansions.asStateFlow()
    
    private val _enabledExpansions = MutableStateFlow<List<ExpansionEntity>>(emptyList())
    val enabledExpansions: StateFlow<List<ExpansionEntity>> = _enabledExpansions.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadExpansions()
    }
    
    private fun loadExpansions() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                Timber.d("Loading expansions")
                
                // Collect both flows
                expansionRepository.getAllExpansions().collect { expansions ->
                    _expansions.value = expansions
                    Timber.d("Loaded ${expansions.size} expansions")
                }
                
                expansionRepository.getEnabledExpansions().collect { enabledExpansions ->
                    _enabledExpansions.value = enabledExpansions
                    Timber.d("Loaded ${enabledExpansions.size} enabled expansions")
                }
                
            } catch (e: Exception) {
                Timber.e(e, "Error loading expansions")
                _error.value = "Failed to load expansions: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleExpansion(expansionId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                Timber.d("Toggling expansion: $expansionId -> $isEnabled")
                expansionRepository.updateExpansionEnabled(expansionId, isEnabled)
            } catch (e: Exception) {
                Timber.e(e, "Error toggling expansion")
                _error.value = "Failed to update expansion: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 