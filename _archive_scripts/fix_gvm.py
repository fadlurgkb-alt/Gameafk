import re

with open("app/src/main/java/com/example/ui/game/GameViewModel.kt", "r") as f:
    content = f.read()

# Add _upgradingBuildings
state_flow_decl = """    private val _alertMessage = MutableStateFlow<String?>(null)
    val alertMessage: StateFlow<String?> = _alertMessage.asStateFlow()"""
    
new_state_flow_decl = """    private val _alertMessage = MutableStateFlow<String?>(null)
    val alertMessage: StateFlow<String?> = _alertMessage.asStateFlow()
    
    private val _upgradingBuildings = MutableStateFlow<Map<String, Long>>(emptyMap())
    val upgradingBuildings: StateFlow<Map<String, Long>> = _upgradingBuildings.asStateFlow()"""

if "_upgradingBuildings" not in content:
    content = content.replace(state_flow_decl, new_state_flow_decl)

buy_node_start = """    fun buyNode(id: String) {
        _uiState.update { currentState ->"""
        
buy_node_check = """    fun buyNode(id: String) {
        if (_upgradingBuildings.value.containsKey(id)) {
            _alertMessage.value = "🚧 Bangunan sedang ditingkatkan!"
            return
        }
        
        var isBuildingUpgrade = false
        var targetLevel = 0
        var wasUnlocked = false

        _uiState.update { currentState ->"""

if "var isBuildingUpgrade = false" not in content:
    content = content.replace(buy_node_start, buy_node_check)

with open("app/src/main/java/com/example/ui/game/GameViewModel.kt", "w") as f:
    f.write(content)

