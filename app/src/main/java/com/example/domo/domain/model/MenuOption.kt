package com.example.domo.domain.model

enum class MenuOption(val label: String, val route: String) {
    READ("Ler", "read"),
    PLAY("Jogar", "play"),
    CHALLENGES("Desafios", "challenges")
}
