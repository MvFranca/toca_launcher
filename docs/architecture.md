# 🦊 Arquitetura do Projeto — Toca Launcher

> Este documento descreve a arquitetura proposta para o aplicativo Android da **Toca**, um launcher educativo gamificado voltado para crianças.

O objetivo desta arquitetura é permitir que o projeto cresça durante anos mantendo:

* Baixo acoplamento
* Alta coesão
* Fácil manutenção
* Escalabilidade
* Testabilidade
* Separação clara de responsabilidades

---

# Filosofia

A Toca não é apenas um aplicativo Android.

Ela possui três grandes responsabilidades:

* Um **Launcher Android**
* Uma **plataforma de aprendizado**
* Um **jogo gamificado**

Por isso, a arquitetura precisa refletir essas responsabilidades desde o início.

---

# Arquitetura Geral

```text
app
│
├── core
├── engine
├── feature
├── services
└── build-logic
```

Cada módulo possui uma responsabilidade única.

---

# app

Responsável apenas por iniciar a aplicação.

Contém:

* MainActivity
* Application
* Navegação inicial
* Configuração do Hilt
* Tema global

Nunca deve conter regras de negócio.

---

# core

Tudo que pode ser reutilizado por qualquer feature.

```text
core
├── analytics
├── common
├── database
├── datastore
├── designsystem
├── device
├── education
├── gamification
├── launcher
├── model
├── navigation
├── network
├── notification
├── permissions
├── security
├── testing
├── ui
└── utils
```

---

## core/designsystem

Nosso Design System.

Responsável por:

* Colors
* Typography
* Icons
* Components
* Theme
* Spacing
* Shapes

Nenhuma feature cria seus próprios componentes visuais.

Tudo deve nascer aqui.

---

## core/ui

Componentes reutilizáveis.

Exemplos:

```text
Button
Card
FoxAvatar
ProgressCard
MissionCard
TopBar
BottomBar
Dialogs
ProgressIndicator
```

---

## core/model

Apenas modelos.

Exemplo:

```kotlin
User
Child
Parent
Mission
Reward
Achievement
Question
InstalledApp
Device
```

Sem lógica.

---

## core/network

Responsável por:

* Retrofit
* APIs
* DTOs
* Interceptors
* Token
* Serialização

---

## core/database

Responsável pelo Room.

Contém:

* Entities
* DAO
* Database

---

## core/datastore

Responsável por preferências.

Exemplo:

* Tema
* Token
* Idioma
* Configurações
* Primeiro acesso

---

## core/navigation

Centraliza toda navegação do aplicativo.

Nenhuma feature conhece rotas diretamente.

---

## core/permissions

Todo gerenciamento de permissões.

Exemplos:

* Accessibility
* Usage Stats
* Notification
* Overlay
* Device Owner
* Battery Optimization

---

## core/security

Tudo relacionado à segurança.

Exemplo:

* JWT
* Android Keystore
* Biometria
* Criptografia

---

## core/device

Abstrações do Android.

Exemplo:

```text
Installed Apps

Battery

Storage

Connectivity

Manufacturer

Screen

Memory

Device Info
```

---

## core/launcher

Abstrações do Launcher.

Nenhuma feature acessa PackageManager diretamente.

Exemplo:

```text
LauncherRepository

InstalledAppsRepository

AppLauncher

ShortcutManager

WidgetManager
```

---

## core/gamification

Modelos e contratos da gamificação.

---

## core/education

Modelos relacionados ao aprendizado.

---

# engine

Os engines são o coração da aplicação.

Toda regra de negócio fica aqui.

```text
engine
├── launcher-engine
├── unlock-engine
├── gamification-engine
├── education-engine
├── rewards-engine
├── personalization-engine
└── analytics-engine
```

Esses módulos NÃO conhecem:

* Compose
* Activities
* Fragments
* Navigation

Eles apenas recebem dados e retornam resultados.

---

## launcher-engine

Responsável por:

* abrir aplicativos
* bloquear aplicativos
* verificar permissões
* liberar acesso

---

## unlock-engine

Toda lógica de desbloqueio.

Exemplo:

```
Matemática concluída

↓

10 pontos

↓

desbloquear YouTube

↓

30 minutos
```

---

## gamification-engine

Responsável por:

* XP
* Levels
* Missões
* Badges
* Inventário
* Sequências
* Pontuação

---

## education-engine

Toda inteligência do aprendizado.

Exemplo:

* recomendações
* dificuldade
* progresso
* adaptação

---

## rewards-engine

Calcula recompensas.

---

## personalization-engine

Gerencia toda evolução da toca.

Exemplo:

* móveis
* plantas
* decoração
* mascote
* temas

---

# feature

Cada funcionalidade vive isoladamente.

```text
feature
├── home
├── explore
├── learn
├── missions
├── achievements
├── rewards
├── inventory
├── decorations
├── profile
├── parents
├── onboarding
└── settings
```

Cada feature possui sua própria arquitetura.

Exemplo:

```text
feature/home

presentation/
domain/
data/
di/
```

---

## presentation

Interface.

Contém:

```text
Screen

ViewModel

UiState

UiAction

UiEvent

Components
```

---

## domain

Casos de uso.

Interfaces.

Regras específicas da feature.

---

## data

Implementações.

Repositories.

Data Sources.

---

## di

Injeção de dependência da feature.

---

# services

Componentes Android que vivem fora da interface.

```text
services
├── accessibility
├── launcher
├── notifications
├── sync
├── usage
└── boot
```

---

## accessibility

AccessibilityService.

Detecta aplicativos abertos.

Executa regras.

---

## launcher

Responsável pelo comportamento do Launcher.

---

## usage

Monitora tempo de uso.

---

## notifications

Push.

Lembretes.

Missões.

---

## sync

WorkManager.

Sincronização.

---

## boot

BootReceiver.

Inicialização automática.

---

# build-logic

Convenções do Gradle.

Plugins.

Version Catalog.

Configurações compartilhadas.

---

# Fluxo de Dependências

```text
Feature

↓

Engine

↓

Core

↓

Android
```

Nunca o contrário.

As Features nunca devem depender umas das outras.

Toda comunicação acontece através de interfaces.

---

# Princípios

## Single Responsibility

Cada módulo possui apenas uma responsabilidade.

---

## Feature First

Cada funcionalidade vive isoladamente.

---

## Clean Architecture

A UI nunca conhece regras de negócio.

---

## Dependency Inversion

Toda dependência importante deve ser uma interface.

---

## Testabilidade

Engines devem ser totalmente testáveis sem Android.

---

## Reutilização

Tudo reutilizável deve morar em Core.

---

# Tecnologias

## UI

* Kotlin
* Jetpack Compose
* Material 3
* Navigation Compose

---

## Injeção de Dependência

* Hilt

---

## Persistência

* Room
* DataStore

---

## Rede

* Retrofit
* OkHttp
* Kotlin Serialization

---

## Assíncrono

* Coroutines
* Flow
* StateFlow

---

## Animações

* Rive
* Lottie

---

## Backend

* Spring Boot
* PostgreSQL
* Redis

---

# Referências

## Arquitetura Android

https://developer.android.com/topic/architecture

---

## Modularização

https://developer.android.com/topic/modularization

---

## Recomendações Oficiais

https://developer.android.com/topic/architecture/recommendations

---

## Compose

https://developer.android.com/jetpack/compose

---

## Now in Android

https://github.com/android/nowinandroid

---

# Objetivo

Criar uma base sólida para que a **Toca** possa evoluir de um launcher educativo para um ecossistema completo de aprendizagem infantil, mantendo uma arquitetura limpa, escalável e preparada para novos módulos, funcionalidades e plataformas.
